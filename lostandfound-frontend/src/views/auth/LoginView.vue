<template>
  <div class="auth-container">
    <el-card class="auth-card">
      <h2>校园失物招领</h2>
      <h3>登录</h3>
      <el-form :model="form" label-width="80px">
        <el-form-item label="学号">
          <el-input v-model="form.studentId" placeholder="请输入学号" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin" :loading="loading" style="width:100%">
            登录
          </el-button>
        </el-form-item>
        <div style="text-align:center">
          没有账号？<router-link to="/register">立即注册</router-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '../../api/user'
import { useUserStore } from '../../stores/user'

const router = useRouter()
const userStore = useUserStore()

const form = ref({ studentId: '', password: '' })
const loading = ref(false)

const handleLogin = async () => {
  if (!form.value.studentId || !form.value.password) {
    ElMessage.warning('请填写完整信息')
    return
  }
  loading.value = true
  try {
    const res = await login(form.value)
    userStore.setUser(res.data)
    ElMessage.success('登录成功')
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
