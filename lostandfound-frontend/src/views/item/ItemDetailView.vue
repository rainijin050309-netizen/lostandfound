<template>
  <div style="max-width:800px;margin:24px auto;padding:0 16px">
    <el-button @click="router.back()" style="margin-bottom:16px">返回</el-button>

    <el-card v-if="item" v-loading="loading">
      <template #header>
        <div style="display:flex;justify-content:space-between;align-items:center">
          <span style="font-size:18px;font-weight:bold">{{ item.title }}</span>
          <div>
            <el-tag :type="item.type === 'lost' ? 'danger' : 'success'" style="margin-right:8px">
              {{ item.type === 'lost' ? '失物' : '招领' }}
            </el-tag>
            <el-tag :type="item.status === 'open' ? 'primary' : 'info'">
              {{ item.status === 'open' ? '待认领' : item.status === 'claimed' ? '已认领' : '已关闭' }}
            </el-tag>
          </div>
        </div>
      </template>

      <el-descriptions :column="2" border>
        <el-descriptions-item label="发布者">{{ item.username }}</el-descriptions-item>
        <el-descriptions-item label="分类">{{ item.category }}</el-descriptions-item>
        <el-descriptions-item label="地点">{{ item.location }}</el-descriptions-item>
        <el-descriptions-item label="发布时间">{{ item.createdAt }}</el-descriptions-item>
        <el-descriptions-item label="丢失日期" v-if="item.lostDate">{{ item.lostDate }}</el-descriptions-item>
        <el-descriptions-item label="发现日期" v-if="item.foundDate">{{ item.foundDate }}</el-descriptions-item>
        <el-descriptions-item label="描述" :span="2">{{ item.description }}</el-descriptions-item>
      </el-descriptions>

      <div style="margin-top:16px" v-if="userStore.userInfo && item.status === 'open' && userStore.userInfo.id !== item.userId">
        <el-button type="primary" @click="showClaimDialog = true">
          {{ item.type === 'lost' ? '我捡到了' : '申请认领' }}
        </el-button>
      </div>
    </el-card>

    <el-card style="margin-top:16px" v-if="claims.length > 0 && userStore.userInfo?.id === item?.userId">
      <template #header>认领申请列表</template>
      <el-table :data="claims">
        <el-table-column label="申请人" prop="username" />
        <el-table-column label="说明" prop="message" />
        <el-table-column label="状态" prop="status">
          <template #default="{ row }">
            <el-tag :type="row.status === 'approved' ? 'success' : row.status === 'rejected' ? 'danger' : 'warning'">
              {{ row.status === 'pending' ? '待审批' : row.status === 'approved' ? '已通过' : '已拒绝' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" v-if="item?.status === 'open'">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 'pending'"
              type="success" size="small"
              @click="handleClaim(row.id, 'approved')">通过</el-button>
            <el-button
              v-if="row.status === 'pending'"
              type="danger" size="small"
              @click="handleClaim(row.id, 'rejected')">拒绝</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="showClaimDialog" :title="item?.type === 'lost' ? '我捡到了' : '申请认领'" width="400px">
      <el-input
        v-model="claimMessage"
        type="textarea"
        :rows="4"
        :placeholder="item?.type === 'lost' ? '请描述捡到的地点、时间及物品现状' : '请描述这个物品是你的理由，例如特征、购买时间等'" />
      <template #footer>
        <el-button @click="showClaimDialog = false">取消</el-button>
        <el-button type="primary" @click="submitClaim" :loading="claimLoading">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getItemById } from '../../api/item'
import { createClaim, getClaimsByItemId, updateClaimStatus } from '../../api/claim'
import { useUserStore } from '../../stores/user'

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
    ElMessage.warning('请填写认领理由')
    return
  }
  claimLoading.value = true
  try {
    await createClaim({
      itemId: item.value.id,
      userId: userStore.userInfo.id,
      message: claimMessage.value
    })
    ElMessage.success('认领申请已提交')
    showClaimDialog.value = false
    claimMessage.value = ''
  } finally {
    claimLoading.value = false
  }
}

const handleClaim = async (id, status) => {
  await updateClaimStatus(id, status)
  ElMessage.success(status === 'approved' ? '已通过认领' : '已拒绝认领')
  loadItem()
}

onMounted(loadItem)
</script>
