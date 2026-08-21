import axios from 'axios'
import { ElMessage } from 'element-plus/es/components/message/index.mjs'
import router from '../router'

import { TOKEN_KEY } from '../constants/auth'

const api = axios.create({
  baseURL: '/api',
  timeout: 180000, // 长正文生成可能超过一分钟，避免浏览器先于后端判定失败
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
    const message = error.code === 'ECONNABORTED'
      ? '请求等待时间过长，请稍后重试'
      : (error.response?.data?.message || '无法连接服务器，请确认后端已启动')
    ElMessage.error(message)
    return Promise.reject(error)
  },
)

export default api
