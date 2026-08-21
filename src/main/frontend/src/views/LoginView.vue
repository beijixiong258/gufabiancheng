<template>
  <div class="login-page">
    <el-card class="login-card" shadow="always">
      <h1 class="login-title">北极熊记录助手</h1>
      <p class="login-subtitle">把零散的想法，整理成完整的文字</p>

      <el-tabs v-model="tab" stretch>
        <!-- 登录 -->
        <el-tab-pane label="登录" name="login">
          <el-form label-position="top" @submit.prevent>
            <el-form-item label="账号">
              <el-input v-model="loginForm.zhanghao" maxlength="32" placeholder="4-32位账号" />
            </el-form-item>
            <el-form-item label="密码">
              <el-input
                v-model="loginForm.mima"
                type="password"
                show-password
                maxlength="32"
                placeholder="6-32位密码"
                @keyup.enter="handleLogin"
              />
            </el-form-item>
            <el-button type="primary" class="full" :loading="loading" @click="handleLogin">
              登录
            </el-button>
          </el-form>
        </el-tab-pane>

        <!-- 注册 -->
        <el-tab-pane label="注册" name="register">
          <el-form ref="regFormRef" :model="regForm" :rules="regRules" label-position="top" @submit.prevent>
            <el-form-item label="账号" prop="zhanghao">
              <el-input v-model="regForm.zhanghao" maxlength="32" placeholder="4-32位账号（必填）" />
            </el-form-item>
            <el-form-item label="密码" prop="mima">
              <el-input
                v-model="regForm.mima"
                type="password"
                show-password
                maxlength="32"
                placeholder="6-32位密码（必填）"
              />
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmMima">
              <el-input
                v-model="regForm.confirmMima"
                type="password"
                show-password
                maxlength="32"
                placeholder="再次输入密码"
                @keyup.enter="handleRegister"
              />
            </el-form-item>
            <p class="register-note">手机号和邮箱可在登录后进入个人资料补充。</p>
            <el-button type="primary" class="full" :loading="loading" @click="handleRegister">
              注册
            </el-button>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus/es/components/message/index.mjs'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const auth = useAuthStore()

const tab = ref('login')
const loading = ref(false)

const loginForm = reactive({ zhanghao: '', mima: '' })
const regFormRef = ref(null)
const regForm = reactive({ zhanghao: '', mima: '', confirmMima: '' })
const regRules = {
  zhanghao: [
    { required: true, message: '请输入账号', trigger: 'blur' },
    { min: 4, max: 32, message: '账号应为4-32位', trigger: 'blur' },
  ],
  mima: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 32, message: '密码应为6-32位', trigger: 'blur' },
  ],
  confirmMima: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    {
      validator: (_rule, value, callback) => {
        if (value !== regForm.mima) callback(new Error('两次输入的密码不一致'))
        else callback()
      },
      trigger: ['blur', 'change'],
    },
  ],
}

async function handleLogin() {
  if (!loginForm.zhanghao || !loginForm.mima) {
    ElMessage.warning('请输入账号和密码')
    return
  }
  loading.value = true
  try {
    await auth.login(loginForm)
    await auth.fetchMe()
    ElMessage.success('登录成功')
    router.push('/')
  } catch {
    /* 拦截器已提示 */
  } finally {
    loading.value = false
  }
}

async function handleRegister() {
  if (loading.value) return
  try {
    await regFormRef.value?.validate()
  } catch {
    return
  }
  loading.value = true
  try {
    await auth.register({
      zhanghao: regForm.zhanghao.trim(),
      mima: regForm.mima,
    })
    ElMessage.success('注册成功，请登录')
    loginForm.zhanghao = regForm.zhanghao.trim()
    loginForm.mima = ''
    Object.assign(regForm, { zhanghao: '', mima: '', confirmMima: '' })
    regFormRef.value?.clearValidate()
    tab.value = 'login'
  } catch {
    /* 拦截器已提示 */
  } finally {
    loading.value = false
  }
}
</script>
