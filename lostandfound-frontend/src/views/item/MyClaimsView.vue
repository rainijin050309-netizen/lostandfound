<template>
  <div style="max-width:800px;margin:24px auto;padding:0 16px">
    <el-button @click="router.back()" style="margin-bottom:16px">返回</el-button>
    <el-card>
      <template #header>我的认领记录</template>
      <el-table :data="claims" v-loading="loading">
        <el-table-column label="物品" prop="itemTitle" />
        <el-table-column label="认领说明" prop="message" />
        <el-table-column label="状态">
          <template #default="{ row }">
            <el-tag :type="row.status === 'approved' ? 'success' : row.status === 'rejected' ? 'danger' : 'warning'">
              {{ row.status === 'pending' ? '待审批' : row.status === 'approved' ? '已通过' : '已拒绝' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="申请时间" prop="createdAt" />
      </el-table>
      <el-empty v-if="!loading && claims.length === 0" description="暂无认领记录" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getClaimsByUserId } from '../../api/claim'
import { useUserStore } from '../../stores/user'

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
