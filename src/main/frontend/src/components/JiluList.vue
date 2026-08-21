<template>
  <aside v-loading="jilu.loading" class="jilu-sidebar">
    <div class="sidebar-head">
      <div class="sidebar-title">
        <span class="sidebar-title-icon" aria-hidden="true">
          <svg viewBox="0 0 24 24">
            <path d="M6 3h9l4 4v14H6a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2Z" />
            <path d="M14 3v5h5M8 13h7M8 17h5" />
          </svg>
        </span>
        <div>
          <h2>我的记录</h2>
          <p>共 {{ jilu.total }} 条内容</p>
        </div>
      </div>
      <el-button class="new-record-button" type="primary" @click="openNewRecord">
        <span>+</span> 新建
      </el-button>
    </div>

    <div class="list-filters">
      <div class="filter-search-row">
        <el-input
          v-model="jilu.filters.guanjianci"
          clearable
          placeholder="搜索题目或标签"
          @clear="reload"
          @keyup.enter="reload"
        >
          <template #prefix>
            <svg class="search-icon" viewBox="0 0 24 24" aria-hidden="true">
              <circle cx="11" cy="11" r="7" />
              <path d="m20 20-4-4" />
            </svg>
          </template>
        </el-input>
        <el-button class="search-button" @click="reload">搜索</el-button>
      </div>
      <div class="filter-select-row">
        <el-select v-model="jilu.filters.ticai" clearable placeholder="全部题材" @change="reload">
          <el-option v-for="(label, value) in TICAI" :key="value" :label="label" :value="value" />
        </el-select>
        <el-select v-model="jilu.filters.zhuangtai" clearable placeholder="全部状态" @change="reload">
          <el-option v-for="(label, value) in STATUS" :key="value" :label="label" :value="value" />
        </el-select>
      </div>
    </div>

    <div v-if="jilu.selectedIds.length" class="batch-bar">
      <el-checkbox
        :model-value="allChecked"
        @change="toggleAll"
      >已选 {{ jilu.selectedIds.length }} 条</el-checkbox>
      <el-button type="danger" link @click="handleBatchDelete">删除所选</el-button>
    </div>

    <div v-else-if="jilu.list.length" class="list-summary">
      <span>最近修改</span>
      <el-checkbox :model-value="allChecked" @change="toggleAll">全选本页</el-checkbox>
    </div>

    <ul v-if="jilu.list.length" class="jilu-list">
      <li
        v-for="j in jilu.list"
        :key="j.id"
        class="jilu-item"
        :class="{ active: j.id === jilu.current?.id }"
      >
        <el-checkbox
          :model-value="jilu.selectedIds.includes(j.id)"
          :aria-label="`选择记录：${j.timu}`"
          @click.stop
          @change="(v) => toggleOne(j.id, v)"
        />
        <button
          class="record-open-button"
          type="button"
          :aria-current="j.id === jilu.current?.id ? 'true' : undefined"
          :aria-label="`打开记录：${j.timu}`"
          @click="onSelect(j.id)"
        >
          <div class="jilu-main">
            <div class="jilu-item-head">
              <div class="jilu-title" :title="j.timu">{{ j.timu }}</div>
              <span
                class="record-state"
                :class="j.jiluZhuangtai === 'FINISH' ? 'finished' : 'draft'"
              >
                <i></i>{{ STATUS[j.jiluZhuangtai] || j.jiluZhuangtai }}
              </span>
            </div>
            <div class="jilu-meta">
              <span class="meta-chip type-chip">{{ TICAI[j.ticai] || j.ticai }}</span>
              <span v-if="j.biaoqian" class="meta-chip label-chip"># {{ j.biaoqian }}</span>
            </div>
            <div class="jilu-time">
              <svg viewBox="0 0 24 24" aria-hidden="true">
                <circle cx="12" cy="12" r="9" />
                <path d="M12 7v5l3 2" />
              </svg>
              {{ fmt(j.xiugaiShijian) }}
            </div>
          </div>
          <svg class="item-chevron" viewBox="0 0 20 20" aria-hidden="true">
            <path d="m8 5 5 5-5 5" />
          </svg>
        </button>
      </li>
    </ul>

    <div v-else class="list-empty">
      <span class="empty-record-icon" aria-hidden="true">
        <svg viewBox="0 0 72 72">
          <path d="M17 12h27l11 11v37H17a5 5 0 0 1-5-5V17a5 5 0 0 1 5-5Z" />
          <path d="M43 12v13h12M24 37h19M24 46h14" />
          <circle cx="55" cy="53" r="12" />
          <path d="M55 47v12M49 53h12" />
        </svg>
      </span>
      <h3>{{ hasFilters ? '没有找到匹配的记录' : '这里还没有记录' }}</h3>
      <p>{{ hasFilters ? '试试更换关键词或筛选条件' : '点击右上角「新建」，开始整理你的第一个想法' }}</p>
      <el-button v-if="hasFilters" @click="clearFilters">清除筛选</el-button>
    </div>

    <div v-if="jilu.total > jilu.size" class="list-pagination">
      <el-pagination
        :current-page="jilu.page"
        :page-size="jilu.size"
        :total="jilu.total"
        small
        layout="prev, pager, next"
        @current-change="jilu.changePage"
      />
    </div>
  </aside>
</template>

<script setup>
import { computed } from 'vue'
import { ElMessage } from 'element-plus/es/components/message/index.mjs'
import { ElMessageBox } from 'element-plus/es/components/message-box/index.mjs'
import { useJiluStore } from '../stores/jilu'
import { useChatStore } from '../stores/chat'
import { useUiStore } from '../stores/ui'
import { useRecordLeaveGuard } from '../composables/useRecordLeaveGuard'
import { JILU_STATUS_LABELS, TICAI_LABELS } from '../constants/jilu'

const jilu = useJiluStore()
const chat = useChatStore()
const ui = useUiStore()
const emit = defineEmits(['record-opened'])
const { confirmLeave } = useRecordLeaveGuard()

const TICAI = TICAI_LABELS
const STATUS = JILU_STATUS_LABELS

const allChecked = computed(
  () => jilu.list.length > 0 && jilu.list.every((record) => jilu.selectedIds.includes(record.id)),
)
const hasFilters = computed(
  () => Boolean(jilu.filters.guanjianci || jilu.filters.ticai || jilu.filters.zhuangtai),
)

function fmt(v) {
  if (!v) return ''
  const d = new Date(v)
  if (Number.isNaN(d.getTime())) return String(v)
  const p = (n) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${p(d.getMonth() + 1)}-${p(d.getDate())} ${p(d.getHours())}:${p(d.getMinutes())}`
}

function toggleOne(id, checked) {
  if (checked) jilu.selectedIds.push(id)
  else jilu.selectedIds = jilu.selectedIds.filter((x) => x !== id)
}

function toggleAll(checked) {
  jilu.selectedIds = checked ? jilu.list.map((j) => j.id) : []
}

function reload() {
  jilu.loadList(true).catch(() => {})
}

function clearFilters() {
  jilu.filters.guanjianci = ''
  jilu.filters.ticai = ''
  jilu.filters.zhuangtai = ''
  reload()
}

async function onSelect(id) {
  if (id === jilu.current?.id) return
  const canLeave = await confirmLeave({
    message: '当前记录有未保存修改，仍要切换到其他记录吗？',
    confirmButtonText: '放弃并切换',
  })
  if (!canLeave) return
  try {
    await jilu.select(id)
    await chat.loadHuihua(id)
    emit('record-opened')
  } catch {
    /* 拦截器已提示 */
  }
}

async function openNewRecord() {
  const canLeave = await confirmLeave({
    message: '当前记录有未保存修改，仍要新建记录吗？',
    confirmButtonText: '放弃并新建',
  })
  if (canLeave) ui.newJilu = true
}

async function handleBatchDelete() {
  const n = jilu.selectedIds.length
  if (!n) return
  try {
    await ElMessageBox.confirm(
      `确定删除选中的 ${n} 条记录？其会话和消息会一并删除。`,
      '批量删除',
      { type: 'warning', confirmButtonText: '删除', cancelButtonText: '取消' },
    )
  } catch {
    return
  }
  try {
    await jilu.batchRemove()
    ElMessage.success('已删除所选记录')
  } catch {
    /* 拦截器已提示 */
  }
}
</script>

<style scoped>
.jilu-sidebar {
  display: flex;
  flex-direction: column;
  min-height: 0;
  padding: 20px;
  overflow: hidden;
}

.sidebar-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 14px;
  margin-bottom: 20px;
}

.sidebar-title {
  display: flex;
  align-items: center;
  min-width: 0;
  gap: 11px;
}

.sidebar-title-icon {
  display: grid;
  flex: 0 0 auto;
  width: 38px;
  height: 38px;
  color: var(--primary);
  background: var(--primary-soft);
  border-radius: 11px;
  place-items: center;
}

.sidebar-title-icon svg {
  width: 20px;
  fill: none;
  stroke: currentColor;
  stroke-linecap: round;
  stroke-linejoin: round;
  stroke-width: 1.8;
}

.sidebar-title h2 {
  margin: 0;
  color: var(--ink);
  font-size: 17px;
  line-height: 1.35;
}

.sidebar-title p {
  margin: 2px 0 0;
  color: var(--ink-muted);
  font-size: 12px;
  line-height: 1.35;
}

.new-record-button {
  min-width: 78px;
  font-weight: 600;
}

.new-record-button span {
  margin-right: 2px;
  font-size: 18px;
  font-weight: 400;
  line-height: 1;
}

.list-filters {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--line-soft);
}

.filter-search-row {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  gap: 8px;
}

.filter-select-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 8px;
}

.filter-select-row :deep(.el-select) {
  width: 100%;
}

.search-icon {
  width: 17px;
  fill: none;
  stroke: currentColor;
  stroke-linecap: round;
  stroke-width: 1.8;
}

.search-button {
  padding-right: 15px;
  padding-left: 15px;
}

.list-summary {
  display: flex;
  align-items: center;
  justify-content: space-between;
  min-height: 44px;
  color: var(--ink-muted);
  font-size: 12px;
}

.list-summary :deep(.el-checkbox__label) {
  color: var(--ink-muted);
  font-size: 12px;
}

.batch-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  min-height: 44px;
  padding: 0 4px 0 10px;
  margin: 8px 0;
  background: #fff7f6;
  border: 1px solid #f5dbd8;
  border-radius: 10px;
}

.batch-bar :deep(.el-checkbox__label) {
  font-size: 12px;
}

.jilu-list {
  display: flex;
  flex: 1;
  flex-direction: column;
  gap: 9px;
  min-height: 0;
  padding: 1px 4px 1px 0;
  margin: 0;
  overflow-y: auto;
  list-style: none;
}

.jilu-item {
  position: relative;
  display: flex;
  gap: 10px;
  padding: 13px 11px;
  background: var(--card);
  border: 1px solid var(--line-soft);
  border-radius: 12px;
  transition: border-color .18s ease, box-shadow .18s ease, transform .18s ease, background .18s ease;
}

.jilu-item:hover {
  border-color: #a9c9e8;
  box-shadow: 0 8px 20px rgba(45, 91, 130, .08);
  transform: translateY(-1px);
}

.record-open-button:focus-visible {
  outline: 3px solid rgba(43, 108, 176, .18);
  outline-offset: 1px;
}

.record-open-button {
  display: flex;
  flex: 1;
  align-items: stretch;
  min-width: 0;
  padding: 0;
  color: inherit;
  text-align: left;
  background: transparent;
  border: 0;
  cursor: pointer;
}

.jilu-item.active {
  background: linear-gradient(135deg, #f1f8ff 0%, #f8fbff 100%);
  border-color: var(--primary);
  box-shadow: 0 8px 24px rgba(43, 108, 176, .12);
}

.jilu-item.active::before {
  position: absolute;
  top: 13px;
  bottom: 13px;
  left: -1px;
  width: 3px;
  background: var(--primary);
  border-radius: 0 4px 4px 0;
  content: '';
}

.jilu-main {
  flex: 1;
  min-width: 0;
}

.jilu-item-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 8px;
}

.jilu-title {
  min-width: 0;
  overflow: hidden;
  color: var(--ink);
  font-size: 14px;
  font-weight: 650;
  line-height: 1.45;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.record-state {
  display: inline-flex;
  flex: 0 0 auto;
  align-items: center;
  gap: 5px;
  padding: 2px 7px;
  border-radius: 999px;
  font-size: 10px;
  line-height: 1.5;
}

.record-state i {
  width: 5px;
  height: 5px;
  background: currentColor;
  border-radius: 50%;
}

.record-state.draft {
  color: #a86308;
  background: #fff5df;
}

.record-state.finished {
  color: #20845e;
  background: #e8f7f0;
}

.jilu-meta {
  display: flex;
  gap: 6px;
  min-height: 22px;
  margin: 9px 0 8px;
  overflow: hidden;
}

.meta-chip {
  display: inline-block;
  max-width: 125px;
  padding: 2px 8px;
  overflow: hidden;
  border-radius: 6px;
  font-size: 11px;
  line-height: 18px;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.type-chip {
  color: #376f9f;
  background: #eaf4fd;
}

.label-chip {
  color: #6b7280;
  background: #f2f4f7;
}

.jilu-time {
  display: flex;
  align-items: center;
  gap: 5px;
  color: var(--ink-muted);
  font-size: 11px;
  line-height: 1.4;
}

.jilu-time svg {
  width: 13px;
  fill: none;
  stroke: currentColor;
  stroke-linecap: round;
  stroke-linejoin: round;
  stroke-width: 1.8;
}

.item-chevron {
  align-self: center;
  width: 15px;
  color: #b7c3cf;
  fill: none;
  stroke: currentColor;
  stroke-linecap: round;
  stroke-linejoin: round;
  stroke-width: 1.7;
  transition: color .18s ease, transform .18s ease;
}

.jilu-item:hover .item-chevron,
.jilu-item.active .item-chevron {
  color: var(--primary);
  transform: translateX(2px);
}

.list-empty {
  display: flex;
  flex: 1;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 250px;
  padding: 30px 14px;
  text-align: center;
}

.empty-record-icon {
  display: grid;
  width: 86px;
  height: 86px;
  margin-bottom: 17px;
  color: #6f9fc8;
  background: #f1f7fd;
  border: 1px solid #dcebf8;
  border-radius: 26px;
  place-items: center;
  transform: rotate(-3deg);
}

.empty-record-icon svg {
  width: 56px;
  fill: none;
  stroke: currentColor;
  stroke-linecap: round;
  stroke-linejoin: round;
  stroke-width: 2;
}

.list-empty h3 {
  margin: 0 0 6px;
  color: var(--ink);
  font-size: 15px;
}

.list-empty p {
  max-width: 220px;
  margin: 0 0 17px;
  color: var(--ink-muted);
  font-size: 12px;
  line-height: 1.7;
}

.list-pagination {
  display: flex;
  justify-content: center;
  padding-top: 14px;
  border-top: 1px solid var(--line-soft);
}
</style>
