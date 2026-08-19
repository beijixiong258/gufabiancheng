import { defineStore } from 'pinia'
import api from '../api'
import { TOKEN_KEY } from '../constants/auth'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null,
  }),
  getters: {
    isAdmin: (s) => s.user?.quanxian === 'ADMIN',
  },
  actions: {
    async login(form) {
      const token = await api.post('/yonghu/login', form)
      localStorage.setItem(TOKEN_KEY, token)
    },
    async register(form) {
      await api.post('/yonghu/register', form)
    },
    // 前端启动且本地有 Token 时调用，统一保存返回的 Token
    async refresh() {
      const token = await api.post('/yonghu/refresh')
      localStorage.setItem(TOKEN_KEY, token)
    },
    async logout() {
      try {
        await api.post('/yonghu/logout')
      } catch {
        /* 忽略退出异常 */
      }
      localStorage.removeItem(TOKEN_KEY)
      this.user = null
    },
    async fetchMe() {
      this.user = await api.get('/yonghu/me')
    },
  },
})
