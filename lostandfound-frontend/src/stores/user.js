import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  const userInfo = ref(null)

  const setUser = (user) => {
    userInfo.value = user
  }

  const clearUser = () => {
    userInfo.value = null
  }

  const isAdmin = () => {
    return userInfo.value?.role === 'admin'
  }

  return { userInfo, setUser, clearUser, isAdmin }
})
