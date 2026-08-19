<template>
  <el-dialog v-model="visible" title="用户管理" width="780px">
    <div class="admin-toolbar">
      <el-button type="primary" size="small" @click="openCreate">+ 新增用户</el-button>
    </div>

    <el-form
      v-if="formVisible"
      label-position="top"
      style="border: 1px solid var(--line); border-radius: 10px; padding: 14px; margin-bottom: 14px"
    >
      <h4 style="margin: 0 0 10px">{{ editingId ? '编辑用户' : '新增用户' }}</h4>
      <el-row :gutter="12">
        <el-col :span="12">
          <el-form-item label="账号"><el-input v-model="form.zhanghao" maxlength="32" /></el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="密码">
            <el-input v-model="form.mima" type="password" show-password maxlength="32" placeholder="编辑时留空则不修改" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="手机号"><el-input v-model="form.dianhua" maxlength="11" /></el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="身份证号"><el-input v-model="form.shenfenzheng" maxlength="18" /></el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="邮箱"><el-input v-model="form.youxiang" maxlength="255" /></el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="权限">
            <el-select v-model="form.quanxian">
              <el-option label="普通用户" value="USER" />
              <el-option label="管理员" value="ADMIN" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <div style="display: flex; justify-content: flex-end; gap: 8px">
        <el-button size="small" @click="formVisible = false">取消</el-button>
        <el-button type="primary" size="small" :loading="saving" @click="submit">保存</el-button>
      </div>
    </el-form>

    <el-table v-loading="loading" :data="users" size="small" border>
      <el-table-column prop="zhanghao" label="账号" />
      <el-table-column label="手机号">
        <template #default="{ row }">{{ row.dianhua || '-' }}</template>
      </el-table-column>
      <el-table-column label="邮箱">
        <template #default="{ row }">{{ row.youxiang || '-' }}</template>
      </el-table-column>
      <el-table-column label="权限" width="90">
        <template #default="{ row }">
          <el-tag size="small" :type="row.quanxian === 'ADMIN' ? 'danger' : 'primary'">
            {{ row.quanxian === 'ADMIN' ? '管理员' : '普通用户' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="140">
        <template #default="{ row }">
          <el-button size="small" link type="primary" @click="openEdit(row.id)">编辑</el-button>
          <el-button size="small" link type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-empty v-if="!users.length && !loading" description="暂无用户" :image-size="80" />
  </el-dialog>
</template>

<script setup>
import { reactive, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '../api'

const visible = defineModel()
const users = ref([])
const loading = ref(false)
const saving = ref(false)
const formVisible = ref(false)
const editingId = ref(null)
const form = reactive({ zhanghao: '', mima: '', dianhua: '', shenfenzheng: '', youxiang: '', quanxian: 'USER' })

watch(visible, (v) => {
  if (v) {
    formVisible.value = false
    load()
  }
})

async function load() {
  loading.value = true
  try {
    users.value = await api.get('/admin/getlist')
  } catch {
    /* 拦截器已提示 */
  } finally {
    loading.value = false
  }
}

function openCreate() {
  editingId.value = null
  Object.assign(form, { zhanghao: '', mima: '', dianhua: '', shenfenzheng: '', youxiang: '', quanxian: 'USER' })
  formVisible.value = true
}

async function openEdit(id) {
  try {
    const u = await api.get('/admin/get', { params: { yonghuId: id } })
    editingId.value = u.id
    Object.assign(form, {
      zhanghao: u.zhanghao ?? '',
      mima: '',
      dianhua: u.dianhua ?? '',
      shenfenzheng: u.shenfenzheng ?? '',
      youxiang: u.youxiang ?? '',
      quanxian: u.quanxian ?? 'USER',
    })
    formVisible.value = true
  } catch {
    /* 拦截器已提示 */
  }
}

async function submit() {
  if (form.zhanghao.trim().length < 4 || form.zhanghao.trim().length > 32) {
    ElMessage.warning('账号应为4-32位')
    return
  }
  saving.value = true
  try {
    if (editingId.value) {
      // 编辑：空白密码保留原密码；联系方式空字符串表示清空
      const body = {
        id: editingId.value,
        zhanghao: form.zhanghao.trim(),
        dianhua: form.dianhua.trim(),
        shenfenzheng: form.shenfenzheng.trim(),
        youxiang: form.youxiang.trim(),
        quanxian: form.quanxian,
      }
      if (form.mima.trim()) body.mima = form.mima
      await api.put('/admin/modify', body)
      ElMessage.success('用户已更新')
    } else {
      // 新增：密码必填；可选字段为空时不提交，避免格式校验误伤
      if (!form.mima) {
        ElMessage.warning('请输入密码')
        saving.value = false
        return
      }
      await api.post('/admin/add', {
        zhanghao: form.zhanghao.trim(),
        mima: form.mima,
        dianhua: form.dianhua.trim() || undefined,
        shenfenzheng: form.shenfenzheng.trim() || undefined,
        youxiang: form.youxiang.trim() || undefined,
        quanxian: form.quanxian,
      })
      ElMessage.success('用户已创建')
    }
    formVisible.value = false
    await load()
  } catch {
    /* 拦截器已提示 */
  } finally {
    saving.value = false
  }
}

async function handleDelete(id) {
  try {
    await ElMessageBox.confirm('确定删除该用户？其全部记录、会话和消息会一并删除。', '删除用户', {
      type: 'warning',
      confirmButtonText: '删除',
      cancelButtonText: '取消',
    })
  } catch {
    return
  }
  try {
    await api.delete('/admin/delete', { params: { id } })
    await load()
    ElMessage.success('用户已删除')
  } catch {
    /* 拦截器已提示 */
  }
}
</script>
