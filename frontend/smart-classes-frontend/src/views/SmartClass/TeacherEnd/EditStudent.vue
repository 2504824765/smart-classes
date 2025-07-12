<script setup lang="ts">

const props = defineProps<{ visible: boolean; data: any }>()
const emit = defineEmits(['update:visible', 'save', 'deleted'])

const visible = ref(props.visible)
const departments = ref<Department[]>([])

watch(
  () => props.visible,
  (v) => (visible.value = v)
)
watch(visible, (v) => emit('update:visible', v))



watch(
  () => props.data,
  (val) => {
    if (val && val.id) {
      Object.assign(form, {
        id: val.id,
        username: val.username || '',
        name: val.name || '',
        gender: val.gender || '',
        deptId: val.department?.id || undefined,
        gpa: val.gpa || ''
      })
    }
  },
  { immediate: true }
)


const handleClose = () => {
  emit('update:visible', false)
}

const handleSave = () => {
  // 验证必填字段
  if (!form.username || !form.name || !form.gender || !form.deptId || !form.gpa) {
    ElMessage.warning('请填写所有必填字段')
    return
  }

  // 转换数据格式以符合后端期望
  const studentData = {
    id: Number(form.id),
    username: form.username.trim(),
    name: form.name.trim(),
    gender: form.gender,
    deptId: Number(form.deptId),
    gpa: Number(form.gpa)
  }

  console.log('编辑学生数据:', studentData)
  emit('save', studentData)
  emit('update:visible', false)
}
</script>

<template>
  <el-dialog v-model="visible" title="编辑学生" width="500px" @close="handleClose">

          <el-radio value="Male" size="large">男</el-radio>
          <el-radio value="Female" size="large">女</el-radio>
        </el-radio-group>
      </el-form-item>

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

