<script setup lang="ts">
import { Form, FormSchema } from '@/components/Form'
import { ContentWrap } from '@/components/ContentWrap'
import { useForm } from '@/hooks/web/useForm'
import { BaseButton } from '@/components/Button'
import { ElMessage } from 'element-plus'
import { reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { addClassMissionApi } from '@/api/classMission/index'
import type { ClassMissionCreateDTO } from '@/api/classMission/types'

const route = useRoute()
const { push } = useRouter()

// 从路由中获取课程 ID
const classId = Number(route.query.cid)

const missionFormSchema = reactive<FormSchema[]>([
  {
    field: 'type',
    label: '任务类型',
    component: 'Select',
    componentProps: {
      placeholder: '请选择任务类型',
      options: [
        { label: 'PPT浏览', value: 'PPT浏览' },
        { label: '章节资料阅读', value: '章节资料阅读' },
        { label: '视频观看', value: '视频观看' },
        { label: '测试答题', value: '测试答题' },
        { label: '实践项目', value: '实践项目' }
      ]
    },
    formItemProps: {
      required: true
    }
  },
  {
    field: 'description',
    label: '任务说明',
    component: 'Input',
    componentProps: {
      type: 'textarea',
      rows: 3
    },
    formItemProps: {
      required: true
    }
  },
  {
    field: 'deadline',
    label: '截止时间',
    component: 'DatePicker',
    componentProps: {
      type: 'datetime',
      format: 'YYYY-MM-DD HH:mm:ss',
      valueFormat: 'YYYY-MM-DD HH:mm:ss'
    },
    formItemProps: {
      required: true
    }
  },
  {
    field: 'submit_method',
    label: '提交方式',
    component: 'Input'
  },
  {
    field: 'score',
    label: '得分',
    component: 'InputNumber',
    componentProps: {
      min: 0,
      max: 100
    }
  },
  {
    field: 'resource',
    label: '资源 ID',
    component: 'InputNumber'
  }
])

const { formRegister, formMethods } = useForm()
const { getFormData, getElFormExpose } = formMethods

const handleSubmit = async () => {
  const elForm = await getElFormExpose()
  if (!elForm) return

  await elForm.validate(async (valid) => {
    if (!valid) {
      ElMessage.warning('请检查任务信息填写是否完整')
      return
    }

    const formData = await getFormData<ClassMissionCreateDTO>()
    try {
      await addClassMissionApi({
        ...formData,
        cid: classId // 绑定课程 ID
      })
      ElMessage.success('任务创建成功')
      push({ path: '/mission/list', query: { cid: classId } })
    } catch (err) {
      console.error('任务创建失败：', err)
      ElMessage.error('任务创建失败，请稍后重试')
    }
  })
}
</script>

<template>
  <ContentWrap title="新增任务">
    <Form :schema="missionFormSchema" @register="formRegister" />
    <BaseButton type="primary" style="margin-top: 16px" @click="handleSubmit">提交</BaseButton>
  </ContentWrap>
</template>
