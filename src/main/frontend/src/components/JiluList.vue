<template>
  <aside>
    <div class="sidebar-head">
      <h2>我的记录</h2>
      <el-button type="primary" size="small" @click="ui.newJilu = true">+ 新建</el-button>
    </div>

    <div class="list-filters">
      <el-input v-model="jilu.filters.guanjianci" clearable size="small" placeholder="搜索题目或标签" @keyup.enter="reload" />
      <el-select v-model="jilu.filters.ticai" clearable size="small" placeholder="题材" @change="reload">
        <el-option v-for="(label, value) in TICAI" :key="value" :label="label" :value="value" />
      </el-select>
      <el-select v-model="jilu.filters.zhuangtai" clearable size="small" placeholder="状态" @change="reload">
        <el-option v-for="(label, value) in STATUS" :key="value" :label="label" :value="value" />
      </el-select>
    </div>

    <div v-if="jilu.selectedIds.length" class="batch-bar">
      <el-checkbox
        :model-value="allChecked"
        @change="toggleAll"
      >已选 {{ jilu.selectedIds.length }} 条</el-checkbox>
      <el-button type="danger" size="small" @click="handleBatchDelete">删除所选</el-button>
    </div>

    <ul class="jilu-list">
      <li
        v-for="j in jilu.list"
        :key="j.id"
        class="jilu-item"
        :class="{ active: j.id === jilu.current?.id }"
        @click="onSelect(j.id)"
      >
        <el-checkbox
          :model-value="jilu.selectedIds.includes(j.id)"
          @click.stop
          @change="(v) => toggleOne(j.id, v)"
        />
        <div class="jilu-main">
          <div class="jilu-title">{{ j.timu }}</div>
          <div class="jilu-meta">
            <el-tag size="small" type="primary">{{ TICAI[j.ticai] || j.ticai }}</el-tag>
            <el-tag v-if="j.biaoqian" size="small" type="info">{{ j.biaoqian }}</el-tag>
            <el-tag size="small" :type="j.jiluZhuangtai === 'FINISH' ? 'success' : 'warning'">
              {{ STATUS[j.jiluZhuangtai] || j.jiluZhuangtai }}
            </el-tag>
          </div>
          <div class="jilu-time">{{ fmt(j.xiugaiShijian) }}</div>
        </div>
      </li>
    </ul>

    <el-empty v-if="!jilu.list.length" description="还没有记录，点击「+ 新建」开始" :image-size="80" />
    <el-pagination
      v-if="jilu.total > jilu.size"
      :current-page="jilu.page"
      :page-size="jilu.size"
      :total="jilu.total"
      small
      layout="prev, pager, next"
      @current-change="jilu.changePage"
    />
  </aside>
</template>

<script setup>
import { computed } from 'vue'
import { ElMessage } from 'element-plus/es/components/message/index.mjs'
import { ElMessageBox } from 'element-plus/es/components/message-box/index.mjs'
import { useJiluStore } from '../stores/jilu'
import { useChatStore } from '../stores/chat'
import { useUiStore } from '../stores/ui'

const jilu = useJiluStore()
const chat = useChatStore()
const ui = useUiStore()

const TICAI = { QITA: '其他', RIJI: '日记', WENXUE: '文学', XUESHU: '学术', HUIYI: '会议' }
const STATUS = { DRAFT: '草稿', FINISH: '已完成' }

const allChecked = computed(
  () => jilu.list.length > 0 && jilu.list.every((record) => jilu.selectedIds.includes(record.id)),
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

async function onSelect(id) {
  if (jilu.current && JSON.stringify(jilu.editForm) !== JSON.stringify(jilu.savedForm)) {
    try {
      await ElMessageBox.confirm('当前记录有未保存修改，仍要切换吗？', '未保存修改', {
        type: 'warning',
        confirmButtonText: '放弃并切换',
        cancelButtonText: '取消',
      })
    } catch {
      return
    }
  }
  try {
    await jilu.select(id)
    await chat.loadHuihua(id)
  } catch {
    /* 拦截器已提示 */
  }
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
.list-filters {
  display: grid;
  grid-template-columns: 1fr 88px 88px;
  gap: 6px;
  margin-bottom: 10px;
}

.batch-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 6px 8px;
  margin-bottom: 8px;
  background: #fdf3f2;
  border: 1px solid #f2d4d2;
  border-radius: 8px;
}
</style>
