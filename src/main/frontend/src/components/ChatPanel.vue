<template>
  <aside class="chat-panel-card card-col">
    <header class="chat-head">
      <div class="panel-identity">
        <span class="ai-panel-icon" aria-hidden="true">
          <svg viewBox="0 0 24 24">
            <path d="M12 3a5 5 0 0 1 4.8 3.6A4.5 4.5 0 0 1 18 15.4V18a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2v-2.6A4.5 4.5 0 0 1 7.2 6.6 5 5 0 0 1 12 3Z" />
            <path d="M9 12h.01M15 12h.01M9.5 16h5M12 3V1" />
          </svg>
        </span>
        <div>
          <span class="panel-eyebrow">智能协作</span>
          <h2>AI 写作助手</h2>
        </div>
      </div>
      <span
        class="ai-ready"
        :class="{ busy: chat.sending, loading: chat.loading, idle: !chat.currentHuihuaId && !chat.loading }"
      >
        <i></i>{{ aiStatusText }}
      </span>
    </header>

    <div class="session-toolbar">
      <el-select
        :model-value="chat.currentHuihuaId"
        class="session-select"
        placeholder="选择一个会话"
        :disabled="chatBusy"
        @change="onSwitch"
      >
        <el-option
          v-for="(h, i) in chat.huihuaList"
          :key="h.id"
          :label="h.mingcheng || `会话 ${i + 1}`"
          :value="h.id"
        />
      </el-select>
      <el-button
        v-if="chat.huihuaList.length"
        type="primary"
        plain
        :disabled="chatBusy"
        @click="handleNew"
      >+ 新会话</el-button>
      <div v-if="chat.currentHuihuaId" class="session-tools">
        <el-button
          text
          type="danger"
          :disabled="!chat.currentHuihuaId || chatBusy"
          @click="handleDel"
        >
          <svg viewBox="0 0 24 24" aria-hidden="true">
            <path d="M4 7h16M9 7V4h6v3M7 7l1 14h8l1-14M10 11v6M14 11v6" />
          </svg>
          删除
        </el-button>
      </div>
    </div>

    <div ref="msgBox" v-loading="chat.loading" class="message-list">
      <div
        v-for="(m, idx) in chat.messages"
        :key="m.id ?? idx"
        class="message-row"
        :class="m.type === 'USER' ? 'user' : m.type === 'ASSISTANT' ? 'ai' : 'system'"
      >
        <span v-if="m.type === 'ASSISTANT'" class="message-avatar ai-avatar">AI</span>
        <div class="bubble">
          <span v-if="m.type !== 'SYSTEM'" class="message-role">
            {{ m.type === 'USER' ? '我' : '北极熊助手' }}
          </span>
          <div v-if="m._candidate" class="candidate-tag">候选正文</div>
          <div class="bubble-text">{{ m.neirong }}</div>
          <div v-if="m._sending" class="message-send-state sending">
            <i></i>消息已发出，正在等待 AI 回复
          </div>
          <div v-else-if="m._failed" class="message-send-state failed">
            <span>
              <svg viewBox="0 0 20 20" aria-hidden="true">
                <circle cx="10" cy="10" r="7" />
                <path d="M10 6v5M10 14h.01" />
              </svg>
              AI 回复失败
            </span>
            <span class="failed-actions">
              <button type="button" :disabled="chatBusy" @click="retryMessage(m)">重试</button>
              <button type="button" :disabled="chatBusy" @click="restoreMessage(m)">重新编辑</button>
            </span>
          </div>
          <el-button
            v-if="m._candidate"
            class="use-candidate"
            type="primary"
            size="small"
            @click="useCandidate(m.neirong)"
          >使用此版本</el-button>
        </div>
        <span v-if="m.type === 'USER'" class="message-avatar user-avatar">我</span>
      </div>

      <div v-if="!chat.messages.length" class="chat-empty">
        <span class="chat-empty-icon" aria-hidden="true">
          <svg viewBox="0 0 72 72">
            <path d="M16 18h40a8 8 0 0 1 8 8v22a8 8 0 0 1-8 8H34L21 65v-9h-5a8 8 0 0 1-8-8V26a8 8 0 0 1 8-8Z" />
            <path d="m36 27 2.2 6.8L45 36l-6.8 2.2L36 45l-2.2-6.8L27 36l6.8-2.2L36 27ZM51 28v8M47 32h8" />
          </svg>
        </span>
        <h3>{{ chat.huihuaList.length ? '开始今天的创作对话' : '创建一个 AI 会话' }}</h3>
        <p>
          {{ chat.huihuaList.length
            ? '你可以直接发送问题，也可以让 AI 补充思路或根据当前内容生成正文。'
            : '每条记录可以拥有多个独立会话，方便从不同角度梳理内容。' }}
        </p>
        <el-button
          v-if="!chat.huihuaList.length"
          type="primary"
          plain
          :disabled="chatBusy"
          @click="handleNew"
        >
          + 创建会话
        </el-button>
      </div>
    </div>

    <div class="chat-composer">
      <div class="quick-actions">
        <span>快捷操作</span>
        <el-button
          :disabled="!chat.currentHuihuaId || chatBusy"
          @click="handleSend(1)"
        >
          <svg viewBox="0 0 24 24" aria-hidden="true">
            <path d="M8 5H5a2 2 0 0 0-2 2v10a2 2 0 0 0 2 2h11l5 3V7a2 2 0 0 0-2-2h-3M9 9h6M9 13h4M12 2v6M9 5h6" />
          </svg>
          补充提问
        </el-button>
        <el-button
          :disabled="!chat.currentHuihuaId || chatBusy"
          @click="handleSend(2)"
        >
          <svg viewBox="0 0 24 24" aria-hidden="true">
            <path d="m12 3 1.7 5.3L19 10l-5.3 1.7L12 17l-1.7-5.3L5 10l5.3-1.7L12 3ZM19 16l.8 2.2L22 19l-2.2.8L19 22l-.8-2.2L16 19l2.2-.8L19 16Z" />
          </svg>
          生成正文
        </el-button>
      </div>
      <div v-if="failedQuickCommand !== null" class="quick-action-error" role="alert">
        <span>{{ failedQuickCommand === 1 ? '补充提问' : '生成正文' }}失败，本次操作未完成。</span>
        <button type="button" :disabled="chatBusy" @click="retryQuickAction">重试</button>
      </div>
      <el-input
        ref="composerInput"
        v-model="input"
        class="composer-input"
        type="textarea"
        :rows="3"
        maxlength="2000"
        placeholder="说说你想怎样调整这篇内容，或让 AI 帮你梳理思路……"
        @keydown="onKeydown"
      />
      <div class="composer-foot">
        <span><kbd>Ctrl</kbd> + <kbd>Enter</kbd> 快速发送</span>
        <el-button
          type="primary"
          :loading="chat.sending"
          :disabled="!chat.currentHuihuaId || chatBusy"
          @click="handleSend(0)"
        >
          发送消息
          <svg viewBox="0 0 24 24" aria-hidden="true">
            <path d="m22 2-7 20-4-9-9-4 20-7ZM11 13 22 2" />
          </svg>
        </el-button>
      </div>
    </div>
  </aside>
</template>

<script setup>
import { computed, nextTick, ref, watch } from 'vue'
import { ElMessage } from 'element-plus/es/components/message/index.mjs'
import { ElMessageBox } from 'element-plus/es/components/message-box/index.mjs'
import { useChatStore } from '../stores/chat'
import { useJiluStore } from '../stores/jilu'

const chat = useChatStore()
const jilu = useJiluStore()
const emit = defineEmits(['show-editor'])
const input = ref('')
const failedQuickCommand = ref(null)
const msgBox = ref(null)
const composerInput = ref(null)
const draftCache = new Map()
const chatBusy = computed(() => chat.sending || chat.loading)
const aiStatusText = computed(() => {
  if (chat.sending) return '正在思考'
  if (chat.loading) return '正在加载'
  return chat.currentHuihuaId ? '会话可用' : '等待会话'
})
const draftKey = computed(() => {
  const recordId = jilu.current?.id
  if (!recordId) return null
  return `${recordId}:${chat.currentHuihuaId || 'new'}`
})

watch(
  draftKey,
  (newKey, oldKey) => {
    if (oldKey) draftCache.set(oldKey, input.value)
    input.value = newKey ? (draftCache.get(newKey) || '') : ''
    failedQuickCommand.value = null
  },
  { immediate: true },
)

watch(input, (value) => {
  if (draftKey.value) draftCache.set(draftKey.value, value)
})

watch(
  () => chat.messages.length,
  async () => {
    await nextTick()
    if (msgBox.value) msgBox.value.scrollTop = msgBox.value.scrollHeight
  },
)

async function onSwitch(id) {
  if (chatBusy.value || id === chat.currentHuihuaId) return
  chat.currentHuihuaId = id
  try {
    await chat.loadMessages()
  } catch {
    /* 拦截器已提示 */
  }
}

async function handleNew() {
  if (!jilu.current || chatBusy.value) return
  try {
    await chat.createHuihua(jilu.current.id)
    ElMessage.success('已创建新会话')
  } catch {
    /* 拦截器已提示 */
  }
}

async function handleDel() {
  if (!chat.currentHuihuaId || chatBusy.value) return
  try {
    await ElMessageBox.confirm('确定删除当前会话？该会话的全部消息会一并删除。', '删除会话', {
      type: 'warning',
      confirmButtonText: '删除',
      cancelButtonText: '取消',
    })
  } catch {
    return
  }
  try {
    await chat.removeHuihua(jilu.current.id)
    ElMessage.success('会话已删除')
  } catch {
    /* 拦截器已提示 */
  }
}

async function handleSend(command) {
  if (chatBusy.value) return
  if (!chat.currentHuihuaId) {
    ElMessage.warning('请先创建一个会话')
    return
  }
  const zhengwen = getValidContent()
  if (zhengwen === null) return
  let neirong = null
  if (command === 0) {
    neirong = input.value.trim()
    if (!neirong) {
      ElMessage.warning('请输入消息内容')
      return
    }
    // 点击发送后立即交给消息列表，不等待耗时的 AI 请求结束。
    input.value = ''
  }
  try {
    if (command !== 0) failedQuickCommand.value = null
    await chat.send({ command, neirong, zhengwen })
  } catch {
    if (command !== 0) failedQuickCommand.value = command
    /* 拦截器已提示 */
  }
}

function retryQuickAction() {
  if (failedQuickCommand.value === null) return
  handleSend(failedQuickCommand.value)
}

async function retryMessage(message) {
  if (chatBusy.value || !chat.currentHuihuaId) return
  const zhengwen = getValidContent()
  if (zhengwen === null) return
  try {
    await chat.send({
      command: 0,
      neirong: message.neirong,
      zhengwen,
      retryMessageId: message.id,
    })
  } catch {
    /* 拦截器已提示 */
  }
}

async function restoreMessage(message) {
  input.value = message.neirong || ''
  chat.removeLocalMessage(message.id)
  await nextTick()
  composerInput.value?.focus()
}

async function useCandidate(text) {
  const willOverwriteDraft = (
    jilu.editForm.zhengwen !== jilu.savedForm.zhengwen
    && jilu.editForm.zhengwen !== text
  )
  if (willOverwriteDraft) {
    try {
      await ElMessageBox.confirm(
        '当前正文有未保存修改，使用 AI 版本会覆盖这些正文修改。',
        '替换当前正文',
        {
          type: 'warning',
          confirmButtonText: '使用 AI 版本',
          cancelButtonText: '取消',
        },
      )
    } catch {
      return
    }
  }
  jilu.useCandidate(text)
  emit('show-editor')
  ElMessage.success('已放入编辑器，确认无误后点击「保存」')
}

function getValidContent() {
  const zhengwen = jilu.editForm.zhengwen
  if (!zhengwen.trim()) {
    ElMessage.warning('正文为空时无法与 AI 交流，请先在编辑器中写点内容')
    return null
  }
  if (zhengwen.length > 2000) {
    ElMessage.warning('正文不能超过2000字')
    return null
  }
  return zhengwen
}

function onKeydown(e) {
  if (e.key === 'Enter' && (e.ctrlKey || e.metaKey)) {
    e.preventDefault()
    handleSend(0)
  }
}
</script>

<style scoped>
.chat-panel-card {
  display: flex;
  flex-direction: column;
  min-width: 0;
  min-height: 0;
  overflow: hidden;
}

.chat-head {
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

.ai-panel-icon {
  display: grid;
  width: 40px;
  height: 40px;
  color: #6b63c7;
  background: #f0efff;
  border-radius: 12px;
  place-items: center;
}

.ai-panel-icon svg {
  width: 22px;
  fill: none;
  stroke: currentColor;
  stroke-linecap: round;
  stroke-linejoin: round;
  stroke-width: 1.7;
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

.ai-ready {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: #23805d;
  font-size: 11px;
  font-weight: 600;
}

.ai-ready i {
  width: 7px;
  height: 7px;
  background: #35ad7e;
  border: 2px solid #d8f2e7;
  border-radius: 50%;
  box-sizing: content-box;
}

.ai-ready.busy {
  color: #9a680d;
}

.ai-ready.busy i {
  background: #e9a92f;
  border-color: #fff0ce;
  animation: status-pulse 1.1s ease-in-out infinite;
}

.ai-ready.loading {
  color: var(--primary);
}

.ai-ready.loading i {
  background: var(--primary);
  border-color: #dcecf9;
  animation: status-pulse 1.1s ease-in-out infinite;
}

.ai-ready.idle {
  color: var(--ink-muted);
}

.ai-ready.idle i {
  background: #aeb9c4;
  border-color: #edf1f4;
}

@keyframes status-pulse {
  50% {
    opacity: .45;
    transform: scale(.8);
  }
}

.session-toolbar {
  display: flex;
  flex: 0 0 auto;
  align-items: center;
  gap: 8px;
  min-height: 60px;
  padding: 10px 16px;
  background: #fbfdff;
  border-bottom: 1px solid var(--line-soft);
}

.session-select {
  width: 160px;
}

.session-tools {
  display: flex;
  align-items: center;
  gap: 2px;
  margin-left: auto;
}

.session-tools :deep(.el-button + .el-button) {
  margin-left: 0;
}

.session-tools svg,
.quick-actions svg,
.composer-foot .el-button svg {
  width: 16px;
  margin-right: 4px;
  fill: none;
  stroke: currentColor;
  stroke-linecap: round;
  stroke-linejoin: round;
  stroke-width: 1.7;
}

.message-list {
  display: flex;
  flex: 1;
  flex-direction: column;
  gap: 18px;
  min-height: 0;
  padding: 22px 20px;
  overflow-y: auto;
  background:
    radial-gradient(circle at 10% 0%, rgba(223, 239, 252, .52), transparent 30%),
    #f7fafc;
}

.message-row {
  display: flex;
  align-items: flex-start;
  gap: 9px;
}

.message-row.user {
  justify-content: flex-end;
}

.message-row.system {
  justify-content: center;
}

.message-avatar {
  display: grid;
  flex: 0 0 auto;
  width: 29px;
  height: 29px;
  border-radius: 9px;
  font-size: 10px;
  font-weight: 700;
  place-items: center;
}

.ai-avatar {
  color: #625bb9;
  background: #e9e7ff;
}

.user-avatar {
  color: #fff;
  background: var(--primary);
}

.bubble {
  max-width: calc(86% - 38px);
  padding: 10px 13px 12px;
  color: var(--ink);
  background: #fff;
  border: 1px solid var(--line-soft);
  border-radius: 4px 14px 14px 14px;
  box-shadow: 0 5px 16px rgba(43, 69, 94, .05);
  font-size: 13px;
  line-height: 1.75;
  white-space: pre-wrap;
  word-break: break-word;
}

.message-row.user .bubble {
  color: #fff;
  background: linear-gradient(135deg, #3b82c4 0%, #2869aa 100%);
  border: 0;
  border-radius: 14px 4px 14px 14px;
  box-shadow: 0 6px 18px rgba(43, 108, 176, .16);
}

.message-row.system .bubble {
  max-width: 90%;
  padding: 4px 10px;
  color: var(--ink-muted);
  background: transparent;
  border: 0;
  box-shadow: none;
  font-size: 11px;
  text-align: center;
}

.message-role {
  display: block;
  margin-bottom: 5px;
  color: var(--ink-muted);
  font-size: 10px;
  line-height: 1.3;
}

.message-row.user .message-role {
  color: rgba(255, 255, 255, .68);
  text-align: right;
}

.bubble-text {
  white-space: pre-wrap;
}

.message-send-state {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 5px;
  margin-top: 7px;
  color: rgba(255, 255, 255, .72);
  font-size: 10px;
  line-height: 1.4;
}

.message-send-state.sending i {
  width: 9px;
  height: 9px;
  border: 1.5px solid rgba(255, 255, 255, .35);
  border-top-color: #fff;
  border-radius: 50%;
  animation: message-spin .8s linear infinite;
}

.message-send-state.failed {
  align-items: flex-end;
  flex-direction: column;
  gap: 4px;
  color: #ffe0dc;
}

.message-send-state.failed > span:first-child {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.message-send-state.failed svg {
  width: 13px;
  fill: none;
  stroke: currentColor;
  stroke-linecap: round;
  stroke-linejoin: round;
  stroke-width: 1.7;
}

.failed-actions {
  display: inline-flex;
  gap: 9px;
}

.failed-actions button {
  padding: 0;
  color: #fff;
  background: transparent;
  border: 0;
  border-bottom: 1px solid rgba(255, 255, 255, .55);
  cursor: pointer;
  font-size: 10px;
}

.failed-actions button:disabled {
  cursor: not-allowed;
  opacity: .5;
}

@keyframes message-spin {
  to {
    transform: rotate(360deg);
  }
}

.candidate-tag {
  display: inline-flex;
  align-items: center;
  padding: 2px 8px;
  margin-bottom: 7px;
  color: #257957;
  background: #e4f5ec;
  border-radius: 999px;
  font-size: 10px;
  font-weight: 600;
}

.use-candidate {
  margin-top: 10px;
}

.chat-empty {
  display: flex;
  flex: 1;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 240px;
  padding: 24px 12px;
  text-align: center;
}

.chat-empty-icon {
  display: grid;
  width: 86px;
  height: 86px;
  margin-bottom: 18px;
  color: #7168c9;
  background: linear-gradient(145deg, #f3f2ff, #e9f3ff);
  border: 1px solid #e1e0f8;
  border-radius: 28px;
  place-items: center;
}

.chat-empty-icon svg {
  width: 58px;
  fill: none;
  stroke: currentColor;
  stroke-linecap: round;
  stroke-linejoin: round;
  stroke-width: 1.8;
}

.chat-empty h3 {
  margin: 0 0 7px;
  color: var(--ink);
  font-size: 15px;
}

.chat-empty p {
  max-width: 340px;
  margin: 0 0 17px;
  color: var(--ink-muted);
  font-size: 12px;
  line-height: 1.75;
}

.chat-composer {
  display: flex;
  flex: 0 0 auto;
  flex-direction: column;
  gap: 10px;
  padding: 13px 16px 14px;
  background: #fff;
  border-top: 1px solid var(--line-soft);
}

.quick-actions {
  display: flex;
  align-items: center;
  gap: 7px;
}

.quick-actions > span {
  margin-right: 2px;
  color: var(--ink-muted);
  font-size: 11px;
  white-space: nowrap;
}

.quick-actions :deep(.el-button) {
  height: 28px;
  padding: 0 10px;
  margin-left: 0;
  font-size: 11px;
}

.quick-action-error {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  padding: 7px 10px;
  color: #a2473e;
  background: #fff4f2;
  border: 1px solid #f3d5d1;
  border-radius: 8px;
  font-size: 11px;
}

.quick-action-error button {
  flex: 0 0 auto;
  padding: 0;
  color: #b14239;
  background: transparent;
  border: 0;
  border-bottom: 1px solid currentColor;
  cursor: pointer;
  font-size: 11px;
}

.quick-action-error button:disabled {
  cursor: not-allowed;
  opacity: .5;
}

.composer-input :deep(.el-textarea__inner) {
  min-height: 76px !important;
  padding: 11px 13px;
  background: #f9fbfd;
  border: 0;
  border-radius: 10px;
  box-shadow: 0 0 0 1px var(--line) inset;
  font-family: inherit;
  line-height: 1.65;
  resize: none;
}

.composer-input :deep(.el-textarea__inner:focus) {
  background: #fff;
  box-shadow: 0 0 0 1px var(--primary) inset, 0 0 0 3px rgba(43, 108, 176, .08);
}

.composer-foot {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.composer-foot > span {
  color: var(--ink-muted);
  font-size: 10px;
}

.composer-foot kbd {
  padding: 1px 4px;
  color: #516173;
  background: #f2f4f7;
  border: 1px solid #dfe4ea;
  border-radius: 4px;
  box-shadow: 0 1px 0 #d7dde4;
  font-family: inherit;
  font-size: 9px;
}

.composer-foot .el-button svg {
  margin-right: 0;
  margin-left: 6px;
}

@media (max-width: 680px) {
  .chat-head,
  .session-toolbar,
  .message-list,
  .chat-composer {
    padding-right: 14px;
    padding-left: 14px;
  }

  .session-toolbar {
    flex-wrap: wrap;
  }

  .session-select {
    flex: 1;
    min-width: 150px;
  }

  .session-tools {
    width: 100%;
    justify-content: flex-end;
  }

  .quick-actions {
    flex-wrap: wrap;
  }
}
</style>
