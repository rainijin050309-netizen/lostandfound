<template>
  <div style="max-width:1100px;margin:24px auto;padding:0 16px">
    <el-button @click="router.push('/home')" style="margin-bottom:16px">返回首页</el-button>

    <el-card style="margin-bottom:16px">
      <template #header>系统统计总览</template>
      <el-row :gutter="12" v-loading="loadingOverview">
        <el-col :span="6"><el-statistic title="失物条目" :value="toNum(overview.total_lost_items)" /></el-col>
        <el-col :span="6"><el-statistic title="招领条目" :value="toNum(overview.total_found_items)" /></el-col>
        <el-col :span="6"><el-statistic title="匹配数" :value="toNum(overview.total_matches)" /></el-col>
        <el-col :span="6"><el-statistic title="认领总数" :value="toNum(overview.total_claims)" /></el-col>
      </el-row>
      <el-row :gutter="12" style="margin-top:10px" v-loading="loadingOverview">
        <el-col :span="8"><el-statistic title="已通过" :value="toNum(overview.approved_claims)" /></el-col>
        <el-col :span="8"><el-statistic title="待处理" :value="toNum(overview.pending_claims)" /></el-col>
        <el-col :span="8"><el-statistic title="已拒绝" :value="toNum(overview.rejected_claims)" /></el-col>
      </el-row>
    </el-card>

    <el-row :gutter="16">
      <el-col :span="12">
        <el-card>
          <template #header>分类分布（失物/招领）</template>
          <el-table :data="categoryRows" size="small" v-loading="loadingCategory">
            <el-table-column prop="category" label="分类" min-width="120" />
            <el-table-column prop="lost" label="失物" width="90" />
            <el-table-column prop="found" label="招领" width="90" />
            <el-table-column prop="total" label="合计" width="90" />
          </el-table>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card>
          <template #header>高频地点 Top10</template>
          <el-table :data="locationRows" size="small" v-loading="loadingLocation">
            <el-table-column prop="location" label="地点" min-width="150" />
            <el-table-column prop="count" label="数量" width="100" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-card style="margin-top:16px">
      <template #header>月度趋势</template>
      <el-table :data="monthlyRows" size="small" v-loading="loadingMonthly">
        <el-table-column prop="month" label="月份" width="140" />
        <el-table-column prop="lost" label="失物" width="100" />
        <el-table-column prop="found" label="招领" width="100" />
        <el-table-column prop="total" label="总量" width="100" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { fetchCategoryStats, fetchLocationStats, fetchMonthlyStats, fetchOverviewStats } from '../../api/stats'

const router = useRouter()

const overview = ref({})
const categoryRows = ref([])
const locationRows = ref([])
const monthlyRows = ref([])

const loadingOverview = ref(false)
const loadingCategory = ref(false)
const loadingLocation = ref(false)
const loadingMonthly = ref(false)

const toNum = (v) => Number(v || 0)

const loadOverview = async () => {
  loadingOverview.value = true
  try {
    const res = await fetchOverviewStats()
    overview.value = res.data || {}
  } finally {
    loadingOverview.value = false
  }
}

const loadCategory = async () => {
  loadingCategory.value = true
  try {
    const res = await fetchCategoryStats()
    categoryRows.value = res.data || []
  } finally {
    loadingCategory.value = false
  }
}

const loadLocation = async () => {
  loadingLocation.value = true
  try {
    const res = await fetchLocationStats()
    locationRows.value = res.data || []
  } finally {
    loadingLocation.value = false
  }
}

const loadMonthly = async () => {
  loadingMonthly.value = true
  try {
    const res = await fetchMonthlyStats()
    monthlyRows.value = res.data || []
  } finally {
    loadingMonthly.value = false
  }
}

onMounted(() => {
  loadOverview()
  loadCategory()
  loadLocation()
  loadMonthly()
})
</script>
