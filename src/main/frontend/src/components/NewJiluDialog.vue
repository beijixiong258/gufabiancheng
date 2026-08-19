<template>
  <el-dialog v-model="visible" title="新建记录" width="440px">
    <el-form label-position="top">
      <el-form-item label="题目">
        <el-input v-model="form.timu" maxlength="50" placeholder="不超过50字（必填）" />
      </el-form-item>
      <el-form-item label="题材">
        <el-select v-model="form.ticai">
          <el-option label="其他" value="QITA" />
          <el-option label="日记" value="RIJI" />
          <el-option label="文学" value="WENXUE" />
          <el-option label="学术" value="XUESHU" />
          <el-option label="会议" value="HUIYI" />
        </el-select>
      </el-form-item>
      <el-form-item label="标签">
        <el-input v-model="form.biaoqian" maxlength="20" placeholder="选填，不超过20字" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" :loading="loading" @click="handleCreate">创建</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus/es/components/message/index.mjs'
import { useJiluStore } from '../stores/jilu'
import { useChatStore } from '../stores/chat'

const visible = defineModel()
const jilu = useJiluStore()
const chat = useChatStore()
const loading = ref(false)
const form = reactive({ timu: '', ticai: 'QITA', biaoqian: '' })

async function handleCreate() {
  if (!form.timu.trim()) {
    ElMessage.warning('请输入题目')
    return
  }
  loading.value = true
  try {
    const created = await jilu.create({
      timu: form.timu.trim(),
      ticai: form.ticai,
      biaoqian: form.biaoqian.trim() || undefined,
    })
    visible.value = false
    form.timu = ''
    form.ticai = 'QITA'
    form.biaoqian = ''
    await jilu.select(created.id)
    await chat.loadHuihua(created.id)
    ElMessage.success('已创建记录')
  } catch {
    /* 拦截器已提示 */
  } finally {
    loading.value = false
  }
}
</script>
