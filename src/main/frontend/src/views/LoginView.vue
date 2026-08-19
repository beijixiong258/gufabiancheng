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
          <el-form label-position="top" @submit.prevent>
            <el-form-item label="账号">
              <el-input v-model="regForm.zhanghao" maxlength="32" placeholder="4-32位账号（必填）" />
            </el-form-item>
            <el-form-item label="密码">
              <el-input
                v-model="regForm.mima"
                type="password"
                show-password
                maxlength="32"
                placeholder="6-32位密码（必填）"
              />
            </el-form-item>
            <el-form-item label="手机号">
              <el-input v-model="regForm.dianhua" maxlength="11" placeholder="选填" />
            </el-form-item>
            <el-form-item label="身份证号">
              <el-input v-model="regForm.shenfenzheng" maxlength="18" placeholder="选填" />
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="regForm.youxiang" maxlength="255" placeholder="选填" />
            </el-form-item>
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
import { ElMessage } from 'element-plus'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const auth = useAuthStore()

const tab = ref('login')
const loading = ref(false)

const loginForm = reactive({ zhanghao: '', mima: '' })
const regForm = reactive({ zhanghao: '', mima: '', dianhua: '', shenfenzheng: '', youxiang: '' })

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
  if (!regForm.zhanghao || !regForm.mima) {
    ElMessage.warning('请输入账号和密码')
    return
  }
  loading.value = true
  try {
    // 可选字段为空时不提交，避免后端 @Pattern 误伤
    await auth.register({
      zhanghao: regForm.zhanghao,
      mima: regForm.mima,
      dianhua: regForm.dianhua || undefined,
      shenfenzheng: regForm.shenfenzheng || undefined,
      youxiang: regForm.youxiang || undefined,
    })
    ElMessage.success('注册成功，请登录')
    loginForm.zhanghao = regForm.zhanghao
    loginForm.mima = ''
    tab.value = 'login'
  } catch {
    /* 拦截器已提示 */
  } finally {
    loading.value = false
  }
}
</script>
