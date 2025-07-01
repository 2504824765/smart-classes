<script setup lang="ts">
import { reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { useForm } from '@/hooks/web/useForm'
import { addTeacherApi } from '@/api/teacher/index'
import type { FormSchema } from '@/components/Form'
import type { TeacherCreateDTO } from '@/api/teacher/types'

const teacherFormSchema: FormSchema[] = [
  {
    field: 'name',
    label: '教师姓名',
    component: 'Input',
    formItemProps: {
      required: true
    }
  },
  {
    field: 'email',
    label: '邮箱',
    component: 'Input',
    formItemProps: {
      required: true
    }
  },
  {
    field: 'phone',
    label: '手机号',
    component: 'Input'
  },
  {
    field: 'title',
    label: '职称',
    component: 'Input'
  },
  {
    field: 'department',
    label: '所属院系',
    component: 'Input'
  }
]

const { formRegister, formMethods } = useForm()
const { getElFormExpose, getFormData } = formMethods

const handleSubmit = async () => {
  const elForm = await getElFormExpose()
  if (!elForm) return

  await elForm.validate(async (valid) => {
    if (!valid) {
      ElMessage.warning('请完整填写教师信息')
      return
    }

    const formData = await getFormData<TeacherCreateDTO>()
    try {
      await addTeacherApi(formData)
      ElMessage.success('教师添加成功')
      elForm.resetFields()
    } catch (err) {
      console.error('教师添加失败', err)
      ElMessage.error('添加失败，请稍后重试')
    }
  })
}
</script>

<template>
  <ContentWrap title="新增教师">
    <Form :schema="teacherFormSchema" @register="formRegister" />
    <BaseButton type="primary" style="margin-top: 16px" @click="handleSubmit">提交</BaseButton>
  </ContentWrap>
</template>
