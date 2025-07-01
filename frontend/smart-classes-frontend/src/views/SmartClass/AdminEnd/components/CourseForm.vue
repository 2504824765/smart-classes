<script setup lang="tsx">
import { Form, FormSchema } from '@/components/Form'
import { ContentWrap } from '@/components/ContentWrap'
import { useForm } from '@/hooks/web/useForm'
import { BaseButton } from '@/components/Button'
import { ElMessage } from 'element-plus'
import type { UploadFile } from 'element-plus'
import { ref, reactive } from 'vue'
import { Classes, ClassesCreateDTO } from '@/api/classes/types'
import { addClassApi, updateClassApi } from '@/api/classes'
import { ResourceCreateDTO } from '@/api/resource/types'
import { addResourceApi } from '@/api/resource'
import { uploadResourcesApi } from '@/api/oss'

const uploadedResources = ref<ResourceCreateDTO[]>([])

const courseFormSchema = reactive<FormSchema[]>([
  {
    field: 'name',
    label: '课程名称',
    component: 'Input',
    formItemProps: {
      required: true
    }
  },
  {
    field: 'teacher_id',
    label: '授课教师ID',
    component: 'InputNumber',
    formItemProps: {
      required: true
    }
  },
  {
    field: 'credit',
    label: '学分',
    component: 'InputNumber',
    componentProps: {
      min: 0,
      max: 10
    }
  },
  {
    field: 'class_hours',
    label: '课时',
    component: 'InputNumber',
    componentProps: {
      min: 0
    }
  },
  {
    field: 'description',
    label: '课程简介',
    component: 'Input',
    componentProps: {
      type: 'textarea',
      rows: 4
    }
  },
  {
    field: 'active',
    label: '是否开放',
    component: 'Switch',
    value: true
  },
  {
    field: 'image',
    label: '课程封面图（URL 或文件名）',
    component: 'Input'
  },
  {
    field: 'graph',
    label: '课程知识图谱JSON（可选）',
    component: 'Input'
  },
  {
    field: 'files',
    label: '上传课程资料',
    component: 'Upload',
    componentProps: {
      multiple: true,
      limit: 3,
      httpRequest: async (options) => {
        try {
          const rawFile = options.file as File
          const res = await uploadResourcesApi(rawFile, '课程资料') // 你可以替换成动态 message 名称

          // 上传成功后构建 Resource
          const fileUrl = res.data.url
          const fileName = rawFile.name
          const fileType = fileName.split('.').pop() || ''

          const resource: ResourceCreateDTO = {
            name: fileName,
            path: fileUrl,
            type: fileType,
            description: fileName,
            classId: classId.value // 课程 ID，此时是 ref
          }

          uploadedResources.value.push(resource)

          // 手动触发 onSuccess 回调，让 UI 显示为上传成功
          options.onSuccess?.(res, rawFile)
          ElMessage.success(`文件 ${fileName} 上传成功`)
        } catch (error) {
          options.onError?.({
            name: 'UploadError',
            message: '文件上传失败',
            status: 500,
            method: 'POST',
            url: '/oss/uploadResources'
          })
        }
      },
      slots: {
        default: () => <BaseButton type="primary">点击上传</BaseButton>
      }
    }
  }
])

const { formRegister, formMethods } = useForm()
const { getFormData, getElFormExpose } = formMethods
const classId = ref<number>(0)

// 提交表单
const handleSubmit = async () => {
  const elForm = await getElFormExpose()
  if (!elForm) return

  await elForm.validate(async (valid) => {
    if (!valid) {
      ElMessage.warning('请检查表单填写是否完整')
      return
    }

    try {
      // 1. 创建一个空课程，后端返回课程 id
      const nullClass: ClassesCreateDTO = {
        name: '',
        teacher_id: 0,
        credit: 0,
        class_hours: 0,
        description: '',
        active: false,
        image: '',
        graph: ''
      }

      const res = await addClassApi(nullClass)
      const newClassId = res.data?.id
      if (!newClassId) {
        ElMessage.error('课程 ID 获取失败')
        return
      }

      // 2. 获取表单数据，并合并课程 ID，更新课程信息
      const formData = await getFormData<ClassesCreateDTO>()
      const updatedCourse: Classes = {
        id: newClassId,
        ...formData
      }

      await updateClassApi(updatedCourse)

      // 3. 绑定上传的资源（uploadedResources 是前面上传时填充的）
      const uploadList = uploadedResources.value
      if (uploadList.length > 0) {
        for (const resource of uploadList) {
          await addResourceApi({
            ...resource,
            classId: newClassId
          })
        }
      }

      ElMessage.success('课程创建成功并绑定资源')
      console.log('课程数据：', updatedCourse)
      console.log('绑定资源：', uploadList)

      // 4. 可选：重置表单、清空资源状态
      elForm.resetFields()
      uploadedResources.value = []

    } catch (err) {
      ElMessage.error('提交失败，请重试')
      console.error('提交错误：', err)
    }
  })
}

</script>

<template>
  <ContentWrap title="新增课程">
    <Form :schema="courseFormSchema" @register="formRegister" />
    <BaseButton type="primary" style="margin-top: 16px" @click="handleSubmit">提交</BaseButton>
  </ContentWrap>
</template>
