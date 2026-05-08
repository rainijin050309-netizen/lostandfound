<template>
  <div>
    <el-container>
      <el-header style="background:#409eff;display:flex;align-items:center;justify-content:space-between">
        <span style="color:white;font-size:18px;font-weight:bold">{{ $t('nav.title') }}</span>
        <div style="display:flex;align-items:center;gap:8px">
          <el-button v-if="userStore.userInfo" @click="router.push('/publish')" type="success" size="small">
            {{ $t('nav.publish') }}
          </el-button>
          <el-button v-if="userStore.userInfo" @click="router.push('/my-claims')" size="small">
            {{ $t('nav.myClaims') }}
          </el-button>
          <el-button @click="router.push('/stats')" size="small" type="info" style="margin-left:8px">
            {{ $t('nav.stats') }}
          </el-button>
          <el-button @click="router.push('/llm-query')" size="small" type="primary" plain style="margin-left:8px">
            {{ $t('nav.llmQuery') }}
          </el-button>
          <el-button v-if="userStore.isAdmin()" @click="router.push('/admin')" type="warning" size="small">
            {{ $t('nav.admin') }}
          </el-button>
          <el-button v-if="userStore.userInfo" @click="handleLogout" size="small">
            {{ $t('nav.logout') }} {{ userStore.userInfo.username }}
          </el-button>
          <el-button v-else @click="router.push('/login')" type="primary" size="small">
            {{ $t('nav.login') }}
          </el-button>
          <el-button size="small" @click="toggleLocale" style="margin-left:4px">
            {{ locale === 'zh' ? 'EN' : '中' }}
          </el-button>
        </div>
      </el-header>

      <el-main>
        <el-row :gutter="16" style="margin-bottom:16px">
          <el-col :span="6">
            <el-select v-model="query.type" :placeholder="$t('home.type')" clearable @change="loadItems">
              <el-option :label="$t('home.lost')" value="lost" />
              <el-option :label="$t('home.found')" value="found" />
            </el-select>
          </el-col>
          <el-col :span="6">
            <el-select v-model="query.status" :placeholder="$t('home.status')" clearable @change="loadItems">
              <el-option :label="$t('home.open')" value="open" />
              <el-option :label="$t('home.claimed')" value="claimed" />
              <el-option :label="$t('home.closed')" value="closed" />
            </el-select>
          </el-col>
          <el-col :span="8">
            <el-input v-model="query.keyword" :placeholder="$t('home.keyword')" @keyup.enter="loadItems" clearable />
          </el-col>
          <el-col :span="4">
            <el-button type="primary" @click="loadItems">{{ $t('home.search') }}</el-button>
          </el-col>
        </el-row>

        <el-row :gutter="16" v-loading="loading">
          <el-col :span="8" v-for="item in items" :key="item.id" style="margin-bottom:16px">
            <el-card @click="router.push(`/item/${item.id}`)" style="cursor:pointer">
              <template #header>
                <div style="display:flex;justify-content:space-between;align-items:center">
                  <span>{{ item.title }}</span>
                  <el-tag :type="item.type === 'lost' ? 'danger' : 'success'">
                    {{ item.type === 'lost' ? $t('home.lost') : $t('home.found') }}
                  </el-tag>
                </div>
              </template>
              <p style="color:#666;font-size:13px">{{ item.description }}</p>
              <p style="font-size:12px;color:#999">
                {{ $t('home.location') }}：{{ item.location }} | {{ $t('home.publisher') }}：{{ item.username }}
              </p>
              <p style="font-size:12px;color:#999">{{ item.createdAt }}</p>
              <el-tag size="small" :type="item.status === 'open' ? 'primary' : 'info'">
                {{ item.status === 'open' ? $t('home.open') : item.status === 'claimed' ? $t('home.claimed') : $t('home.closed') }}
              </el-tag>
            </el-card>
          </el-col>
        </el-row>

        <el-empty v-if="!loading && items.length === 0" :description="$t('home.noData')" />
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useI18n } from 'vue-i18n'
import { queryItems } from '../../api/item'
import { useUserStore } from '../../stores/user'

const { t, locale } = useI18n()
const router = useRouter()
const userStore = useUserStore()

const items = ref([])
const loading = ref(false)
const query = ref({ type: '', status: '', keyword: '', page: 1, size: 12 })

const toggleLocale = () => {
  locale.value = locale.value === 'zh' ? 'en' : 'zh'
  localStorage.setItem('locale', locale.value)
}

const loadItems = async () => {
  loading.value = true
  try {
    const res = await queryItems(query.value)
    items.value = res.data
  } finally {
    loading.value = false
  }
}

const handleLogout = () => {
  userStore.clearUser()
  ElMessage.success(t('msg.logoutSuccess'))
  router.push('/login')
}

onMounted(loadItems)
</script>
