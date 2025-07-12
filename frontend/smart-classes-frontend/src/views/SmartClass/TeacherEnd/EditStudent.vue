<script setup lang="ts">
import { ref, watch, defineProps, defineEmits } from 'vue'
import { reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { deleteStudentApi } from '@/api/student'

const props = defineProps<{ visible: boolean; data: any }>()
const emit = defineEmits(['update:visible', 'save', 'deleted'])

const visible = ref(props.visible)
watch(
  () => props.visible,
  (v) => (visible.value = v)
)
watch(visible, (v) => emit('update:visible', v))

const form = reactive({ ...props.data })
console.log(form)

watch(
  () => props.data,
  (val) => {
    Object.assign(form, val)
  }
)

const deleteFn = async () => {
  try {
    await ElMessageBox.confirm(`确定要删除学生 "${form.name}" 的数据吗？`, '删除确认', {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'warning',
      confirmButtonClass: 'el-button--danger'
    })
    // 用户确认删除后，执行删除操作
    await deleteStudentApi(form.id)
    ElMessage.success('删除成功')
    emit('update:visible', false)
    // 这里可以触发父组件刷新列表
    emit('deleted')
  } catch (error) {
    if (error === 'cancel') {
      // 用户取消删除
      ElMessage.info('取消删除')
    } else {
      // 删除失败
      ElMessage.error('删除失败')
    }
  }
}

const handleClose = () => {
  emit('update:visible', false)
}

const handleSave = () => {
  emit('save', form)
  emit('update:visible', false)
}
</script>

<template>
  <el-dialog v-model="visible" title="编辑学生" width="500px" @close="handleClose">
    <!-- 这里放表单内容，比如： -->
    <el-form :model="form">
      <el-form-item label="学号">
        <el-input v-model="form.student.id" disabled />
      </el-form-item>
      <el-form-item label="姓名">
        <el-input v-model="form.student.name" disabled />
      </el-form-item>
      <el-form-item label="性别">
        <el-radio-group v-model="form.student.gender" disabled>
          <el-radio value="Male" size="large">男</el-radio>
          <el-radio value="Female" size="large">女</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="所在部门">
        <el-input v-model="form.student.department.name" disabled />
      </el-form-item>
      <el-form-item label="成绩">
        <el-input v-model="form.grade" />
      </el-form-item>
    </el-form>

    <template #footer>
      <div
        style="display: flex; justify-content: space-between; align-items: center; margin-top: 20px"
      >
        <el-button type="danger" @click="deleteFn">删除学生数据</el-button>
        <div style="display: flex; gap: 8px">
          <el-button type="primary" @click="handleSave">保存</el-button>
          <el-button @click="handleClose">取消</el-button>
        </div>
      </div>
    </template>
  </el-dialog>
</template>