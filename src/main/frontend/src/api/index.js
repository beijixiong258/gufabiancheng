import axios from 'axios'
import { ElMessage } from 'element-plus/es/components/message/index.mjs'
import router from '../router'

import { TOKEN_KEY } from '../constants/auth'

const api = axios.create({
  baseURL: '/api',
  timeout: 60000, // AI 对话可能较慢，放宽超时
})

// 请求拦截器：统一携带 Token
api.interceptors.request.use((config) => {
  const token = localStorage.getItem(TOKEN_KEY)
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

// 响应拦截器：统一按后端 Result.code 处理
api.interceptors.response.use(
  (response) => {
    const result = response.data
    // 后端统一响应 { code, data, message }，HTTP 恒为 200
    if (result && typeof result.code === 'number') {
      if (result.code === 200) return result.data
      if (result.code === 401) {
        localStorage.removeItem(TOKEN_KEY)
        if (router.currentRoute.value.name !== 'login') router.push('/login')
      }
      ElMessage.error(result.message || `请求失败(${result.code})`)
      return Promise.reject(new Error(result.message || '请求失败'))
    }
    return result
  },
  (error) => {
    ElMessage.error(error.response?.data?.message || '无法连接服务器，请确认后端已启动')
    return Promise.reject(error)
  },
)

export default api
