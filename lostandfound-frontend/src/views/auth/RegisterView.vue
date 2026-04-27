<template>
  <div class="auth-container">
    <el-card class="auth-card">
      <h2>校园失物招领</h2>
      <h3>注册</h3>
      <el-form :model="form" label-width="80px">
        <el-form-item label="学号">
          <el-input v-model="form.studentId" placeholder="请输入9位学号" />
        </el-form-item>
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleRegister" :loading="loading" style="width:100%">
            注册
          </el-button>
        </el-form-item>
        <div style="text-align:center">
          已有账号？<router-link to="/login">立即登录</router-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { register } from '../../api/user'
import { useUserStore } from '../../stores/user'

const router = useRouter()
const userStore = useUserStore()

const form = ref({
  studentId: '',
  username: '',
  password: '',
  phone: ''
})
const loading = ref(false)

const handleRegister = async () => {
  if (!form.value.studentId || !form.value.username || !form.value.password) {
    ElMessage.warning('请填写完整信息')
    return
  }
  loading.value = true
  try {
    const res = await register({
      ...form.value,
      email: `${form.value.studentId}@link.cuhk.edu.cn`
    })
    userStore.setUser(res.data)
    ElMessage.success('注册成功')
    router.push('/home')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: #f5f5f5;
}
.auth-card {
  width: 400px;
}
h2 {
  text-align: center;
  color: #409eff;
  margin-bottom: 8px;
}
h3 {
  text-align: center;
  margin-bottom: 24px;
  color: #333;
}
</style>
