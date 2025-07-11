<script setup lang="ts">
import { ref, watch, defineProps, defineEmits } from 'vue'
import { reactive } from 'vue'

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
        <div style="display: flex; gap: 8px">
          <el-button type="primary" @click="handleSave">保存</el-button>
          <el-button @click="handleClose">取消</el-button>
        </div>
      </div>
    </template>
  </el-dialog>
</template>

