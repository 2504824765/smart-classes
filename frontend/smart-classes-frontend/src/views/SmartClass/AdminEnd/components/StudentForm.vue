<script setup lang="ts">
import { Form, FormSchema } from '@/components/Form'
import { ContentWrap } from '@/components/ContentWrap'
import { BaseButton } from '@/components/Button'
import { reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { useForm } from '@/hooks/web/useForm'
import { createStudentApi } from '@/api/student/index'
import type { Student, StudentCreateDTO } from '@/api/student/types'

const studentFormSchema = reactive<FormSchema[]>([
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
    field: 'dept',
    label: '所属院系',
    component: 'Input',
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
const { getElFormExpose, getFormData } = formMethods

const handleSubmit = async () => {
  const elForm = await getElFormExpose()
  if (!elForm) return

  await elForm.validate(async (valid) => {
    if (!valid) {
      ElMessage.warning('请完整填写学生信息')
      return
    }

    const formData = await getFormData<StudentCreateDTO>()
    try {
      await createStudentApi(formData)
      ElMessage.success('学生添加成功')
      elForm.resetFields()
    } catch (err) {
      console.error('学生添加失败', err)
      ElMessage.error('添加失败，请稍后重试')
    }
  })
}
</script>

<template>
  <ContentWrap title="新增学生">
    <Form :schema="studentFormSchema" @register="formRegister" />
    <BaseButton type="primary" style="margin-top: 16px" @click="handleSubmit">提交</BaseButton>
  </ContentWrap>
</template>
