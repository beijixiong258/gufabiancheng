import { defineStore } from 'pinia'
import api from '../api'

export const useChatStore = defineStore('chat', {
  state: () => ({
    huihuaList: [],
    currentHuihuaId: null,
    messages: [],
    sending: false,
  }),
  actions: {
    async loadHuihua(jiluId) {
      this.huihuaList = await api.get('/huihua/getlist', { params: { jiluId } })
      this.currentHuihuaId = this.huihuaList[0]?.id ?? null
      if (this.currentHuihuaId) await this.loadMessages()
      else this.messages = []
    },
    async createHuihua(jiluId) {
      const id = await api.post('/huihua/new', null, { params: { jiluId } })
      await this.loadHuihua(jiluId)
      this.currentHuihuaId = id
      this.messages = []
    },
    async removeHuihua(jiluId) {
      await api.delete('/huihua/delete', { params: { huihuaId: this.currentHuihuaId } })
      await this.loadHuihua(jiluId)
    },
    async loadMessages() {
      this.messages = await api.get('/duihua/getlist', {
        params: { huihuaId: this.currentHuihuaId },
      })
    },
    async send({ command, neirong, zhengwen }) {
      this.sending = true
      try {
        const vo = await api.post('/duihua/chat', {
          huihuaId: this.currentHuihuaId,
          command,
          neirong,
          zhengwen,
        })
        if (vo.renleiXiaoxi) this.messages.push(vo.renleiXiaoxi)
        if (vo.aiHuida) {
          // 前端临时标记候选正文（OVER）
          this.messages.push({ ...vo.aiHuida, _candidate: vo.zhuangtai === 'OVER' })
        }
        return vo
      } finally {
        this.sending = false
      }
    },
    reset() {
      this.huihuaList = []
      this.currentHuihuaId = null
      this.messages = []
      this.sending = false
    },
  },
})
