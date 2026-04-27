<template>
  <div style="max-width:600px;margin:24px auto;padding:0 16px">
    <el-button @click="router.back()" style="margin-bottom:16px">返回</el-button>
    <el-card>
      <template #header>发布失物/招领信息</template>
      <el-form :model="form" label-width="80px">
        <el-form-item label="类型">
          <el-radio-group v-model="form.type">
            <el-radio value="lost">失物（我丢了东西）</el-radio>
            <el-radio value="found">招领（我捡到东西）</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="标题">
          <el-input v-model="form.title" placeholder="简短描述物品" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="form.category" placeholder="选择分类" style="width:100%">
            <el-option label="电子产品" value="电子产品" />
            <el-option label="证件" value="证件" />
            <el-option label="钥匙" value="钥匙" />
            <el-option label="钱包" value="钱包" />
            <el-option label="背包" value="背包" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="地点">
          <el-input v-model="form.location" placeholder="丢失或发现的地点" />
        </el-form-item>
        <el-form-item label="日期">
          <el-date-picker
            v-model="form.date"
            type="date"
            placeholder="选择日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width:100%" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="4" placeholder="详细描述物品特征" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handlePublish" :loading="loading" style="width:100%">
            发布
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { createItem } from '../../api/item'
import { useUserStore } from '../../stores/user'

const router = useRouter()
const userStore = useUserStore()

const form = ref({
  type: 'lost',
  title: '',
  category: '',
  location: '',
  date: '',
  description: ''
})
const loading = ref(false)

const handlePublish = async () => {
  if (!form.value.title || !form.value.location) {
    ElMessage.warning('请填写标题和地点')
    return
  }
  loading.value = true
  try {
    await createItem({
      userId: userStore.userInfo.id,
      type: form.value.type,
      title: form.value.title,
      category: form.value.category,
      location: form.value.location,
      lostDate: form.value.type === 'lost' ? form.value.date : null,
      foundDate: form.value.type === 'found' ? form.value.date : null,
      description: form.value.description
    })
    ElMessage.success('发布成功')
    router.push('/home')
  } finally {
    loading.value = false
  }
}
</script>
