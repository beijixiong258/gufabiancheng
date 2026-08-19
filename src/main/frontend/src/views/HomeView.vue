<template>
  <div class="home">
    <header class="topbar">
      <div class="brand">
        <span class="brand-mark"></span>
        <span class="brand-name">北极熊记录助手</span>
      </div>
      <div class="topbar-actions">
        <span class="current-user">
          {{ auth.user?.zhanghao }}<template v-if="auth.isAdmin">（管理员）</template>
        </span>
        <el-button v-if="auth.isAdmin" size="small" @click="ui.admin = true">用户管理</el-button>
        <el-button size="small" @click="ui.profile = true">个人资料</el-button>
        <el-button size="small" @click="handleLogout">退出</el-button>
      </div>
    </header>

    <main class="layout">
      <JiluList class="sidebar-card card-col" />
      <section class="workspace">
        <el-empty
          v-if="!jilu.current"
          description="从左侧选择一条记录，或新建一条记录开始写作"
          :image-size="120"
        />
        <div v-else class="workspace-grid">
          <JiluEditor />
          <ChatPanel />
        </div>
      </section>
    </main>

    <NewJiluDialog v-model="ui.newJilu" />
    <ProfileDialog v-model="ui.profile" />
    <AdminDialog v-model="ui.admin" />
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { useUiStore } from '../stores/ui'
import { useJiluStore } from '../stores/jilu'
import { useChatStore } from '../stores/chat'
import JiluList from '../components/JiluList.vue'
import JiluEditor from '../components/JiluEditor.vue'
import ChatPanel from '../components/ChatPanel.vue'
import NewJiluDialog from '../components/NewJiluDialog.vue'
import ProfileDialog from '../components/ProfileDialog.vue'
import AdminDialog from '../components/AdminDialog.vue'

const router = useRouter()
const auth = useAuthStore()
const ui = useUiStore()
const jilu = useJiluStore()
const chat = useChatStore()

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
  await auth.logout()
  jilu.clearCurrent()
  chat.reset()
  router.push('/login')
}
</script>

<style scoped>
.card-col {
  background: var(--card);
  border: 1px solid var(--line);
  border-radius: 10px;
}
</style>
