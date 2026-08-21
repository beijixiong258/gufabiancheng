import { ElMessageBox } from 'element-plus/es/components/message-box/index.mjs'
import { useJiluStore } from '../stores/jilu'

export function useRecordLeaveGuard() {
  const jilu = useJiluStore()
  async function confirmLeave({
    message = '当前记录有未保存修改，继续操作会放弃这些修改。',
    confirmButtonText = '放弃修改',
  } = {}) {
    if (!jilu.hasUnsavedChanges) return true
    try {
      await ElMessageBox.confirm(message, '未保存修改', {
        type: 'warning',
        confirmButtonText,
        cancelButtonText: '继续编辑',
        distinguishCancelAndClose: true,
      })
      return true
    } catch {
      return false
    }
  }
  return { confirmLeave }
}
