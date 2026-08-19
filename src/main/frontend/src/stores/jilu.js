import { defineStore } from 'pinia'
import api from '../api'

const emptyForm = () => ({ timu: '', ticai: 'QITA', biaoqian: '', zhengwen: '' })

export const useJiluStore = defineStore('jilu', {
  state: () => ({
    list: [],
    current: null, // 当前记录详情
    editForm: emptyForm(), // 编辑器当前内容（正文可以尚未保存）
    selectedIds: [],
  }),
  actions: {
    async loadList() {
      this.list = await api.get('/jilu/getlist')
    },
    async select(id) {
      this.current = await api.get('/jilu/get', { params: { id } })
      this.editForm = {
        timu: this.current.timu ?? '',
        ticai: this.current.ticai ?? 'QITA',
        biaoqian: this.current.biaoqian ?? '',
        zhengwen: this.current.zhengwen ?? '',
      }
    },
    async create(form) {
      const jilu = await api.post('/jilu/add', form)
      await this.loadList()
      return jilu
    },
    async save() {
      await api.put('/jilu/modify', { ...this.editForm }, { params: { id: this.current.id } })
      await this.reloadCurrent()
    },
    async finish() {
      await api.put('/jilu/modify', { ...this.editForm }, { params: { id: this.current.id } })
      await api.put('/jilu/finish', null, { params: { id: this.current.id } })
      await this.reloadCurrent()
    },
    async remove() {
      const id = this.current.id
      await api.delete('/jilu/delete', { params: { id } })
      this.selectedIds = this.selectedIds.filter((x) => x !== id)
      this.current = null
      await this.loadList()
    },
    async batchRemove() {
      await api.delete('/jilu/deletebatch', { data: [...this.selectedIds] })
      if (this.current && this.selectedIds.includes(this.current.id)) this.current = null
      this.selectedIds = []
      await this.loadList()
    },
    async reloadCurrent() {
      if (this.current) {
        this.current = await api.get('/jilu/get', { params: { id: this.current.id } })
      }
      await this.loadList()
    },
    // AI 候选正文进入编辑器（不立即写库）
    useCandidate(text) {
      this.editForm.zhengwen = text
    },
    clearCurrent() {
      this.current = null
      this.editForm = emptyForm()
    },
  },
})
