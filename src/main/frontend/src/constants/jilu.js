export const TICAI_OPTIONS = [
  { label: '其他', value: 'QITA' },
  { label: '日记', value: 'RIJI' },
  { label: '文学', value: 'WENXUE' },
  { label: '学术', value: 'XUESHU' },
  { label: '会议', value: 'HUIYI' },
]

export const TICAI_LABELS = Object.fromEntries(
  TICAI_OPTIONS.map(({ label, value }) => [value, label]),
)

export const JILU_STATUS_LABELS = {
  DRAFT: '草稿',
  FINISH: '已完成',
}
