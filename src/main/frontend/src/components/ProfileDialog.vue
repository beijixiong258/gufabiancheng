<template>
  <el-dialog v-model="visible" title="个人资料" width="440px">
    <el-form label-position="top">
      <el-form-item label="账号">
        <el-input v-model="form.zhanghao" maxlength="32" placeholder="4-32位账号" />
      </el-form-item>
      <el-form-item label="手机号">
        <el-input v-model="form.dianhua" maxlength="11" placeholder="留空则保持不变" />
      </el-form-item>
      <el-form-item label="邮箱">
        <el-input v-model="form.youxiang" maxlength="255" placeholder="留空则保持不变" />
      </el-form-item>
    </el-form>
    <el-button type="primary" class="full" :loading="saving" @click="saveProfile">保存资料</el-button>

    <el-divider />
    <h4 class="password-title">修改密码</h4>
    <el-form label-position="top">
      <el-form-item label="原密码">
        <el-input v-model="pwd.mima1" type="password" show-password />
      </el-form-item>
      <el-form-item label="新密码">
        <el-input v-model="pwd.mima2" type="password" show-password maxlength="32" placeholder="6-32位" />
      </el-form-item>
      <el-form-item label="确认新密码">
        <el-input
          v-model="pwd.mima3"
          type="password"
          show-password
          maxlength="32"
          placeholder="再次输入新密码"
          @keyup.enter="changePassword"
        />
      </el-form-item>
    </el-form>
    <el-button class="full" :loading="changing" @click="changePassword">修改密码</el-button>
  </el-dialog>
</template>

<script setup>
import { reactive, ref, watch } from 'vue'
import { ElMessage } from 'element-plus/es/components/message/index.mjs'
import api from '../api'
import { useAuthStore } from '../stores/auth'

const visible = defineModel()
const auth = useAuthStore()
const saving = ref(false)
const changing = ref(false)
const form = reactive({ zhanghao: '', dianhua: '', youxiang: '' })
const pwd = reactive({ mima1: '', mima2: '', mima3: '' })

watch(visible, async (v) => {
  if (v) {
    try {
      await auth.fetchMe()
    } catch {
      /* 拦截器已提示 */
    }
    form.zhanghao = auth.user?.zhanghao ?? ''
    form.dianhua = auth.user?.dianhua ?? ''
    form.youxiang = auth.user?.youxiang ?? ''
    pwd.mima1 = ''
    pwd.mima2 = ''
    pwd.mima3 = ''
  }
})

async function saveProfile() {
  const len = form.zhanghao.trim().length
  if (len < 4 || len > 32) {
    ElMessage.warning('账号应为4-32位')
    return
  }
  saving.value = true
  try {
    await api.put('/yonghu/update', {
      zhanghao: form.zhanghao.trim(),
      dianhua: form.dianhua.trim() || undefined,
      youxiang: form.youxiang.trim() || undefined,
    })
    await auth.fetchMe()
    ElMessage.success('资料已保存')
  } catch {
    /* 拦截器已提示 */
  } finally {
    saving.value = false
  }
}

async function changePassword() {
  if (!pwd.mima1) {
    ElMessage.warning('请输入原密码')
    return
  }
  if (pwd.mima2.length < 6 || pwd.mima2.length > 32) {
    ElMessage.warning('新密码应为6-32位')
    return
  }
  if (pwd.mima2 !== pwd.mima3) {
    ElMessage.warning('两次输入的新密码不一致')
    return
  }
  changing.value = true
  try {
    await api.put('/yonghu/password', null, { params: { mima1: pwd.mima1, mima2: pwd.mima2 } })
    pwd.mima1 = ''
    pwd.mima2 = ''
    pwd.mima3 = ''
    ElMessage.success('密码已修改')
  } catch {
    /* 拦截器已提示 */
  } finally {
    changing.value = false
  }
}
</script>

<style scoped>
.password-title {
  margin: 0 0 10px;
  color: var(--ink);
  font-size: 15px;
}
</style>
