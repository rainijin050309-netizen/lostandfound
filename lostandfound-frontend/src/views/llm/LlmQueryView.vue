<template>
  <div style="max-width:1000px;margin:24px auto;padding:0 16px">
    <el-button @click="router.push('/home')" style="margin-bottom:16px">{{ $t('llm.back') }}</el-button>

    <el-card>
      <template #header>{{ $t('llm.title') }}</template>

      <el-input
        v-model="query"
        type="textarea"
        :rows="4"
        :placeholder="$t('llm.placeholder')"
      />

      <div style="margin-top:12px;display:flex;gap:8px">
        <el-button type="primary" :loading="loading" @click="handleRun">{{ $t('llm.run') }}</el-button>
        <el-button @click="query = ''">{{ $t('llm.clear') }}</el-button>
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
        <el-descriptions-item :label="$t('llm.generatedSql')">
          <pre style="white-space:pre-wrap;margin:0">{{ sqlText }}</pre>
        </el-descriptions-item>
        <el-descriptions-item :label="$t('llm.resultCount')">{{ resultCount }}</el-descriptions-item>
      </el-descriptions>

      <el-table v-if="rows.length > 0" :data="rows" style="margin-top:12px" size="small">
        <el-table-column prop="item_id" :label="$t('llm.colId')" width="80" />
        <el-table-column prop="name" :label="$t('llm.colTitle')" min-width="160" />
        <el-table-column prop="category" :label="$t('llm.colCategory')" width="120" />
        <el-table-column prop="location" :label="$t('llm.colLocation')" width="140" />
        <el-table-column prop="date" :label="$t('llm.colDate')" width="120" />
        <el-table-column prop="status" :label="$t('llm.colType')" width="100" />
      </el-table>

      <el-empty v-if="!loading && sqlText && rows.length === 0" :description="$t('llm.empty')" />
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { runLlmQuery } from '../../api/llm'

const { t } = useI18n()
const router = useRouter()

const query = ref('')
const loading = ref(false)
const errorMsg = ref('')
const sqlText = ref('')
const resultCount = ref(0)
const rows = ref([])

const handleRun = async () => {
  if (!query.value.trim()) {
    errorMsg.value = t('llm.error')
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
    errorMsg.value = t('llm.error')
  } finally {
    loading.value = false
  }
}
</script>
