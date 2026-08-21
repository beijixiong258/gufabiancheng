<template>
  <div class="home" :class="{ 'has-current-record': Boolean(jilu.current) }">
    <header class="topbar">
      <div class="brand">
        <span class="brand-mark" aria-hidden="true">
          <svg viewBox="0 0 40 40">
            <circle cx="20" cy="20" r="16" fill="currentColor" opacity=".12" />
            <circle cx="13" cy="13" r="5" fill="currentColor" />
            <circle cx="27" cy="13" r="5" fill="currentColor" />
            <path d="M9 21c0-8 4.9-12 11-12s11 4 11 12c0 6.1-4.9 11-11 11S9 27.1 9 21Z" fill="#fff" />
            <circle cx="16" cy="20" r="1.5" fill="currentColor" />
            <circle cx="24" cy="20" r="1.5" fill="currentColor" />
            <path d="M17 24.1c.9-1.7 5.1-1.7 6 0-.6 2-2 3-3 3s-2.4-1-3-3Z" fill="currentColor" />
          </svg>
        </span>
        <span class="brand-copy">
          <strong class="brand-name">北极熊记录助手</strong>
          <span class="brand-subtitle">让灵感清晰成篇</span>
        </span>
      </div>
      <div class="topbar-actions">
        <button
          class="user-chip"
          type="button"
          aria-label="打开个人资料"
          title="打开个人资料"
          @click="ui.profile = true"
        >
          <span class="account-avatar">{{ userInitial }}</span>
          <span class="user-copy">
            <strong>{{ auth.user?.zhanghao || '当前用户' }}</strong>
            <span>{{ auth.isAdmin ? '管理员' : '普通用户' }}</span>
          </span>
        </button>
        <span class="topbar-divider" aria-hidden="true"></span>
        <el-button
          v-if="auth.isAdmin"
          class="nav-button"
          aria-label="用户管理"
          title="用户管理"
          @click="ui.admin = true"
        >
          <svg viewBox="0 0 24 24" aria-hidden="true">
            <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2M9 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8ZM17 11l2 2 3-4" />
          </svg>
          <span class="nav-label">用户管理</span>
        </el-button>
        <el-button
          class="nav-button logout-button"
          aria-label="退出登录"
          title="退出登录"
          :loading="loggingOut"
          :disabled="loggingOut"
          @click="handleLogout"
        >
          <svg viewBox="0 0 24 24" aria-hidden="true">
            <path d="M10 17l5-5-5-5M15 12H3M14 3h5a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2h-5" />
          </svg>
          <span class="nav-label">退出</span>
        </el-button>
      </div>
    </header>

    <main class="layout">
      <JiluList class="sidebar-card card-col" @record-opened="handleRecordOpened" />
      <section class="workspace">
        <div v-if="jilu.loading && !jilu.current" class="welcome-card welcome-loading" aria-label="正在加载记录">
          <span class="welcome-loading-mark" aria-hidden="true"></span>
          <strong>正在整理你的记录</strong>
          <p>请稍候，马上就好</p>
        </div>
        <div v-else-if="!jilu.current" class="welcome-card">
          <div class="welcome-hero">
            <div class="welcome-copy">
              <span class="welcome-kicker"><i></i>专注记录 · 智能整理</span>
              <h1>把每一个想法，<br /><em>写成完整的故事</em></h1>
              <p>
                从随手记录到完整成稿，在这里整理文字，并让 AI 陪你补充思路、润色表达。
              </p>
              <div class="welcome-actions">
                <el-button type="primary" size="large" @click="openNewRecord">
                  <span class="button-plus">+</span>
                  {{ jilu.total ? '新建一条记录' : '创建第一条记录' }}
                </el-button>
                <span v-if="jilu.total" class="welcome-hint">也可以从左侧选择已有记录继续编辑</span>
                <span v-else class="welcome-hint">只需一个题目，随时都可以继续完善</span>
              </div>
            </div>

            <div class="welcome-visual" aria-hidden="true">
              <span class="visual-glow"></span>
              <svg class="welcome-illustration" viewBox="0 0 460 350">
                <defs>
                  <linearGradient id="ice-card" x1="0" y1="0" x2="1" y2="1">
                    <stop offset="0" stop-color="#ffffff" />
                    <stop offset="1" stop-color="#eaf4ff" />
                  </linearGradient>
                  <linearGradient id="ice-blue" x1="0" y1="0" x2="1" y2="1">
                    <stop offset="0" stop-color="#69a9eb" />
                    <stop offset="1" stop-color="#2567aa" />
                  </linearGradient>
                  <filter id="card-shadow" x="-30%" y="-30%" width="160%" height="180%">
                    <feDropShadow dx="0" dy="18" stdDeviation="16" flood-color="#315d87" flood-opacity=".16" />
                  </filter>
                </defs>
                <circle cx="230" cy="174" r="138" fill="#dceeff" opacity=".65" />
                <path d="M42 270 132 139l56 82 47-61 105 110Z" fill="#c8e3fa" opacity=".75" />
                <path d="m132 139 20 29-18-5-17 17Z" fill="#fff" opacity=".9" />
                <ellipse cx="232" cy="307" rx="142" ry="18" fill="#7ea8cc" opacity=".15" />
                <g filter="url(#card-shadow)">
                  <rect x="124" y="61" width="222" height="236" rx="28" fill="url(#ice-card)" />
                  <rect x="124" y="61" width="20" height="236" rx="10" fill="url(#ice-blue)" />
                  <circle cx="145" cy="105" r="5" fill="#fff" opacity=".9" />
                  <circle cx="145" cy="153" r="5" fill="#fff" opacity=".9" />
                  <circle cx="145" cy="201" r="5" fill="#fff" opacity=".9" />
                  <circle cx="145" cy="249" r="5" fill="#fff" opacity=".9" />
                  <path d="M141 105h-15M141 153h-15M141 201h-15M141 249h-15" stroke="#8eb8dd" stroke-width="4" stroke-linecap="round" />
                  <circle cx="235" cy="138" r="48" fill="#e3f1ff" />
                  <circle cx="203" cy="108" r="14" fill="#fff" />
                  <circle cx="267" cy="108" r="14" fill="#fff" />
                  <path d="M195 143c0-30 18-48 40-48s40 18 40 48c0 25-18 42-40 42s-40-17-40-42Z" fill="#fff" />
                  <circle cx="221" cy="140" r="3.5" fill="#24415e" />
                  <circle cx="249" cy="140" r="3.5" fill="#24415e" />
                  <path d="M227 153c3-5 13-5 16 0-1 6-5 9-8 9s-7-3-8-9Z" fill="#24415e" />
                  <path d="M181 211h108M181 230h86M181 249h102" stroke="#bbd4ea" stroke-width="8" stroke-linecap="round" />
                </g>
                <g transform="rotate(10 344 237)">
                  <rect x="328" y="165" width="25" height="124" rx="12" fill="url(#ice-blue)" />
                  <path d="m328 276 12.5 30 12.5-30Z" fill="#f0c690" />
                  <path d="m337 298 3.5 8 3.5-8Z" fill="#24415e" />
                  <rect x="328" y="165" width="25" height="24" rx="10" fill="#87bdf0" />
                </g>
                <path d="M371 104h30M386 89v30M63 106h22M74 95v22" stroke="#76acd9" stroke-width="5" stroke-linecap="round" opacity=".7" />
                <circle cx="88" cy="223" r="7" fill="#fff" />
                <circle cx="389" cy="217" r="9" fill="#fff" />
              </svg>
            </div>
          </div>

          <div class="welcome-guide">
            <div class="guide-intro">
              <span>简单三步</span>
              <strong>从灵感到成稿</strong>
            </div>
            <article class="guide-item">
              <span class="guide-number">01</span>
              <div>
                <h3>创建记录</h3>
                <p>定下题目和题材，先把想法留下来</p>
              </div>
            </article>
            <article class="guide-item">
              <span class="guide-number">02</span>
              <div>
                <h3>专注写作</h3>
                <p>在沉浸式编辑区整理你的正文</p>
              </div>
            </article>
            <article class="guide-item">
              <span class="guide-number">03</span>
              <div>
                <h3>AI 协作</h3>
                <p>补充、扩写或润色，直到满意为止</p>
              </div>
            </article>
          </div>
        </div>
        <div v-else class="workspace-shell">
          <nav class="workspace-switcher" aria-label="记录工作区切换">
            <button class="back-to-list" type="button" @click="backToList">
              <svg viewBox="0 0 20 20" aria-hidden="true"><path d="m12.5 4.5-5 5 5 5M8 9.5h8" /></svg>
              返回列表
            </button>
            <div class="workspace-tabs" role="tablist" aria-label="编辑与 AI 助手">
              <button
                type="button"
                role="tab"
                :aria-selected="workspacePanel === 'editor'"
                :class="{ active: workspacePanel === 'editor' }"
                @click="workspacePanel = 'editor'"
              >编辑内容</button>
              <button
                type="button"
                role="tab"
                :aria-selected="workspacePanel === 'chat'"
                :class="{ active: workspacePanel === 'chat' }"
                @click="workspacePanel = 'chat'"
              >AI 助手</button>
            </div>
          </nav>
          <div class="workspace-grid" :class="`show-${workspacePanel}`">
            <JiluEditor class="editor-workspace-panel" @returned-to-list="handleReturnedToList" />
            <ChatPanel class="chat-workspace-panel" @show-editor="workspacePanel = 'editor'" />
          </div>
        </div>
      </section>
    </main>

    <NewJiluDialog v-model="ui.newJilu" @created="handleRecordOpened" />
    <ProfileDialog v-model="ui.profile" />
    <AdminDialog v-model="ui.admin" />
  </div>
</template>

<script setup>
import { computed, defineAsyncComponent, nextTick, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { useUiStore } from '../stores/ui'
import { useJiluStore } from '../stores/jilu'
import { useChatStore } from '../stores/chat'
import { useRecordLeaveGuard } from '../composables/useRecordLeaveGuard'

const JiluList = defineAsyncComponent(() => import('../components/JiluList.vue'))
const JiluEditor = defineAsyncComponent(() => import('../components/JiluEditor.vue'))
const ChatPanel = defineAsyncComponent(() => import('../components/ChatPanel.vue'))
const NewJiluDialog = defineAsyncComponent(() => import('../components/NewJiluDialog.vue'))
const ProfileDialog = defineAsyncComponent(() => import('../components/ProfileDialog.vue'))
const AdminDialog = defineAsyncComponent(() => import('../components/AdminDialog.vue'))

const router = useRouter()
const auth = useAuthStore()
const ui = useUiStore()
const jilu = useJiluStore()
const chat = useChatStore()
const { confirmLeave } = useRecordLeaveGuard()
const workspacePanel = ref('editor')
const loggingOut = ref(false)
const userInitial = computed(() => (auth.user?.zhanghao?.trim().charAt(0) || '北').toUpperCase())

// 每次进入工作台都从当前登录用户重新加载，避免切换账号时短暂显示上一位用户的数据。
jilu.reset({ loading: true })
chat.reset()
ui.closeAll()

onMounted(async () => {
  try {
    // 启动刷新 Token → 拉取当前用户 → 加载记录列表
    await auth.refresh()
    await auth.fetchMe()
    await jilu.loadList()
  } catch {
    /* 401 时拦截器已跳转登录页 */
  }
})

async function handleLogout() {
  const canLeave = await confirmLeave({
    message: '当前记录有未保存修改，仍要退出登录吗？',
    confirmButtonText: '放弃并退出',
  })
  if (!canLeave || loggingOut.value) return
  loggingOut.value = true
  try {
    await auth.logout()
    jilu.reset()
    chat.reset()
    ui.closeAll()
    await router.push('/login')
  } finally {
    loggingOut.value = false
  }
}

async function openNewRecord() {
  const canLeave = await confirmLeave({
    message: '当前记录有未保存修改，仍要新建记录吗？',
    confirmButtonText: '放弃并新建',
  })
  if (canLeave) ui.newJilu = true
}

async function backToList() {
  const canLeave = await confirmLeave({
    message: '当前记录有未保存修改，仍要返回记录列表吗？',
    confirmButtonText: '放弃并返回',
  })
  if (!canLeave) return
  jilu.clearCurrent()
  chat.reset()
  handleReturnedToList()
}

async function handleRecordOpened() {
  workspacePanel.value = 'editor'
  await scrollPageTop()
}

async function handleReturnedToList() {
  workspacePanel.value = 'editor'
  await scrollPageTop()
}

async function scrollPageTop() {
  await nextTick()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}
</script>
