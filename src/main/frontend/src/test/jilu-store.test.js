import { beforeEach, describe, expect, it, vi } from 'vitest'
import { createPinia, setActivePinia } from 'pinia'
import { useJiluStore } from '../stores/jilu'
import api from '../api'

vi.mock('../api', () => ({ default: { get: vi.fn(), put: vi.fn() } }))

describe('jilu store', () => {
  beforeEach(() => setActivePinia(createPinia()))
  it('loads paged records with filters', async () => {
    api.get.mockResolvedValueOnce({ records: [{ id: '1' }], total: 21, pages: 2 })
    const store = useJiluStore()
    store.filters.guanjianci = '日记'
    await store.loadList()
    expect(api.get).toHaveBeenCalledWith('/jilu/getlist', {
      params: { guanjianci: '日记', ticai: undefined, zhuangtai: undefined, page: 1, size: 20 },
    })
    expect(store.list).toHaveLength(1)
    expect(store.total).toBe(21)
  })
  it('moves back to the last page when the current page becomes empty', async () => {
    api.get
      .mockResolvedValueOnce({ records: [], total: 20, pages: 1 })
      .mockResolvedValueOnce({ records: [{ id: '1' }], total: 20, pages: 1 })
    const store = useJiluStore()
    store.page = 2
    await store.loadList()
    expect(store.page).toBe(1)
    expect(api.get).toHaveBeenLastCalledWith('/jilu/getlist', {
      params: { guanjianci: undefined, ticai: undefined, zhuangtai: undefined, page: 1, size: 20 },
    })
    expect(store.list).toEqual([{ id: '1' }])
  })
  it('tracks unsaved form changes through savedForm', async () => {
    api.get.mockResolvedValueOnce({ id: '1', timu: '旧题目', ticai: 'QITA', biaoqian: '', zhengwen: '正文' })
    const store = useJiluStore()
    await store.select('1')
    expect(store.editForm).toEqual(store.savedForm)
    store.editForm.zhengwen = '新正文'
    expect(store.editForm).not.toEqual(store.savedForm)
  })
})
