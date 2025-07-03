<script setup lang="ts">
import { Form, FormSchema } from '@/components/Form'
import { ContentWrap } from '@/components/ContentWrap'
import { BaseButton } from '@/components/Button'
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useForm } from '@/hooks/web/useForm'
import { createTeacherApi, updateTeacherApi, getTeacherByIdApi } from '@/api/teacher/index'
import { getAllDeptApi } from '@/api/department/index'
import type { TeacherCreateDTO, TeacherUpdateDTO } from '@/api/teacher/types'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()
const isEdit = ref(false)
const teacherId = ref<number | null>(null)
const deptOptions = ref<{ label: string; value: number }[]>([])

const teacherFormSchema = reactive<FormSchema[]>([
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
    label: '教师姓名',
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
    field: 'departmentId',
    label: '所属院系',
    component: 'Select',
    componentProps: {
      options: deptOptions
    },
    formItemProps: {
      required: true
    }
  }
])

const { formRegister, formMethods } = useForm()
const { getElFormExpose, getFormData, setValues } = formMethods

const loadDepartments = async () => {
  const res = await getAllDeptApi()
  deptOptions.value = res.data.map((d: any) => ({ label: d.name, value: d.id }))
}

const loadTeacher = async (id: number) => {
  const res = await getTeacherByIdApi(id)
  const teacher = res.data
  setValues({
    username: teacher.username,
    name: teacher.name,
    gender: teacher.gender,
    departmentId: teacher.dept?.id
  })
}

onMounted(async () => {
  await loadDepartments()
  const id = route.query.id
  if (id) {
    isEdit.value = true
    teacherId.value = Number(id)
    await loadTeacher(teacherId.value)
  } else {
    isEdit.value = false
    teacherId.value = null
    // 新增模式，什么都不做
  }
})

const handleSubmit = async () => {
  const elForm = await getElFormExpose()
  if (!elForm) return

  await elForm.validate(async (valid) => {
    if (!valid) {
      ElMessage.warning('请完整填写教师信息')
      return
    }
    let formData = await getFormData<TeacherCreateDTO & { departmentId: number }>()
    // 新增时去除 id 字段，避免后端addTeacher因id为null报错
    if (!isEdit.value) {
      const { id, ...rest } = formData as any
      formData = rest
    }
    try {
      if (isEdit.value) {
        if (!teacherId.value) {
          ElMessage.error('教师ID缺失，无法编辑')
          return
        }
        await updateTeacherApi({ ...formData, id: teacherId.value })
        ElMessage.success('教师信息更新成功')
      } else {
        await createTeacherApi(formData)
        ElMessage.success('教师添加成功')
      }
      router.push({ path: '/admin/teacherManage' })
    } catch (err) {
      ElMessage.error('操作失败，请稍后重试')
    }
  })
}
</script>

<template>
  <ContentWrap :title="isEdit ? '编辑教师' : '新增教师'">
    <Form :schema="teacherFormSchema" @register="formRegister" />
    <BaseButton type="primary" style="margin-top: 16px" @click="handleSubmit">提交</BaseButton>
    <BaseButton
      style="margin-top: 16px; margin-left: 8px"
      @click="() => router.push({ path: '/admin/teacherManage' })"
      >返回</BaseButton
    >
  </ContentWrap>
</template>
