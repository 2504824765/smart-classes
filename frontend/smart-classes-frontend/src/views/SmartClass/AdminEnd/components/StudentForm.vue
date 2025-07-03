<script setup lang="ts">
import { Form, FormSchema } from '@/components/Form'
import { ContentWrap } from '@/components/ContentWrap'
import { BaseButton } from '@/components/Button'
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useForm } from '@/hooks/web/useForm'
import { createStudentApi, updateStudentApi, getStudentByIdApi } from '@/api/student/index'
import { getAllDeptApi } from '@/api/department/index'
import type { Student, StudentCreateDTO, StudentUpdateDTO } from '@/api/student/types'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()
const isEdit = ref(false)
const studentId = ref<number | null>(null)
const deptOptions = ref<{ label: string; value: number }[]>([])

const studentFormSchema = reactive<FormSchema[]>([
  {
    field: 'username',
    label: '用户名',
    component: 'Input',
    formItemProps: {
      required: true
    }
  },
  {
    field: 'name',
    label: '学生姓名',
    component: 'Input',
    formItemProps: {
      required: true
    }
  },
  {
    field: 'gender',
    label: '性别',
    component: 'Select',
    componentProps: {
      options: [
        { label: '男', value: '男' },
        { label: '女', value: '女' }
      ]
    },
    formItemProps: {
      required: true
    }
  },
  {
    field: 'deptId',
    label: '所属院系',
    component: 'Select',
    componentProps: {
      options: deptOptions
    },
    formItemProps: {
      required: true
    }
  },
  {
    field: 'gpa',
    label: '绩点 (GPA)',
    component: 'InputNumber',
    componentProps: {
      min: 0,
      max: 4,
      step: 0.01
    },
    formItemProps: {
      required: true,
      rules: [
        {
          validator(rule, value, callback) {
            if (value < 0 || value > 4) {
              callback(new Error('GPA 必须在 0 到 4 之间'))
            } else {
              callback()
            }
          },
          trigger: 'blur'
        }
      ]
    }
  }
])

const { formRegister, formMethods } = useForm()
const { getElFormExpose, getFormData, setValues } = formMethods

const loadDepartments = async () => {
  const res = await getAllDeptApi()
  deptOptions.value = res.data.map((d: any) => ({ label: d.name, value: d.id }))
}

const loadStudent = async (id: number) => {
  const res = await getStudentByIdApi(id)
  const student = res.data
  setValues({
    username: student.username,
    name: student.name,
    gender: student.gender,
    deptId: student.dept?.id || student.department?.id,
    gpa: student.gpa
  })
}

onMounted(async () => {
  await loadDepartments()
  const id = route.query.id
  if (id) {
    isEdit.value = true
    studentId.value = Number(id)
    await loadStudent(studentId.value)
  }
})

const handleSubmit = async () => {
  const elForm = await getElFormExpose()
  if (!elForm) return

  await elForm.validate(async (valid) => {
    if (!valid) {
      ElMessage.warning('请完整填写学生信息')
      return
    }
    const formData = await getFormData<StudentCreateDTO & { deptId: number }>()
    try {
      if (isEdit.value && studentId.value) {
        await updateStudentApi({ ...formData, id: studentId.value })
        ElMessage.success('学生信息更新成功')
      } else {
        await createStudentApi(formData)
        ElMessage.success('学生添加成功')
      }
      router.push({ path: '/admin/studentManage' })
    } catch (err) {
      ElMessage.error('操作失败，请稍后重试')
    }
  })
}
</script>

<template>
  <ContentWrap :title="isEdit ? '编辑学生' : '新增学生'">
    <Form :schema="studentFormSchema" @register="formRegister" />
    <BaseButton type="primary" style="margin-top: 16px" @click="handleSubmit">提交</BaseButton>
    <BaseButton
      style="margin-top: 16px; margin-left: 8px"
      @click="() => router.push({ path: '/admin/studentManage' })"
      >返回</BaseButton
    >
  </ContentWrap>
</template>
