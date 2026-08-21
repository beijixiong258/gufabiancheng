import { defineStore } from 'pinia'

// 统一管理各弹窗可见性，避免层层 props 传递
export const useUiStore = defineStore('ui', {
  state: () => ({
    newJilu: false,
    profile: false,
    admin: false,
  }),
  actions: {
    closeAll() {
      this.newJilu = false
      this.profile = false
      this.admin = false
    },
  },
})
