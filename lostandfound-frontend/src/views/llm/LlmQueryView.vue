<template>
  <div style="max-width:1000px;margin:24px auto;padding:0 16px">
    <el-button @click="router.push('/home')" style="margin-bottom:16px">返回首页</el-button>

    <el-card>
      <template #header>自然语言查询（LLM）</template>

      <el-input
        v-model="query"
        type="textarea"
        :rows="4"
        placeholder="例如：查找最近在图书馆丢失的电子产品"
      />

      <div style="margin-top:12px;display:flex;gap:8px">
        <el-button type="primary" :loading="loading" @click="handleRun">执行查询</el-button>
        <el-button @click="query = ''">清空</el-button>
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
        <el-descriptions-item label="生成 SQL">
          <pre style="white-space:pre-wrap;margin:0">{{ sqlText }}</pre>
        </el-descriptions-item>
        <el-descriptions-item label="结果条数">{{ resultCount }}</el-descriptions-item>
      </el-descriptions>

      <el-table v-if="rows.length > 0" :data="rows" style="margin-top:12px" size="small">
        <el-table-column prop="item_id" label="ID" width="80" />
        <el-table-column prop="name" label="标题" min-width="160" />
        <el-table-column prop="category" label="分类" width="120" />
        <el-table-column prop="location" label="地点" width="140" />
        <el-table-column prop="date" label="日期" width="120" />
        <el-table-column prop="status" label="类型" width="100" />
      </el-table>

      <el-empty v-if="!loading && sqlText && rows.length === 0" description="查询成功，但没有匹配数据" />
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
    errorMsg.value = '请输入查询语句'
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
    errorMsg.value = 'LLM 查询失败，请确认 Python LLM 服务已启动（默认 5000 端口）'
  } finally {
    loading.value = false
  }
}
</script>
