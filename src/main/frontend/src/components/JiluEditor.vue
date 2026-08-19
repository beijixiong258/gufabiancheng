<template>
  <div class="panel-card card-col">
    <div class="editor-head">
      <el-input
        v-model="jilu.editForm.timu"
        class="editor-title-input"
        maxlength="50"
        placeholder="题目（不超过50字）"
      />
      <el-tag :type="jilu.current?.jiluZhuangtai === 'FINISH' ? 'success' : 'warning'">
        {{ jilu.current?.jiluZhuangtai === 'FINISH' ? '已完成' : '草稿' }}
      </el-tag>
    </div>

    <el-form label-position="top">
      <el-row :gutter="12">
        <el-col :span="9">
          <el-form-item label="题材">
            <el-select v-model="jilu.editForm.ticai">
              <el-option label="其他" value="QITA" />
              <el-option label="日记" value="RIJI" />
              <el-option label="文学" value="WENXUE" />
              <el-option label="学术" value="XUESHU" />
              <el-option label="会议" value="HUIYI" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="15">
          <el-form-item label="标签">
            <el-input v-model="jilu.editForm.biaoqian" maxlength="20" placeholder="可选，不超过20字" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="正文">
        <el-input
          v-model="jilu.editForm.zhengwen"
          type="textarea"
          :rows="16"
          maxlength="2000"
          placeholder="在这里写下正文草稿……（最多2000字）&#10;写作过程中可以随时让右侧的 AI 帮你补充、总结、扩写或润色。"
        />
      </el-form-item>
    </el-form>

    <div class="editor-foot">
      <span class="zhengwen-count">{{ jilu.editForm.zhengwen.length }} / 2000</span>
      <div class="editor-actions">
        <span v-if="dirty" class="dirty-tip">有未保存修改</span>
        <el-button type="primary" size="small" :loading="saving" @click="handleSave">保存</el-button>
        <el-button type="success" size="small" :loading="finishing" @click="handleFinish">标记完成</el-button>
        <el-button type="danger" size="small" @click="handleDelete">删除</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus/es/components/message/index.mjs'
import { ElMessageBox } from 'element-plus/es/components/message-box/index.mjs'
import { useJiluStore } from '../stores/jilu'
import { useChatStore } from '../stores/chat'

const jilu = useJiluStore()
const chat = useChatStore()
const saving = ref(false)
const finishing = ref(false)
const dirty = computed(() => JSON.stringify(jilu.editForm) !== JSON.stringify(jilu.savedForm))

function beforeUnload(event) {
  if (!dirty.value) return
  event.preventDefault()
  event.returnValue = ''
}

onMounted(() => window.addEventListener('beforeunload', beforeUnload))
onBeforeUnmount(() => window.removeEventListener('beforeunload', beforeUnload))

async function handleSave() {
  if (!jilu.editForm.timu.trim()) {
    ElMessage.warning('题目不能为空')
    return
  }
  saving.value = true
  try {
    await jilu.save()
    ElMessage.success('已保存')
  } catch {
    /* 拦截器已提示 */
  } finally {
    saving.value = false
  }
}

async function handleFinish() {
  if (!jilu.editForm.zhengwen.trim()) {
    ElMessage.warning('正文为空，无法标记完成')
    return
  }
  finishing.value = true
  try {
    await jilu.finish()
    ElMessage.success('已保存并标记完成')
  } catch {
    /* 拦截器已提示 */
  } finally {
    finishing.value = false
  }
}

async function handleDelete() {
  try {
    await ElMessageBox.confirm('确定删除这条记录？其会话和消息会一并删除。', '删除记录', {
      type: 'warning',
      confirmButtonText: '删除',
      cancelButtonText: '取消',
    })
  } catch {
    return
  }
  try {
    await jilu.remove()
    chat.reset()
    ElMessage.success('已删除')
  } catch {
    /* 拦截器已提示 */
  }
}
</script>

<style scoped>
.dirty-tip {
  color: #b54708;
  font-size: 12px;
}
</style>
