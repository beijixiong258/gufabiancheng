<template>
  <article class="editor-card card-col">
    <header class="editor-head">
      <div class="panel-identity">
        <span class="panel-icon" aria-hidden="true">
          <svg viewBox="0 0 24 24">
            <path d="M4 20h4l11-11a2.8 2.8 0 0 0-4-4L4 16v4ZM13.5 6.5l4 4M11 20h9" />
          </svg>
        </span>
        <div>
          <span class="panel-eyebrow">内容编辑</span>
          <h2>记录详情</h2>
        </div>
      </div>
      <span
        class="editor-status"
        :class="jilu.current?.jiluZhuangtai === 'FINISH' ? 'finished' : 'draft'"
      >
        <i></i>
        {{ jilu.current?.jiluZhuangtai === 'FINISH' ? '已完成' : '草稿' }}
      </span>
    </header>

    <div class="editor-body">
      <label class="field-caption" for="record-title">记录题目</label>
      <el-input
        id="record-title"
        v-model="jilu.editForm.timu"
        class="editor-title-input"
        maxlength="50"
        placeholder="给这条记录起一个清晰的题目"
      />

      <el-form class="editor-form" label-position="top">
        <el-row :gutter="14">
          <el-col :xs="24" :sm="9">
          <el-form-item label="题材">
            <el-select v-model="jilu.editForm.ticai" class="full-field">
              <el-option
                v-for="option in TICAI_OPTIONS"
                :key="option.value"
                :label="option.label"
                :value="option.value"
              />
            </el-select>
          </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="15">
          <el-form-item label="标签">
            <el-input v-model="jilu.editForm.biaoqian" maxlength="20" placeholder="添加一个便于查找的标签（可选）" />
          </el-form-item>
          </el-col>
        </el-row>

        <el-form-item class="content-form-item">
          <template #label>
            <div class="content-label">
              <span>正文内容</span>
              <small>随时可以让右侧 AI 帮你继续构思</small>
            </div>
          </template>
          <el-input
            v-model="jilu.editForm.zhengwen"
            class="content-textarea"
            type="textarea"
            :rows="15"
            maxlength="2000"
            placeholder="从这里开始写作……&#10;&#10;不必一开始就追求完整，先把想到的内容写下来，再慢慢整理。"
          />
        </el-form-item>
      </el-form>
    </div>

    <footer class="editor-foot">
      <div class="editor-save-state">
        <span class="zhengwen-count">{{ jilu.editForm.zhengwen.length }} / 2000 字</span>
        <span v-if="dirty" class="dirty-tip"><i></i>有未保存修改</span>
        <span v-else class="saved-tip">
          <svg viewBox="0 0 20 20" aria-hidden="true"><path d="m4 10 4 4 8-8" /></svg>
          内容已同步
        </span>
      </div>
      <div class="editor-actions">
        <el-button type="danger" plain :loading="deleting" :disabled="busy" @click="handleDelete">删除</el-button>
        <el-button
          v-if="jilu.current?.jiluZhuangtai !== 'FINISH'"
          :loading="finishing"
          :disabled="busy"
          @click="handleFinish"
        >标记完成</el-button>
        <el-button type="primary" :loading="saving" :disabled="busy" @click="handleSave">保存修改</el-button>
      </div>
    </footer>
  </article>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus/es/components/message/index.mjs'
import { ElMessageBox } from 'element-plus/es/components/message-box/index.mjs'
import { useJiluStore } from '../stores/jilu'
import { useChatStore } from '../stores/chat'
import { TICAI_OPTIONS } from '../constants/jilu'

const jilu = useJiluStore()
const chat = useChatStore()
const emit = defineEmits(['returned-to-list'])
const saving = ref(false)
const finishing = ref(false)
const deleting = ref(false)
const dirty = computed(() => jilu.hasUnsavedChanges)
const busy = computed(() => saving.value || finishing.value || deleting.value)

function beforeUnload(event) {
  if (!dirty.value) return
  event.preventDefault()
  event.returnValue = ''
}

onMounted(() => window.addEventListener('beforeunload', beforeUnload))
onBeforeUnmount(() => window.removeEventListener('beforeunload', beforeUnload))

async function handleSave() {
  if (busy.value) return
  if (!jilu.editForm.timu.trim()) {
    ElMessage.warning('题目不能为空')
    return
  }
  saving.value = true
  try {
    await jilu.save()
    returnToList('已保存，已返回记录列表')
  } catch {
    /* 拦截器已提示 */
  } finally {
    saving.value = false
  }
}

async function handleFinish() {
  if (busy.value) return
  if (!jilu.editForm.timu.trim()) {
    ElMessage.warning('题目不能为空')
    return
  }
  if (!jilu.editForm.zhengwen.trim()) {
    ElMessage.warning('正文为空，无法标记完成')
    return
  }
  finishing.value = true
  try {
    await jilu.finish()
    returnToList('已保存并标记完成，已返回记录列表')
  } catch {
    /* 拦截器已提示 */
  } finally {
    finishing.value = false
  }
}

function returnToList(message) {
  jilu.clearCurrent()
  chat.reset()
  emit('returned-to-list')
  ElMessage.success(message)
}

async function handleDelete() {
  if (busy.value) return
  try {
    await ElMessageBox.confirm('确定删除这条记录？其会话和消息会一并删除。', '删除记录', {
      type: 'warning',
      confirmButtonText: '删除',
      cancelButtonText: '取消',
    })
  } catch {
    return
  }
  deleting.value = true
  try {
    await jilu.remove()
    chat.reset()
    emit('returned-to-list')
    ElMessage.success('已删除')
  } catch {
    /* 拦截器已提示 */
  } finally {
    deleting.value = false
  }
}
</script>

<style scoped>
.editor-card {
  display: flex;
  flex-direction: column;
  min-width: 0;
  min-height: 0;
  overflow: hidden;
}

.editor-head {
  display: flex;
  flex: 0 0 auto;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  min-height: 78px;
  padding: 16px 20px;
  border-bottom: 1px solid var(--line-soft);
}

.panel-identity {
  display: flex;
  align-items: center;
  gap: 11px;
}

.panel-icon {
  display: grid;
  width: 40px;
  height: 40px;
  color: var(--primary);
  background: var(--primary-soft);
  border-radius: 12px;
  place-items: center;
}

.panel-icon svg {
  width: 21px;
  fill: none;
  stroke: currentColor;
  stroke-linecap: round;
  stroke-linejoin: round;
  stroke-width: 1.8;
}

.panel-eyebrow {
  display: block;
  margin-bottom: 1px;
  color: var(--ink-muted);
  font-size: 11px;
  letter-spacing: .08em;
}

.panel-identity h2 {
  margin: 0;
  color: var(--ink);
  font-size: 17px;
  line-height: 1.35;
}

.editor-status {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 5px 10px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 600;
}

.editor-status i {
  width: 6px;
  height: 6px;
  background: currentColor;
  border-radius: 50%;
}

.editor-status.draft {
  color: #9d610e;
  background: #fff5df;
}

.editor-status.finished {
  color: #187d58;
  background: #e6f6ee;
}

.editor-body {
  flex: 1;
  min-height: 0;
  padding: 19px 20px 6px;
  overflow-y: auto;
}

.field-caption {
  display: block;
  margin-bottom: 7px;
  color: #3b4b5c;
  font-size: 13px;
  font-weight: 600;
}

.editor-title-input {
  margin-bottom: 18px;
}

.editor-title-input :deep(.el-input__wrapper) {
  padding: 3px 13px;
  background: #fbfdff;
  box-shadow: 0 0 0 1px var(--line) inset;
}

.editor-title-input :deep(.el-input__inner) {
  height: 38px;
  color: var(--ink);
  font-size: 18px;
  font-weight: 650;
}

.editor-form :deep(.el-form-item__label) {
  padding-bottom: 7px;
  color: #3b4b5c;
  font-size: 13px;
  font-weight: 600;
  line-height: 1.4;
}

.editor-form :deep(.el-form-item) {
  margin-bottom: 17px;
}

.full-field {
  width: 100%;
}

.content-form-item {
  margin-bottom: 8px !important;
}

.content-label {
  display: flex;
  width: 100%;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.content-label small {
  color: var(--ink-muted);
  font-size: 11px;
  font-weight: 400;
}

.content-textarea :deep(.el-textarea__inner) {
  min-height: 354px !important;
  padding: 16px 17px;
  color: #2a3a4a;
  background: #fbfdff;
  border: 0;
  border-radius: 10px;
  box-shadow: 0 0 0 1px var(--line) inset;
  font-family: inherit;
  font-size: 14px;
  line-height: 1.9;
  resize: none;
}

.content-textarea :deep(.el-textarea__inner:focus) {
  background: #fff;
  box-shadow: 0 0 0 1px var(--primary) inset, 0 0 0 3px rgba(43, 108, 176, .08);
}

.editor-foot {
  display: flex;
  flex: 0 0 auto;
  align-items: center;
  justify-content: space-between;
  gap: 18px;
  min-height: 70px;
  padding: 12px 20px;
  background: #fbfdff;
  border-top: 1px solid var(--line-soft);
}

.editor-save-state {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 0;
}

.zhengwen-count {
  color: var(--ink-muted);
  font-size: 11px;
  white-space: nowrap;
}

.dirty-tip {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  color: #a85f00;
  font-size: 11px;
  white-space: nowrap;
}

.dirty-tip i {
  width: 6px;
  height: 6px;
  background: #f0a020;
  border-radius: 50%;
}

.saved-tip {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  color: #438266;
  font-size: 11px;
  white-space: nowrap;
}

.saved-tip svg {
  width: 14px;
  fill: none;
  stroke: currentColor;
  stroke-linecap: round;
  stroke-linejoin: round;
  stroke-width: 2;
}

.editor-actions {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-end;
  gap: 8px;
}

.editor-actions :deep(.el-button + .el-button) {
  margin-left: 0;
}

@media (max-width: 680px) {
  .editor-head,
  .editor-body,
  .editor-foot {
    padding-right: 15px;
    padding-left: 15px;
  }

  .editor-foot {
    align-items: flex-start;
    flex-direction: column;
  }

  .editor-actions {
    width: 100%;
  }
}
</style>
