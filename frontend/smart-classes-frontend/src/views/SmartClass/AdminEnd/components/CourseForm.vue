<script setup lang="tsx">
import { Form, FormSchema } from '@/components/Form'
import { ContentWrap } from '@/components/ContentWrap'
import { useForm } from '@/hooks/web/useForm'
import { BaseButton } from '@/components/Button'
import { ElMessage } from 'element-plus'
import { ref, reactive, onMounted } from 'vue'
import { Classes, ClassesCreateDTO } from '@/api/classes/types'
import { addClassApi, updateClassApi } from '@/api/classes'
import { PendingUploadResource, ResourceCreateDTO } from '@/api/resource/types'
import { addResourceApi } from '@/api/resource'
import { uploadResourcesApi } from '@/api/oss'
import { Teacher } from '@/api/teacher/types'
import { getAllTeacherApi } from '@/api/teacher'

//待上传的资源
const pendingResources = ref<PendingUploadResource[]>([])
// 绑定上传的资源（uploadedResources 是前面上传时填充的）
const uploadedResources: ResourceCreateDTO[] = []
const allTeachers = ref<Teacher[]>([])  // 所有教师原始数据
const teacherOptions = ref<{ label: string; value: number }[]>([])  // 绑定 Select
const teacherLoading = ref(false)

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
    label: '授课教师',
    component: 'Select',
    componentProps: {
      filterable: true,
      clearable: true,
      loading: teacherLoading,
      options: teacherOptions,
      filterMethod: (query: string) => {
        if (!query) {
          teacherOptions.value = allTeachers.value.map(t => ({
            label: `${t.name}（ID: ${t.id}）`,
            value: t.id
          }))
          return
        }
        teacherOptions.value = allTeachers.value
          .filter(t => t.name.includes(query) || String(t.id).includes(query))
          .map(t => ({
            label: `${t.name}（ID: ${t.id}）`,
            value: t.id
          }))
      }
    },
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
        const rawFile = options.file as File
        const fileName = rawFile.name
        const fileType = fileName.split('.').pop() || ''

        pendingResources.value.push({
          name: fileName,
          type: fileType,
          description: fileName,
          file: rawFile
        })

        options.onSuccess?.({}, rawFile)
        ElMessage.success(`已添加到待上传列表：${fileName}`)
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
        teacherId: 1,
        credit: 0,
        classHours: 0,
        description: '',
        active: false,
        image: '',
        graph: ''
      }

      const res = await addClassApi(nullClass)
      console.log(res)
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

      for (const resource of pendingResources.value) {
        const uploadRes = await uploadResourcesApi(resource.file, '课程资料')
        const filePath = uploadRes.data.url

        uploadedResources.push({
          name: resource.name,
          path: filePath,
          type: resource.type,
          description: resource.description,
          classId: newClassId
        })
      }

      // 插入数据库资源记录
      for (const resource of uploadedResources) {
        await addResourceApi(resource)
      }

      ElMessage.success('课程创建成功并绑定资源')
      // 4. 可选：重置表单、清空资源状态
      elForm.resetFields()
    } catch (err) {
      ElMessage.error('提交失败，请重试')
      console.error('提交错误：', err)
    }
  })
}

onMounted(async () => {
  teacherLoading.value = true
  try {
    const res = await getAllTeacherApi()
    allTeachers.value = res.data
    teacherOptions.value = res.data.map(t => ({
      label: `${t.name}（ID: ${t.id}）`,
      value: t.id
    }))
  } finally {
    teacherLoading.value = false
  }
})
</script>

<template>
  <ContentWrap title="新增课程">
    <Form :schema="courseFormSchema" @register="formRegister" />
    <el-table :data="uploadedResources" style="width: 100%">
      <el-table-column prop="name" label="文件名" />
      <el-table-column prop="type" label="类型" width="120" />
    </el-table>
    <BaseButton type="primary" style="margin-top: 16px" @click="handleSubmit">提交</BaseButton>
  </ContentWrap>
</template>
