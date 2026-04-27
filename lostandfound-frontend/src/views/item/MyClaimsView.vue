<template>
  <div style="max-width:800px;margin:24px auto;padding:0 16px">
    <el-button @click="router.back()" style="margin-bottom:16px">{{ $t('myClaims.back') }}</el-button>
    <el-card>
      <template #header>{{ $t('myClaims.title') }}</template>
      <el-table :data="claims" v-loading="loading">
        <el-table-column :label="$t('myClaims.item')" prop="itemTitle" />
        <el-table-column :label="$t('myClaims.message')" prop="message" />
        <el-table-column :label="$t('myClaims.status')">
          <template #default="{ row }">
            <el-tag :type="row.status === 'approved' ? 'success' : row.status === 'rejected' ? 'danger' : 'warning'">
              {{ row.status === 'pending' ? $t('item.pending') : row.status === 'approved' ? $t('item.approved') : $t('item.rejected') }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column :label="$t('myClaims.time')" prop="createdAt" />
      </el-table>
      <el-empty v-if="!loading && claims.length === 0" :description="$t('myClaims.noData')" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { getClaimsByUserId } from '../../api/claim'
import { useUserStore } from '../../stores/user'

const { } = useI18n()
const router = useRouter()
const userStore = useUserStore()
const claims = ref([])
const loading = ref(false)

onMounted(async () => {
  loading.value = true
  try {
    const res = await getClaimsByUserId(userStore.userInfo.id)
    claims.value = res.data
  } finally {
    loading.value = false
  }
})
</script>
