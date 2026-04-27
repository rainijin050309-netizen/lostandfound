<template>
  <div style="max-width:800px;margin:24px auto;padding:0 16px">
    <el-button @click="router.back()" style="margin-bottom:16px">{{ $t('item.back') }}</el-button>

    <el-card v-if="item" v-loading="loading">
      <template #header>
        <div style="display:flex;justify-content:space-between;align-items:center">
          <span style="font-size:18px;font-weight:bold">{{ item.title }}</span>
          <div>
            <el-tag :type="item.type === 'lost' ? 'danger' : 'success'" style="margin-right:8px">
              {{ item.type === 'lost' ? $t('home.lost') : $t('home.found') }}
            </el-tag>
            <el-tag :type="item.status === 'open' ? 'primary' : 'info'">
              {{ item.status === 'open' ? $t('home.open') : item.status === 'claimed' ? $t('home.claimed') : $t('home.closed') }}
            </el-tag>
          </div>
        </div>
      </template>

      <el-descriptions :column="2" border>
        <el-descriptions-item :label="$t('item.publisher')">{{ item.username }}</el-descriptions-item>
        <el-descriptions-item :label="$t('item.category')">{{ item.category }}</el-descriptions-item>
        <el-descriptions-item :label="$t('item.location')">{{ item.location }}</el-descriptions-item>
        <el-descriptions-item :label="$t('item.publishTime')">{{ item.createdAt }}</el-descriptions-item>
        <el-descriptions-item :label="$t('item.lostDate')" v-if="item.lostDate">{{ item.lostDate }}</el-descriptions-item>
        <el-descriptions-item :label="$t('item.foundDate')" v-if="item.foundDate">{{ item.foundDate }}</el-descriptions-item>
        <el-descriptions-item :label="$t('item.description')" :span="2">{{ item.description }}</el-descriptions-item>
      </el-descriptions>

      <div style="margin-top:16px" v-if="userStore.userInfo && item.status === 'open' && userStore.userInfo.id !== item.userId">
        <el-button type="primary" @click="showClaimDialog = true">
          {{ item.type === 'lost' ? $t('item.iFoundIt') : $t('item.applyClaim') }}
        </el-button>
      </div>
    </el-card>

    <el-card style="margin-top:16px" v-if="claims.length > 0 && userStore.userInfo?.id === item?.userId">
      <template #header>{{ $t('item.claimList') }}</template>
      <el-table :data="claims">
        <el-table-column :label="$t('item.applicant')" prop="username" />
        <el-table-column :label="$t('item.message')" prop="message" />
        <el-table-column :label="$t('item.status')">
          <template #default="{ row }">
            <el-tag :type="row.status === 'approved' ? 'success' : row.status === 'rejected' ? 'danger' : 'warning'">
              {{ row.status === 'pending' ? $t('item.pending') : row.status === 'approved' ? $t('item.approved') : $t('item.rejected') }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column :label="$t('item.applyTime')" prop="createdAt" />
        <el-table-column :label="$t('nav.admin')" v-if="item?.status === 'open'">
          <template #default="{ row }">
            <el-button v-if="row.status === 'pending'" type="success" size="small" @click="handleClaim(row.id, 'approved')">
              {{ $t('item.approve') }}
            </el-button>
            <el-button v-if="row.status === 'pending'" type="danger" size="small" @click="handleClaim(row.id, 'rejected')">
              {{ $t('item.reject') }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="showClaimDialog" :title="item?.type === 'lost' ? $t('item.iFoundIt') : $t('item.applyClaim')" width="400px">
      <el-input
        v-model="claimMessage"
        type="textarea"
        :rows="4"
        :placeholder="item?.type === 'lost' ? $t('item.foundReason') : $t('item.claimReason')" />
      <template #footer>
        <el-button @click="showClaimDialog = false">{{ $t('item.cancel') }}</el-button>
        <el-button type="primary" @click="submitClaim" :loading="claimLoading">{{ $t('item.submitClaim') }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useI18n } from 'vue-i18n'
import { getItemById } from '../../api/item'
import { createClaim, getClaimsByItemId, updateClaimStatus } from '../../api/claim'
import { useUserStore } from '../../stores/user'

const { t } = useI18n()
const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const item = ref(null)
const claims = ref([])
const loading = ref(false)
const showClaimDialog = ref(false)
const claimMessage = ref('')
const claimLoading = ref(false)

const loadItem = async () => {
  loading.value = true
  try {
    const res = await getItemById(route.params.id)
    item.value = res.data
    if (userStore.userInfo?.id === res.data.userId) {
      const claimRes = await getClaimsByItemId(route.params.id)
      claims.value = claimRes.data
    }
  } finally {
    loading.value = false
  }
}

const submitClaim = async () => {
  if (!claimMessage.value) {
    ElMessage.warning(t('msg.fillClaimReason'))
    return
  }
  claimLoading.value = true
  try {
    await createClaim({
      itemId: item.value.id,
      userId: userStore.userInfo.id,
      message: claimMessage.value
    })
    ElMessage.success(t('msg.claimSuccess'))
    showClaimDialog.value = false
    claimMessage.value = ''
  } finally {
    claimLoading.value = false
  }
}

const handleClaim = async (id, status) => {
  await updateClaimStatus(id, status)
  ElMessage.success(status === 'approved' ? t('msg.approveSuccess') : t('msg.rejectSuccess'))
  loadItem()
}

onMounted(loadItem)
</script>
