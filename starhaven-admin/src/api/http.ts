import axios, { type AxiosRequestConfig } from 'axios'
import type { ApiResult } from '@/types'

const TOKEN_KEY = 'starhaven-admin-token'
const USER_KEY = 'starhaven-admin-user'

const raw = axios.create({
  timeout: 20000,
})

raw.interceptors.request.use((config) => {
  const token = localStorage.getItem(TOKEN_KEY) || ''
  if (token) {
    config.headers.satoken = token
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

function kickToLogin() {
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(USER_KEY)
  if (!window.location.pathname.startsWith('/login'))
    window.location.assign('/login')
}

raw.interceptors.response.use(
  (response) => {
    const body = response.data as ApiResult<unknown>
    if (typeof body?.code === 'number' && body.code !== 0 && body.code !== 200) {
      if (body.code === 401)
        kickToLogin()
      return Promise.reject(new Error(body.message || '请求失败'))
    }
    return body.data as never
  },
  (error) => {
    if (error.response?.status === 401)
      kickToLogin()
    const message = error.response?.data?.message || error.message || '网络错误'
    return Promise.reject(new Error(message))
  },
)

const http = {
  get<T>(url: string, config?: AxiosRequestConfig) {
    return raw.get<T, T>(url, config)
  },
  post<T>(url: string, data?: unknown, config?: AxiosRequestConfig) {
    return raw.post<T, T>(url, data, config)
  },
  put<T>(url: string, data?: unknown, config?: AxiosRequestConfig) {
    return raw.put<T, T>(url, data, config)
  },
  delete<T>(url: string, config?: AxiosRequestConfig) {
    return raw.delete<T, T>(url, config)
  },
}

export default http
