<script setup lang="ts">
import { ref, watch, defineProps, defineEmits, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getAllDeptApi } from '@/api/department'
import type { Department } from '@/api/department/types'

const props = defineProps<{ visible: boolean; data: any }>()
const emit = defineEmits(['update:visible', 'save'])

const visible = ref(props.visible)
const departments = ref<Department[]>([])

watch(
  () => props.visible,
  (v) => (visible.value = v)
)
watch(visible, (v) => emit('update:visible', v))

const form = reactive({
  username: '',
  name: '',
  gender: '',
  deptId: undefined as number | undefined,
  gpa: ''
})

watch(
  () => props.data,
  (val) => {
    Object.assign(form, val)
  }
)

// 获取部门列表
const getDepartments = async () => {
  try {
    const res = await getAllDeptApi()
    if (res && res.data) {
      departments.value = Array.isArray(res.data) ? res.data : []
    }
  } catch (error) {
    console.error('获取部门列表失败:', error)
  }
}

onMounted(() => {
  getDepartments()
})

const resetForm = () => {
  // 重置表单为空对象
  Object.assign(form, {
    username: '',
    name: '',
    gender: '',
    deptId: undefined,
    gpa: ''
  })
}

const handleClose = () => {
  resetForm()
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
    username: form.username.trim(),
    name: form.name.trim(),
    gender: form.gender,
    deptId: Number(form.deptId),
    gpa: Number(form.gpa)
  }

  console.log('添加学生数据:', studentData)
  emit('save', studentData)
  resetForm()
  emit('update:visible', false)
}
</script>

<template>
  <el-dialog v-model="visible" title="添加学生" width="500px" @close="handleClose">
    <!-- 这里放表单内容，比如： -->
    <el-form :model="form" label-width="80px">
      <el-form-item label="用户名" required>
        <el-input v-model="form.username" placeholder="请输入用户名" />
      </el-form-item>
      <el-form-item label="姓名" required>
        <el-input v-model="form.name" placeholder="请输入姓名" />
      </el-form-item>
      <el-form-item label="性别" required>
        <el-radio-group v-model="form.gender">
          <el-radio value="Male" size="large">男</el-radio>
          <el-radio value="Female" size="large">女</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="所在部门" required>
        <el-select v-model="form.deptId" placeholder="请选择部门" style="width: 100%">
          <el-option
            v-for="dept in departments"
            :key="dept.id"
            :label="dept.name"
            :value="dept.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="绩点" required>
        <el-input v-model="form.gpa" type="number" step="0.01" placeholder="请输入绩点" />
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
