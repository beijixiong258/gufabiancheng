import { createRouter, createWebHistory } from 'vue-router'
const LoginView = () => import('../views/LoginView.vue')
const HomeView = () => import('../views/HomeView.vue')
import { TOKEN_KEY } from '../constants/auth'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/login', name: 'login', component: LoginView },
    { path: '/', name: 'home', component: HomeView },
  ],
})

// 简单登录守卫：无 Token 去登录页，有 Token 不再回登录页
router.beforeEach((to) => {
  const token = localStorage.getItem(TOKEN_KEY)
  if (to.name !== 'login' && !token) return { name: 'login' }
  if (to.name === 'login' && token) return { name: 'home' }
  return true
})

export default router
