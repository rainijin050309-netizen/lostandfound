<template>
  <div class="auth-container">
    <el-card class="auth-card">
      <div style="text-align:right;margin-bottom:8px">
        <el-button size="small" @click="toggleLocale">{{ locale === 'zh' ? 'EN' : '中' }}</el-button>
      </div>
      <h2>{{ $t('auth.appTitle') }}</h2>
      <h3>{{ $t('auth.login') }}</h3>
      <el-form :model="form" label-width="90px">
        <el-form-item :label="$t('auth.studentId')">
          <el-input v-model="form.studentId" :placeholder="$t('auth.studentIdPlaceholder')" />
        </el-form-item>
        <el-form-item :label="$t('auth.password')">
          <el-input v-model="form.password" type="password" :placeholder="$t('auth.passwordPlaceholder')" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin" :loading="loading" style="width:100%">
            {{ $t('auth.loginBtn') }}
          </el-button>
        </el-form-item>
        <div style="text-align:center">
          <router-link to="/register">{{ $t('auth.toRegister') }}</router-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useI18n } from 'vue-i18n'
import { login } from '../../api/user'
import { useUserStore } from '../../stores/user'

const { t, locale } = useI18n()
const router = useRouter()
const userStore = useUserStore()

const form = ref({ studentId: '', password: '' })
const loading = ref(false)

const toggleLocale = () => {
  locale.value = locale.value === 'zh' ? 'en' : 'zh'
  localStorage.setItem('locale', locale.value)
}

const handleLogin = async () => {
  if (!form.value.studentId || !form.value.password) {
    ElMessage.warning(t('msg.fillComplete'))
    return
  }
  loading.value = true
  try {
    const res = await login(form.value)
    userStore.setUser(res.data)
    ElMessage.success(t('msg.loginSuccess'))
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
.auth-card { width: 400px; }
h2 { text-align: center; color: #409eff; margin-bottom: 8px; }
h3 { text-align: center; margin-bottom: 24px; color: #333; }
</style>
