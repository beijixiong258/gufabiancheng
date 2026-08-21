import { defineStore } from 'pinia'
import api from '../api'

let localMessageSequence = 0

export const useChatStore = defineStore('chat', {
  state: () => ({
    huihuaList: [],
    currentHuihuaId: null,
    messages: [],
    loading: false,
    sending: false,
  }),
  actions: {
    async loadHuihua(jiluId) {
      this.loading = true
      this.huihuaList = []
      this.currentHuihuaId = null
      this.messages = []
      try {
        this.huihuaList = await api.get('/huihua/getlist', { params: { jiluId } })
        this.currentHuihuaId = this.huihuaList[0]?.id ?? null
        if (this.currentHuihuaId) await this.loadMessages(false)
        else this.messages = []
      } finally {
        this.loading = false
      }
    },
    async createHuihua(jiluId) {
      this.loading = true
      try {
        const id = await api.post('/huihua/new', null, { params: { jiluId } })
        await this.loadHuihua(jiluId)
        this.currentHuihuaId = id
        this.messages = []
      } finally {
        this.loading = false
      }
    },
    async removeHuihua(jiluId) {
      this.loading = true
      try {
        await api.delete('/huihua/delete', { params: { huihuaId: this.currentHuihuaId } })
        await this.loadHuihua(jiluId)
      } finally {
        this.loading = false
      }
    },
    async loadMessages(manageLoading = true) {
      if (manageLoading) this.loading = true
      this.messages = []
      try {
        this.messages = await api.get('/duihua/getlist', {
          params: { huihuaId: this.currentHuihuaId },
        })
      } finally {
        if (manageLoading) this.loading = false
      }
    },
    async send({ command, neirong, zhengwen, retryMessageId = null }) {
      this.sending = true
      const huihuaId = this.currentHuihuaId
      let localMessageId = null
      // 普通消息先进入对话区，AI 请求失败也不让用户刚输入的内容消失。
      if (command === 0) {
        localMessageId = retryMessageId
        const retryIndex = retryMessageId
          ? this.messages.findIndex((message) => message.id === retryMessageId)
          : -1
        if (retryIndex >= 0) {
          this.messages.splice(retryIndex, 1, {
            ...this.messages[retryIndex],
            _sending: true,
            _failed: false,
          })
        } else {
          localMessageSequence += 1
          localMessageId = `local-${Date.now()}-${localMessageSequence}`
          this.messages.push({
            id: localMessageId,
            type: 'USER',
            neirong,
            _sending: true,
          })
        }
      }
      try {
        const vo = await api.post('/duihua/chat', {
          huihuaId,
          command,
          neirong,
          zhengwen,
        })
        // 用户在等待期间切换了会话时，不把旧会话的返回结果混入新会话。
        if (this.currentHuihuaId !== huihuaId) return vo
        if (vo.renleiXiaoxi) {
          const localIndex = this.messages.findIndex((message) => message.id === localMessageId)
          if (localIndex >= 0) this.messages.splice(localIndex, 1, vo.renleiXiaoxi)
          else if (!this.messages.some((message) => message.id === vo.renleiXiaoxi.id)) {
            this.messages.push(vo.renleiXiaoxi)
          }
        }
        if (vo.aiHuida) {
          // 前端临时标记候选正文（OVER）
          if (!this.messages.some((message) => message.id === vo.aiHuida.id)) {
            this.messages.push({ ...vo.aiHuida, _candidate: vo.zhuangtai === 'OVER' })
          }
        }
        return vo
      } catch (error) {
        if (this.currentHuihuaId === huihuaId && localMessageId) {
          const localIndex = this.messages.findIndex((message) => message.id === localMessageId)
          if (localIndex >= 0) {
            this.messages.splice(localIndex, 1, {
              ...this.messages[localIndex],
              _sending: false,
              _failed: true,
            })
          }
        }
        throw error
      } finally {
        this.sending = false
      }
    },
    removeLocalMessage(id) {
      this.messages = this.messages.filter((message) => message.id !== id)
    },
    reset() {
      this.huihuaList = []
      this.currentHuihuaId = null
      this.messages = []
      this.loading = false
      this.sending = false
    },
  },
})
