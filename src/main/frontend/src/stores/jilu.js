import { defineStore } from 'pinia'
import api from '../api'

const emptyForm = () => ({ timu: '', ticai: 'QITA', biaoqian: '', zhengwen: '' })

export const useJiluStore = defineStore('jilu', {
  state: () => ({
    list: [],
    loading: false,
    total: 0,
    page: 1,
    size: 20,
    filters: { guanjianci: '', ticai: '', zhuangtai: '' },
    current: null, // 当前记录详情
    editForm: emptyForm(), // 编辑器当前内容（正文可以尚未保存）
    savedForm: emptyForm(),
    selectedIds: [],
  }),
  getters: {
    hasUnsavedChanges: (state) => (
      Boolean(state.current)
      && JSON.stringify(state.editForm) !== JSON.stringify(state.savedForm)
    ),
  },
  actions: {
    async loadList(resetPage = false) {
      this.loading = true
      try {
        if (resetPage) this.page = 1
        const data = await api.get('/jilu/getlist', {
          params: {
            guanjianci: this.filters.guanjianci || undefined,
            ticai: this.filters.ticai || undefined,
            zhuangtai: this.filters.zhuangtai || undefined,
            page: this.page,
            size: this.size,
          },
        })
        this.list = data.records ?? []
        this.total = data.total ?? 0
        const lastPage = Math.max(data.pages ?? Math.ceil(this.total / this.size), 1)
        if (this.page > lastPage) {
          this.page = lastPage
          await this.loadList()
          return
        }
        const visibleIds = new Set(this.list.map((record) => record.id))
        this.selectedIds = this.selectedIds.filter((id) => visibleIds.has(id))
      } finally {
        this.loading = false
      }
    },
    async changePage(page) {
      this.page = page
      await this.loadList()
    },
    async select(id) {
      this.current = await api.get('/jilu/get', { params: { id } })
      this.editForm = {
        timu: this.current.timu ?? '',
        ticai: this.current.ticai ?? 'QITA',
        biaoqian: this.current.biaoqian ?? '',
        zhengwen: this.current.zhengwen ?? '',
      }
      this.savedForm = { ...this.editForm }
    },
    async create(form) {
      const jilu = await api.post('/jilu/add', form)
      this.page = 1
      this.selectedIds = []
      await this.loadList()
      return jilu
    },
    async save() {
      await api.put('/jilu/modify', { ...this.editForm }, { params: { id: this.current.id } })
      this.savedForm = { ...this.editForm }
      await this.reloadCurrent()
    },
    async finish() {
      await api.put('/jilu/modify', { ...this.editForm }, { params: { id: this.current.id } })
      await api.put('/jilu/finish', null, { params: { id: this.current.id } })
      this.savedForm = { ...this.editForm }
      await this.reloadCurrent()
    },
    async remove() {
      const id = this.current.id
      await api.delete('/jilu/delete', { params: { id } })
      this.selectedIds = this.selectedIds.filter((x) => x !== id)
      this.clearCurrent()
      const lastPage = Math.max(Math.ceil((this.total - 1) / this.size), 1)
      if (this.page > lastPage) this.page = lastPage
      await this.loadList()
    },
    async batchRemove() {
      const removedCount = this.selectedIds.length
      await api.delete('/jilu/deletebatch', { data: [...this.selectedIds] })
      if (this.current && this.selectedIds.includes(this.current.id)) this.clearCurrent()
      this.selectedIds = []
      const lastPage = Math.max(Math.ceil((this.total - removedCount) / this.size), 1)
      if (this.page > lastPage) this.page = lastPage
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
      this.savedForm = emptyForm()
    },
    reset({ loading = false } = {}) {
      this.list = []
      this.loading = loading
      this.total = 0
      this.page = 1
      this.filters = { guanjianci: '', ticai: '', zhuangtai: '' }
      this.selectedIds = []
      this.clearCurrent()
    },
  },
})
