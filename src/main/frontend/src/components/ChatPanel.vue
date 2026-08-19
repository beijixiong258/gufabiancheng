<template>
  <div class="chat-panel-card card-col">
    <div class="chat-head">
      <h3>AI 对话</h3>
      <div class="chat-session">
        <el-select
          :model-value="chat.currentHuihuaId"
          placeholder="选择会话"
          size="small"
          @change="onSwitch"
        >
          <el-option
            v-for="(h, i) in chat.huihuaList"
            :key="h.id"
            :label="h.mingcheng || `会话 ${i + 1}`"
            :value="h.id"
          />
        </el-select>
        <el-button size="small" @click="handleNew">新会话</el-button>
        <el-button size="small" :disabled="!chat.currentHuihuaId" @click="handleDel">删除会话</el-button>
        <el-button size="small" @click="handleTest">测试AI</el-button>
      </div>
    </div>

    <div ref="msgBox" class="message-list">
      <div
        v-for="(m, idx) in chat.messages"
        :key="m.id ?? idx"
        class="msg"
        :class="m.type === 'USER' ? 'user' : m.type === 'ASSISTANT' ? 'ai' : 'system'"
      >
        <div class="bubble">
          <div v-if="m._candidate" class="candidate-tag">候选正文</div>
          <div class="bubble-text">{{ m.neirong }}</div>
          <el-button
            v-if="m._candidate"
            class="use-candidate"
            type="primary"
            size="small"
            @click="useCandidate(m.neirong)"
          >使用此版本</el-button>
        </div>
      </div>
      <el-empty
        v-if="!chat.messages.length"
        :description="chat.huihuaList.length ? '暂无消息，写下想法或让 AI 帮你补充、生成正文' : '还没有会话，点击「新会话」开始与 AI 交流'"
        :image-size="80"
      />
    </div>

    <div class="chat-input">
      <el-input
        v-model="input"
        type="textarea"
        :rows="2"
        maxlength="2000"
        placeholder="输入想对 AI 说的话……（Ctrl+Enter 发送）"
        @keydown="onKeydown"
      />
      <div class="chat-buttons">
        <el-button type="primary" size="small" :loading="chat.sending" @click="handleSend(0)">发送</el-button>
        <el-button size="small" :loading="chat.sending" @click="handleSend(1)">补充提问</el-button>
        <el-button size="small" :loading="chat.sending" @click="handleSend(2)">生成正文</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { nextTick, ref, watch } from 'vue'
import { ElMessage } from 'element-plus/es/components/message/index.mjs'
import { ElMessageBox } from 'element-plus/es/components/message-box/index.mjs'
import api from '../api'
import { useChatStore } from '../stores/chat'
import { useJiluStore } from '../stores/jilu'

const chat = useChatStore()
const jilu = useJiluStore()
const input = ref('')
const msgBox = ref(null)

watch(
  () => chat.messages.length,
  async () => {
    await nextTick()
    if (msgBox.value) msgBox.value.scrollTop = msgBox.value.scrollHeight
  },
)

async function onSwitch(id) {
  chat.currentHuihuaId = id
  try {
    await chat.loadMessages()
  } catch {
    /* 拦截器已提示 */
  }
}

async function handleNew() {
  if (!jilu.current) return
  try {
    await chat.createHuihua(jilu.current.id)
    ElMessage.success('已创建新会话')
  } catch {
    /* 拦截器已提示 */
  }
}

async function handleDel() {
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

async function handleTest() {
  try {
    await api.get('/duihua/test')
    ElMessage.success('AI连接正常')
  } catch {
    /* 拦截器已提示 */
  }
}

async function handleSend(command) {
  if (chat.sending) return
  if (!chat.currentHuihuaId) {
    ElMessage.warning('请先创建一个会话')
    return
  }
  const zhengwen = jilu.editForm.zhengwen
  if (!zhengwen.trim()) {
    ElMessage.warning('正文为空时无法与 AI 交流，请先在编辑器中写点内容')
    return
  }
  if (zhengwen.length > 2000) {
    ElMessage.warning('正文不能超过2000字')
    return
  }
  let neirong = null
  if (command === 0) {
    neirong = input.value.trim()
    if (!neirong) {
      ElMessage.warning('请输入消息内容')
      return
    }
  }
  try {
    await chat.send({ command, neirong, zhengwen })
    if (command === 0) input.value = ''
  } catch {
    /* 拦截器已提示 */
  }
}

function useCandidate(text) {
  jilu.useCandidate(text)
  ElMessage.success('已放入编辑器，确认无误后点击「保存」')
}

function onKeydown(e) {
  if (e.key === 'Enter' && (e.ctrlKey || e.metaKey)) {
    e.preventDefault()
    handleSend(0)
  }
}
</script>
