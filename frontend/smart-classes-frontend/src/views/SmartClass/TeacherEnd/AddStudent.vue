<script setup lang="ts">
import { ref, watch, defineProps, defineEmits } from 'vue'
import { reactive } from 'vue'

const props = defineProps<{ visible: boolean; data: any }>()
const emit = defineEmits(['update:visible', 'save'])

const visible = ref(props.visible)
watch(
  () => props.visible,
  (v) => (visible.value = v)
)
watch(visible, (v) => emit('update:visible', v))

const form = reactive({ ...props.data })

watch(
  () => props.data,
  (val) => {
    Object.assign(form, val)
  }
)

const resetForm = () => {
  // 重置表单为空对象
  Object.assign(form, {
    id: '',
    name: '',
    gender: '',
    department: '',
    gpa: ''
  })
}

const handleClose = () => {
  resetForm()
  emit('update:visible', false)
}
const handleSave = () => {
  emit('save', form)
  resetForm()
  emit('update:visible', false)
}
</script>

<template>
  <el-dialog v-model="visible" title="添加学生" width="500px" @close="handleClose">
    <!-- 这里放表单内容，比如： -->
    <el-form :model="form">
      <el-form-item label="学号">
        <el-input v-model="form.id" />
      </el-form-item>
      <el-form-item label="姓名">
        <el-input v-model="form.name" />
      </el-form-item>
      <el-form-item label="性别">
        <el-radio-group v-model="form.gender">
          <el-radio value="Male" size="large">男</el-radio>
          <el-radio value="Female" size="large">女</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="所在部门">
        <el-input v-model="form.department" />
      </el-form-item>
      <el-form-item label="绩点">
        <el-input v-model="form.gpa" />
      </el-form-item>
    </el-form>
    <template #footer>
      <div style="text-align: center; margin-top: 20px">
        <el-button type="primary" @click="handleSave">保存</el-button>
        <el-button @click="handleClose">取消</el-button>
      </div>
    </template>
  </el-dialog>
</template>
