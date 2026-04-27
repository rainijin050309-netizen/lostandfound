<template>
  <div style="max-width:1000px;margin:24px auto;padding:0 16px">
    <el-button @click="router.back()" style="margin-bottom:16px">返回</el-button>
    <el-card>
      <template #header>
        <div style="display:flex;justify-content:space-between;align-items:center">
          <span>管理后台 - 物品管理</span>
          <el-radio-group v-model="filterStatus" @change="loadItems">
            <el-radio-button value="">全部</el-radio-button>
            <el-radio-button value="open">待认领</el-radio-button>
            <el-radio-button value="claimed">已认领</el-radio-button>
            <el-radio-button value="closed">已关闭</el-radio-button>
          </el-radio-group>
        </div>
      </template>

      <el-table :data="items" v-loading="loading">
        <el-table-column label="标题" prop="title" />
        <el-table-column label="类型">
          <template #default="{ row }">
            <el-tag :type="row.type === 'lost' ? 'danger' : 'success'" size="small">
              {{ row.type === 'lost' ? '失物' : '招领' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="发布者" prop="username" />
        <el-table-column label="地点" prop="location" />
        <el-table-column label="状态">
          <template #default="{ row }">
            <el-tag :type="row.status === 'open' ? 'primary' : row.status === 'claimed' ? 'success' : 'info'" size="small">
              {{ row.status === 'open' ? '待认领' : row.status === 'claimed' ? '已认领' : '已关闭' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="发布时间" prop="createdAt" />
        <el-table-column label="操作">
          <template #default="{ row }">
            <el-button size="small" @click="router.push(`/item/${row.id}`)">查看</el-button>
            <el-button size="small" type="info" @click="handleViewClaims(row)">认领申请</el-button>
            <el-button
              size="small"
              type="warning"
              v-if="row.status !== 'closed'"
              @click="handleClose(row.id)">关闭</el-button>
            <el-button
              size="small"
              type="danger"
              @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!loading && items.length === 0" description="暂无数据" />
    </el-card>

    <!-- 认领申请对话框 -->
    <el-dialog v-model="showClaimsDialog" title="认领申请列表" width="700px">
      <el-table :data="currentClaims" v-loading="claimsLoading">
        <el-table-column label="申请人" prop="username" />
        <el-table-column label="认领说明" prop="message" />
        <el-table-column label="申请时间" prop="createdAt" />
        <el-table-column label="状态">
          <template #default="{ row }">
            <el-tag :type="row.status === 'approved' ? 'success' : row.status === 'rejected' ? 'danger' : 'warning'">
              {{ row.status === 'pending' ? '待审批' : row.status === 'approved' ? '已通过' : '已拒绝' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="{ row }">
            <template v-if="row.status === 'pending'">
              <el-button size="small" type="success" @click="handleClaim(row.id, 'approved')">通过</el-button>
              <el-button size="small" type="danger" @click="handleClaim(row.id, 'rejected')">拒绝</el-button>
            </template>
            <span v-else style="color:#999;font-size:12px">已处理</span>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!claimsLoading && currentClaims.length === 0" description="暂无认领申请" />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { queryItems, updateItemStatus, deleteItem } from '../../api/item'
import { getClaimsByItemId, updateClaimStatus } from '../../api/claim'

const router = useRouter()
const items = ref([])
const loading = ref(false)
const filterStatus = ref('')

const showClaimsDialog = ref(false)
const currentClaims = ref([])
const claimsLoading = ref(false)
const currentItemId = ref(null)

const loadItems = async () => {
  loading.value = true
  try {
    const res = await queryItems({ status: filterStatus.value, page: 1, size: 100 })
    items.value = res.data
  } finally {
    loading.value = false
  }
}

const handleViewClaims = async (item) => {
  currentItemId.value = item.id
  showClaimsDialog.value = true
  claimsLoading.value = true
  try {
    const res = await getClaimsByItemId(item.id)
    currentClaims.value = res.data
  } finally {
    claimsLoading.value = false
  }
}

const handleClaim = async (id, status) => {
  await updateClaimStatus(id, status)
  ElMessage.success(status === 'approved' ? '已通过认领' : '已拒绝认领')
  const res = await getClaimsByItemId(currentItemId.value)
  currentClaims.value = res.data
  loadItems()
}

const handleClose = async (id) => {
  await ElMessageBox.confirm('确认关闭该物品？', '提示', { type: 'warning' })
  await updateItemStatus(id, 'closed')
  ElMessage.success('已关闭')
  loadItems()
}

const handleDelete = async (id) => {
  await ElMessageBox.confirm('确认删除该物品？此操作不可撤销', '警告', { type: 'error' })
  await deleteItem(id)
  ElMessage.success('已删除')
  loadItems()
}

onMounted(loadItems)
</script>
