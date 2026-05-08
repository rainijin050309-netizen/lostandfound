<template>
  <div style="max-width:600px;margin:24px auto;padding:0 16px">
    <el-button @click="router.back()" style="margin-bottom:16px">{{ $t('publish.back') }}</el-button>
    <el-card>
      <template #header>{{ $t('publish.title') }}</template>
      <el-form :model="form" label-width="80px">
        <el-form-item :label="$t('publish.type')">
          <el-radio-group v-model="form.type">
            <el-radio value="lost">{{ $t('publish.lostType') }}</el-radio>
            <el-radio value="found">{{ $t('publish.foundType') }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item :label="$t('publish.titleField')">
          <el-input v-model="form.title" :placeholder="$t('publish.titlePlaceholder')" />
        </el-form-item>
        <el-form-item :label="$t('publish.category')">
          <el-select v-model="form.category" :placeholder="$t('publish.categoryPlaceholder')" style="width:100%">
            <el-option :label="$t('publish.categories.electronics')" value="电子产品" />
            <el-option :label="$t('publish.categories.id')" value="证件" />
            <el-option :label="$t('publish.categories.keys')" value="钥匙" />
            <el-option :label="$t('publish.categories.wallet')" value="钱包" />
            <el-option :label="$t('publish.categories.bag')" value="背包" />
            <el-option :label="$t('publish.categories.other')" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('publish.location')">
          <el-input v-model="form.location" :placeholder="$t('publish.locationPlaceholder')" />
        </el-form-item>
        <el-form-item :label="$t('publish.date')">
          <el-date-picker
            v-model="form.date"
            type="date"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width:100%" />
        </el-form-item>
        <el-form-item :label="$t('publish.description')">
          <el-input v-model="form.description" type="textarea" :rows="4" :placeholder="$t('publish.descriptionPlaceholder')" />
        </el-form-item>
        <el-form-item label="Image">
          <el-upload
            :action="`http://localhost:8080/api/upload`"
            :limit="1"
            :on-success="handleUploadSuccess"
            :on-remove="handleUploadRemove"
            accept=".jpg,.jpeg,.png,.gif"
            list-type="picture">
            <el-button type="primary">Upload Image</el-button>
            <template #tip>
              <div style="color:#999;font-size:12px">jpg / png / gif, optional</div>
            </template>
          </el-upload>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handlePublish" :loading="loading" style="width:100%">
            {{ $t('publish.submit') }}
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
import { useI18n } from 'vue-i18n'
import { createItem } from '../../api/item'
import { useUserStore } from '../../stores/user'

const { t } = useI18n()
const router = useRouter()
const userStore = useUserStore()

const form = ref({ type: 'lost', title: '', category: '', location: '', date: '', description: '',imageUrl: null })
const loading = ref(false)
const handleUploadSuccess = (res) => {
  form.value.imageUrl = res.data
}

const handleUploadRemove = () => {
  form.value.imageUrl = null
}

const handlePublish = async () => {
  if (!form.value.title || !form.value.location) {
    ElMessage.warning(t('msg.fillTitleLocation'))
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
      description: form.value.description,
      imageUrl: form.value.imageUrl
    })
    ElMessage.success(t('msg.publishSuccess'))
    router.push('/home')
  } finally {
    loading.value = false
  }
}
</script>
