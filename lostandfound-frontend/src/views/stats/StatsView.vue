<template>
  <div style="max-width:1100px;margin:24px auto;padding:0 16px">
    <el-button @click="router.push('/home')" style="margin-bottom:16px">Back to Home</el-button>

    <el-card style="margin-bottom:16px">
      <template #header>System Statistics Overview</template>
      <el-row :gutter="12" v-loading="loadingOverview">
        <el-col :span="6"><el-statistic title="Lost Items" :value="toNum(overview.total_lost_items)" /></el-col>
        <el-col :span="6"><el-statistic title="Found Items" :value="toNum(overview.total_found_items)" /></el-col>
        <el-col :span="6"><el-statistic title="Matches" :value="toNum(overview.total_matches)" /></el-col>
        <el-col :span="6"><el-statistic title="Total Claims" :value="toNum(overview.total_claims)" /></el-col>
      </el-row>
      <el-row :gutter="12" style="margin-top:10px" v-loading="loadingOverview">
        <el-col :span="8"><el-statistic title="Approved" :value="toNum(overview.approved_claims)" /></el-col>
        <el-col :span="8"><el-statistic title="Pending" :value="toNum(overview.pending_claims)" /></el-col>
        <el-col :span="8"><el-statistic title="Rejected" :value="toNum(overview.rejected_claims)" /></el-col>
      </el-row>
    </el-card>

    <el-row :gutter="16">
      <el-col :span="12">
        <el-card>
          <template #header>Category Distribution (Lost / Found)</template>
          <el-table :data="categoryRows" size="small" v-loading="loadingCategory">
            <el-table-column prop="category" label="Category" min-width="120" />
            <el-table-column prop="lost" label="Lost" width="90" />
            <el-table-column prop="found" label="Found" width="90" />
            <el-table-column prop="total" label="Total" width="90" />
          </el-table>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card>
          <template #header>Top 10 Locations</template>
          <el-table :data="locationRows" size="small" v-loading="loadingLocation">
            <el-table-column prop="location" label="Location" min-width="150" />
            <el-table-column prop="count" label="Count" width="100" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-card style="margin-top:16px">
      <template #header>Monthly Trends</template>
      <el-table :data="monthlyRows" size="small" v-loading="loadingMonthly">
        <el-table-column prop="month" label="Month" width="140" />
        <el-table-column prop="lost" label="Lost" width="100" />
        <el-table-column prop="found" label="Found" width="100" />
        <el-table-column prop="total" label="Total" width="100" />
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
