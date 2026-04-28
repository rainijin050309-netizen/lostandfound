<template>
  <div style="max-width:1000px;margin:24px auto;padding:0 16px">
    <el-button @click="router.push('/home')" style="margin-bottom:16px">Back to Home</el-button>

    <el-card>
      <template #header>Natural Language Query (LLM)</template>

      <el-input
        v-model="query"
        type="textarea"
        :rows="4"
        placeholder="e.g., Find electronic devices lost recently in the library"
      />

      <div style="margin-top:12px;display:flex;gap:8px">
        <el-button type="primary" :loading="loading" @click="handleRun">Execute Query</el-button>
        <el-button @click="query = ''">Clear</el-button>
      </div>

      <el-divider />

      <el-alert
        v-if="errorMsg"
        :title="errorMsg"
        type="error"
        show-icon
        :closable="false"
        style="margin-bottom:12px"
      />

      <el-descriptions v-if="sqlText" :column="1" border>
        <el-descriptions-item label="Generated SQL">
          <pre style="white-space:pre-wrap;margin:0">{{ sqlText }}</pre>
        </el-descriptions-item>
        <el-descriptions-item label="Result Count">{{ resultCount }}</el-descriptions-item>
      </el-descriptions>

      <el-table v-if="rows.length > 0" :data="rows" style="margin-top:12px" size="small">
        <el-table-column prop="item_id" label="ID" width="80" />
        <el-table-column prop="name" label="Title" min-width="160" />
        <el-table-column prop="category" label="Category" width="120" />
        <el-table-column prop="location" label="Location" width="140" />
        <el-table-column prop="date" label="Date" width="120" />
        <el-table-column prop="status" label="Type" width="100" />
      </el-table>

      <el-empty v-if="!loading && sqlText && rows.length === 0" description="Query successful, but no matching data found" />
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { runLlmQuery } from '../../api/llm'

const router = useRouter()

const query = ref('')
const loading = ref(false)
const errorMsg = ref('')
const sqlText = ref('')
const resultCount = ref(0)
const rows = ref([])

const handleRun = async () => {
  if (!query.value.trim()) {
    errorMsg.value = 'Please insert the query statement.'
    return
  }

  loading.value = true
  errorMsg.value = ''
  sqlText.value = ''
  rows.value = []
  resultCount.value = 0

  try {
    const res = await runLlmQuery(query.value)
    const data = res.data || {}
    sqlText.value = data.sql || ''
    rows.value = data.results || []
    resultCount.value = Number(data.count || 0)
  } catch (_error) {
    errorMsg.value = 'LLM query failed. Please confirm that the Python LLM service is running (default port 5000)'
  } finally {
    loading.value = false
  }
}
</script>
