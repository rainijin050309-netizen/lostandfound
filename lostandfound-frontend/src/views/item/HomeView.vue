<template>
  <div>
    <el-container>
      <el-header style="background:#409eff;display:flex;align-items:center;justify-content:space-between">
        <span style="color:white;font-size:18px;font-weight:bold">校园失物招领</span>
        <div>
          <el-button v-if="userStore.userInfo" @click="router.push('/publish')" type="success" size="small">
            发布信息
          </el-button>
          <el-button v-if="userStore.userInfo" @click="router.push('/my-claims')" size="small" style="margin-left:8px">
            我的认领
          </el-button>
          <el-button v-if="userStore.isAdmin()" @click="router.push('/admin')" type="warning" size="small" style="margin-left:8px">
            管理后台
          </el-button>
          <el-button v-if="userStore.userInfo" @click="handleLogout" size="small" style="margin-left:8px">
            退出 {{ userStore.userInfo.username }}
          </el-button>
          <el-button v-else @click="router.push('/login')" type="primary" size="small">
            登录
          </el-button>
        </div>
      </el-header>

      <el-main>
        <el-row :gutter="16" style="margin-bottom:16px">
          <el-col :span="6">
            <el-select v-model="query.type" placeholder="类型" clearable @change="loadItems">
              <el-option label="失物" value="lost" />
              <el-option label="招领" value="found" />
            </el-select>
          </el-col>
          <el-col :span="6">
            <el-select v-model="query.status" placeholder="状态" clearable @change="loadItems">
              <el-option label="待认领" value="open" />
              <el-option label="已认领" value="claimed" />
              <el-option label="已关闭" value="closed" />
            </el-select>
          </el-col>
          <el-col :span="8">
            <el-input v-model="query.keyword" placeholder="搜索关键词" @keyup.enter="loadItems" clearable />
          </el-col>
          <el-col :span="4">
            <el-button type="primary" @click="loadItems">搜索</el-button>
          </el-col>
        </el-row>

        <el-row :gutter="16" v-loading="loading">
          <el-col :span="8" v-for="item in items" :key="item.id" style="margin-bottom:16px">
            <el-card @click="router.push(`/item/${item.id}`)" style="cursor:pointer">
              <template #header>
                <div style="display:flex;justify-content:space-between;align-items:center">
                  <span>{{ item.title }}</span>
                  <el-tag :type="item.type === 'lost' ? 'danger' : 'success'">
                    {{ item.type === 'lost' ? '失物' : '招领' }}
                  </el-tag>
                </div>
              </template>
              <p style="color:#666;font-size:13px">{{ item.description }}</p>
              <p style="font-size:12px;color:#999">
                地点：{{ item.location }} | 发布者：{{ item.username }}
              </p>
              <p style="font-size:12px;color:#999">{{ item.createdAt }}</p>
              <el-tag size="small" :type="item.status === 'open' ? 'primary' : 'info'">
                {{ item.status === 'open' ? '待认领' : item.status === 'claimed' ? '已认领' : '已关闭' }}
              </el-tag>
            </el-card>
          </el-col>
        </el-row>

        <el-empty v-if="!loading && items.length === 0" description="暂无数据" />
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { queryItems } from '../../api/item'
import { useUserStore } from '../../stores/user'

const router = useRouter()
const userStore = useUserStore()

const items = ref([])
const loading = ref(false)
const query = ref({ type: '', status: '', keyword: '', page: 1, size: 12 })

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
  ElMessage.success('已退出登录')
  router.push('/login')
}

onMounted(loadItems)
</script>
