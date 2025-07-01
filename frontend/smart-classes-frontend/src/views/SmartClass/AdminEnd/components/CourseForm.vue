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
      limit: 3,
      action: 'https://your-oss-upload-api-or-direct-oss-url',
      multiple: true,
      onSuccess: async (response: { url?: string }, uploadFile: UploadFile) => {
        const fileUrl = response?.url || uploadFile.url // 适配 response 返回结构
        if (!fileUrl) return

        const fileName = decodeURIComponent(fileUrl.split('/').pop() ?? '未知文件')
        const fileType = fileName.split('.').pop()?.toLowerCase() ?? 'unknown'

        // TODO: 替换为你当前课程的 id
        const currentClassId = classId.value

        // 构造 ResourceCreateDTO
        const resource: ResourceCreateDTO = {
          name: fileName,
          path: fileUrl,
          type: fileType,
          description: fileName,
          classId: currentClassId
        }

        // 发送资源信息到后端
        try {
          await addResourceApi(resource)
          ElMessage.success(`${fileName} 上传并登记资源成功`)
        } catch (err) {
          console.error('资源登记失败', err)
          ElMessage.error('资源信息登记失败')
        }
      },
      slots: {
        default: () => <BaseButton type="primary">点击上传</BaseButton>,
        tip: () => <div class="el-upload__tip">最多上传3个文件，大小限制请参考 OSS 配置</div>
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
    if (valid) {
      const nullClass = {
        name: '',
        teacher_id: 0,
        credit: 0,
        class_hours: 0,
        description: '',
        active: false,
        image: '',
        graph: '',
      }
      const res = await addClassApi(nullClass) 
      classId.value = res.data.id 
      const formData = await getFormData<ClassesCreateDTO>()
      const updatedCourse: Classes = {
        id: classId.value,           
        ...formData              
      }

      await updateClassApi(updatedCourse)

      console.log('课程信息：', formData)
      ElMessage.success('提交成功（模拟）')
    } else {
      ElMessage.warning('请检查表单填写是否完整')
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
