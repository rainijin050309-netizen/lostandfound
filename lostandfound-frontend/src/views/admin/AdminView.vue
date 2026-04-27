<template>
  <div style="max-width:1000px;margin:24px auto;padding:0 16px">
    <el-button @click="router.back()" style="margin-bottom:16px">{{ $t('publish.back') }}</el-button>
    <el-card>
      <template #header>
        <div style="display:flex;justify-content:space-between;align-items:center">
          <span>{{ $t('admin.title') }}</span>
          <el-radio-group v-model="filterStatus" @change="loadItems">
            <el-radio-button value="">{{ $t('admin.all') }}</el-radio-button>
            <el-radio-button value="open">{{ $t('home.open') }}</el-radio-button>
            <el-radio-button value="claimed">{{ $t('home.claimed') }}</el-radio-button>
            <el-radio-button value="closed">{{ $t('home.closed') }}</el-radio-button>
          </el-radio-group>
        </div>
      </template>

      <el-table :data="items" v-loading="loading">
        <el-table-column :label="$t('publish.titleField')" prop="title" />
        <el-table-column :label="$t('publish.type')">
          <template #default="{ row }">
            <el-tag :type="row.type === 'lost' ? 'danger' : 'success'" size="small">
              {{ row.type === 'lost' ? $t('home.lost') : $t('home.found') }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column :label="$t('item.publisher')" prop="username" />
        <el-table-column :label="$t('item.location')" prop="location" />
        <el-table-column :label="$t('home.status')">
          <template #default="{ row }">
            <el-tag :type="row.status === 'open' ? 'primary' : row.status === 'claimed' ? 'success' : 'info'" size="small">
              {{ row.status === 'open' ? $t('home.open') : row.status === 'claimed' ? $t('home.claimed') : $t('home.closed') }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column :label="$t('item.publishTime')" prop="createdAt" />
        <el-table-column label="">
          <template #default="{ row }">
            <el-button size="small" @click="router.push(`/item/${row.id}`)">{{ $t('admin.view') }}</el-button>
            <el-button size="small" type="info" @click="handleViewClaims(row)">{{ $t('admin.claimList') }}</el-button>
            <el-button size="small" type="warning" v-if="row.status !== 'closed'" @click="handleClose(row.id)">{{ $t('admin.close') }}</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row.id)">{{ $t('admin.delete') }}</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!loading && items.length === 0" :description="$t('admin.noData')" />
    </el-card>

    <el-dialog v-model="showClaimsDialog" :title="$t('admin.claimDialog')" width="700px">
      <el-table :data="currentClaims" v-loading="claimsLoading">
        <el-table-column :label="$t('item.applicant')" prop="username" />
        <el-table-column :label="$t('item.message')" prop="message" />
        <el-table-column :label="$t('item.applyTime')" prop="createdAt" />
        <el-table-column :label="$t('home.status')">
          <template #default="{ row }">
            <el-tag :type="row.status === 'approved' ? 'success' : row.status === 'rejected' ? 'danger' : 'warning'">
              {{ row.status === 'pending' ? $t('item.pending') : row.status === 'approved' ? $t('item.approved') : $t('item.rejected') }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="">
          <template #default="{ row }">
            <template v-if="row.status === 'pending'">
              <el-button size="small" type="success" @click="handleClaim(row.id, 'approved')">{{ $t('item.approve') }}</el-button>
              <el-button size="small" type="danger" @click="handleClaim(row.id, 'rejected')">{{ $t('item.reject') }}</el-button>
            </template>
            <span v-else style="color:#999;font-size:12px">{{ $t('item.processed') }}</span>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!claimsLoading && currentClaims.length === 0" :description="$t('admin.noClaims')" />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useI18n } from 'vue-i18n'
import { queryItems, updateItemStatus, deleteItem } from '../../api/item'
import { getClaimsByItemId, updateClaimStatus } from '../../api/claim'

const { t } = useI18n()
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
  ElMessage.success(status === 'approved' ? t('msg.approveSuccess') : t('msg.rejectSuccess'))
  const res = await getClaimsByItemId(currentItemId.value)
  currentClaims.value = res.data
  loadItems()
}

const handleClose = async (id) => {
  await ElMessageBox.confirm(t('msg.confirmClose'), t('msg.tip'), { type: 'warning' })
  await updateItemStatus(id, 'closed')
  ElMessage.success(t('msg.closeSuccess'))
  loadItems()
}

const handleDelete = async (id) => {
  await ElMessageBox.confirm(t('msg.confirmDelete'), t('msg.warning'), { type: 'error' })
  await deleteItem(id)
  ElMessage.success(t('msg.deleteSuccess'))
  loadItems()
}

onMounted(loadItems)
</script>
