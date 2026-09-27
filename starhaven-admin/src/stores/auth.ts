import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import { fetchProfile, login as loginApi, logout as logoutApi } from '@/api/admin'
import type { AdminUser } from '@/types'

const TOKEN_KEY = 'starhaven-admin-token'
const USER_KEY = 'starhaven-admin-user'

function readUser(): AdminUser | null {
  const raw = localStorage.getItem(USER_KEY)
  if (!raw)
    return null
  try {
    return JSON.parse(raw) as AdminUser
  }
  catch {
    return null
  }
}

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem(TOKEN_KEY) || '')
  const user = ref<AdminUser | null>(readUser())

  const isAdmin = computed(() => user.value?.role === 'ADMIN' || user.value?.roles?.includes('ADMIN'))
  const isStaff = computed(() => {
    const role = user.value?.role
    return role === 'ADMIN' || role === 'HOST'
  })

  function persist() {
    if (token.value)
      localStorage.setItem(TOKEN_KEY, token.value)
    else
      localStorage.removeItem(TOKEN_KEY)
    if (user.value)
      localStorage.setItem(USER_KEY, JSON.stringify(user.value))
    else
      localStorage.removeItem(USER_KEY)
  }

  function clear() {
    token.value = ''
    user.value = null
    persist()
  }

  async function login(username: string, password: string) {
    const data = await loginApi(username, password)
    token.value = data.token
    persist()
    const profile = await fetchProfile()
    if (profile.role !== 'ADMIN' && profile.role !== 'HOST') {
      clear()
      throw new Error('请使用商家或管理员账号登录')
    }
    user.value = profile
    persist()
  }

  async function logout() {
    try {
      await logoutApi()
    }
    catch {
      // 本地清理优先
    }
    clear()
  }

  return { token, user, isAdmin, isStaff, login, logout, clear }
})
