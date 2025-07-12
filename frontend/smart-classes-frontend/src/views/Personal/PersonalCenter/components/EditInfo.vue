<script lang="ts" setup>
import { FormSchema, Form } from '@/components/Form'
import { useForm } from '@/hooks/web/useForm'
import { useValidator } from '@/hooks/web/useValidator'
import { reactive, ref, watch, onMounted } from 'vue'
import { ElDivider, ElMessage, ElMessageBox } from 'element-plus'
import { getStudentByUsernameApi, createStudentApi, updateStudentApi } from '@/api/student'
import { getAllDeptApi } from '@/api/department'
import { useUserStore } from '@/store/modules/user'
import { addStudentDataApi } from '@/api/studentData'
import { getTeacherByUsernameApi, updateTeacherApi, createTeacherApi } from '@/api/teacher'

const props = defineProps({
  userInfo: {
    type: Object,
    default: () => ({})
  },
  role: {
    type: [String, Object],
    default: 'student'
  }
})

const { required } = useValidator()
const userStore = useUserStore()
const username = ref('')
const studentId = ref<number | null>(null)
const teacherId = ref<number | null>(null)
const isAdd = ref(true)
const deptOptions = ref<any[]>([])
const deptMap = ref<Record<number, { id: number, parentId: number }>>({})
const deptPath = ref<number[]>([])
const studentDataId = ref<number | null>(null)

// 学生表单
const studentFormSchema = reactive<FormSchema[]>([
  {
    field: 'name',
    label: '姓名',
    component: 'Input',
    colProps: { span: 24 }
  },
  {
    field: 'gender',
    label: '性别',
    component: 'Select',
    colProps: { span: 24 },
    componentProps: {
      options: [
        { label: '男', value: '男' },
        { label: '女', value: '女' }
      ]
    }
  },
  {
    field: 'deptPath',
    label: '部门',
    component: 'Cascader',
    colProps: { span: 24 },
    componentProps: {
      options: deptOptions,
      props: {
        value: 'id',
        label: 'name',
        children: 'children',
        checkStrictly: true
      }
    }
  }
])

// 教师表单
const teacherFormSchema = reactive<FormSchema[]>([
  {
    field: 'name',
    label: '姓名',
    component: 'Input',
    colProps: { span: 24 }
  },
  {
    field: 'gender',
    label: '性别',
    component: 'Select',
    colProps: { span: 24 },
    componentProps: {
      options: [
        { label: '男', value: '男' },
        { label: '女', value: '女' }
      ]
    }
  },
  {
    field: 'deptPath',
    label: '部门',
    component: 'Cascader',
    colProps: { span: 24 },
    componentProps: {
      options: deptOptions,
      props: {
        value: 'id',
        label: 'name',
        children: 'children',
        checkStrictly: true
      }
    }
  }
])

const rules = reactive({
  name: [required()],
  gender: [required()],
  deptPath: [required()]
})

const { formRegister, formMethods } = useForm()
const { setValues, getElFormExpose, getFormData } = formMethods

function buildDeptTree(list: any[], parentId = 0) {
  return list
    .filter(item => item.parentId === parentId)
    .map(item => ({
      ...item,
      children: buildDeptTree(list, item.id)
    }))
}

// 构建部门id->parentId映射
function buildDeptMap(list: any[]) {
  list.forEach(item => {
    deptMap.value[item.id] = { id: item.id, parentId: item.parentId }
    if (item.children && item.children.length) buildDeptMap(item.children)
  })
}
// 递归获取部门path
function getDeptPathById(id: number): number[] {
  const path: number[] = []
  let current = id
  while (current && deptMap.value[current]) {
    path.unshift(current)
    current = deptMap.value[current].parentId
  }
  return path
}

// 获取部门树和映射
const fetchDeptOptions = async () => {
  const res = await getAllDeptApi()
  if (res && res.data) {
    deptOptions.value = buildDeptTree(res.data)
    buildDeptMap(deptOptions.value)
  }
}

// 获取学生信息
const fetchStudentInfo = async () => {
  username.value = userStore.getUserInfo?.username || props.userInfo?.username || ''
  if (!username.value) return
  const res = await getStudentByUsernameApi(username.value)
  if (res && res.data) {
    isAdd.value = false
    studentId.value = res.data.id
    studentDataId.value = res.data.studentData?.id || null
    const path = res.data.department?.id ? getDeptPathById(res.data.department.id) : []
    setValues({
      name: res.data.name,
      gender: res.data.gender,
      deptPath: path
    })
  } else {
    isAdd.value = true
    setValues({})
  }
}
// 获取教师信息
const fetchTeacherInfo = async () => {
  username.value = userStore.getUserInfo?.username || props.userInfo?.username || ''
  if (!username.value) return
  const res = await getTeacherByUsernameApi(username.value)
  if (res && res.data) {
    isAdd.value = false
    teacherId.value = res.data.id
    const path = res.data.department?.id ? getDeptPathById(res.data.department.id) : []
    setValues({
      name: res.data.name,
      gender: res.data.gender,
      deptPath: path
    })
  } else {
    isAdd.value = true
    setValues({})
  }
}

onMounted(async () => {
  await fetchDeptOptions()
  if (props.role === 'student') {
    await fetchStudentInfo()
  } else if (props.role === 'teacher') {
    await fetchTeacherInfo()
  }
})

const saveLoading = ref(false)
const save = async () => {
  const elForm = await getElFormExpose()
  const valid = await elForm?.validate().catch((err) => {
    console.log(err)
  })
  if (valid) {
    ElMessageBox.confirm('是否确认保存?', '提示', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    })
      .then(async () => {
        try {
          saveLoading.value = true
          const formData = await getFormData()
          const deptId = Array.isArray(formData.deptPath) ? formData.deptPath.at(-1) : formData.deptPath
          if (props.role === 'student') {
            if (isAdd.value) {
              const studentDataRes = await addStudentDataApi({
                conceptUnderstanding: 0,
                logicalReasoning: 0,
                problemSolving: 0,
                innovativeThinking: 0,
                expressionNorms: 0
              })
              const newStudentDataId = studentDataRes?.data?.id
              if (!newStudentDataId) throw new Error('学生数据创建失败')
              await createStudentApi({
                name: formData.name,
                gender: formData.gender,
                deptId,
                studentDataId: newStudentDataId,
                gpa: 0,
                username: username.value,
                department: { id: 0, name: '', parentId: 0, departmentLevel: 0 }
              })
              ElMessage.success('添加成功')
              isAdd.value = false
              await fetchStudentInfo()
            } else {
              await updateStudentApi({
                id: studentId.value!,
                name: formData.name,
                gender: formData.gender,
                deptId,
                studentDataId: studentDataId.value!
              })
              ElMessage.success('修改成功')
              await fetchStudentInfo()
            }
          } else if (props.role === 'teacher') {
            if (isAdd.value) {
              await createTeacherApi({
                name: formData.name,
                gender: formData.gender,
                departmentId: deptId,
                username: username.value
              })
              ElMessage.success('添加成功')
              isAdd.value = false
              await fetchTeacherInfo()
            } else {
              await updateTeacherApi({
                id: teacherId.value!,
                name: formData.name,
                gender: formData.gender,
                departmentId: deptId
              })
              ElMessage.success('修改成功')
              await fetchTeacherInfo()
            }
          }
        } catch (error) {
          console.log(error)
        } finally {
          saveLoading.value = false
        }
      })
      .catch(() => {})
  }
}
</script>

<template>
  <Form
    :rules="rules"
    @register="formRegister"
    :schema="props.role === 'student' ? studentFormSchema : teacherFormSchema"
  />
  <ElDivider />
  <BaseButton type="primary" :loading="saveLoading" @click="save">保存</BaseButton>
</template>
