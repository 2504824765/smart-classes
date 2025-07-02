<script setup lang="tsx">
import { Form, FormSchema } from '@/components/Form'
import { ContentWrap } from '@/components/ContentWrap'
import { useForm } from '@/hooks/web/useForm'
import { BaseButton } from '@/components/Button'
import { ElMessage } from 'element-plus'
import { ref, reactive, onMounted } from 'vue'
import { Classes, ClassesCreateDTO } from '@/api/classes/types'
import { addClassApi, updateClassApi, getClassesByIdApi } from '@/api/classes'
import { PendingUploadResource, ResourceCreateDTO } from '@/api/resource/types'
import { addResourceApi } from '@/api/resource'
import { uploadResourcesApi } from '@/api/oss'
import { Teacher } from '@/api/teacher/types'
import { getAllTeacherApi } from '@/api/teacher'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const isEdit = ref(false)
const courseId = ref<number | null>(null)

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
    field: 'teacherId',
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
            label: `${t.name}（${t.dept?.name || '未分配院系'}）`,
            value: t.id
          }))
          return
        }
        teacherOptions.value = allTeachers.value
          .filter(t => t.name.includes(query) || t.dept?.name?.includes(query) || String(t.id).includes(query))
          .map(t => ({
            label: `${t.name}（${t.dept?.name || '未分配院系'}）`,
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
    field: 'classHours',
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
    field: 'imageUrl',
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

onMounted(async () => {
  teacherLoading.value = true
  try {
    const res = await getAllTeacherApi()
    allTeachers.value = res.data
    teacherOptions.value = res.data.map(t => ({
      label: `${t.name}（${t.dept?.name || '未分配院系'}）`,
      value: t.id
    }))
    
    // 编辑模式：拉取课程详情
    const id = route.query.id
    if (id) {
      isEdit.value = true
      courseId.value = Number(id)
      try {
        const courseRes = await getClassesByIdApi(courseId.value)
        const course = courseRes.data
        formMethods.setValues({
          name: course.name,
          teacherId: course.teacher?.id,
          credit: course.credit,
          classHours: course.classHours,
          description: course.description,
          active: course.active,
          imageUrl: course.imageUrl,
          graph: course.graph
        })
      } catch (error) {
        ElMessage.error('获取课程信息失败')
        console.error('获取课程信息错误：', error)
      }
    } else {
      isEdit.value = false
      courseId.value = null
    }
  } finally {
    teacherLoading.value = false
  }
})

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
      const formData = await getFormData<ClassesCreateDTO>()
      
      if (isEdit.value) {
        if (!courseId.value) {
          ElMessage.error('课程ID缺失，无法编辑')
          return
        }
        await updateClassApi({ ...formData, id: courseId.value })
        ElMessage.success('课程修改成功')
      } else {
        await addClassApi(formData)
        ElMessage.success('课程创建成功')
      }

      // 处理资源上传（仅在新增模式下）
      if (!isEdit.value && pendingResources.value.length > 0) {
        // 获取新创建的课程ID
        const newCourseId = courseId.value || 0
        
        for (const resource of pendingResources.value) {
          const uploadRes = await uploadResourcesApi(resource.file, '课程资料')
          const filePath = uploadRes.data.url

          uploadedResources.push({
            name: resource.name,
            path: filePath,
            type: resource.type,
            description: resource.description,
            classId: newCourseId
          })
        }

        // 插入数据库资源记录
        for (const resource of uploadedResources) {
          await addResourceApi(resource)
        }
      }

      router.push({ path: '/admin/courseManage' })
    } catch (err) {
      ElMessage.error('提交失败，请重试')
      console.error('提交错误：', err)
    }
  })
}
</script>

<template>
  <ContentWrap :title="isEdit ? '编辑课程' : '新增课程'">
    <Form :schema="courseFormSchema" @register="formRegister" />
    <el-table :data="uploadedResources" style="width: 100%">
      <el-table-column prop="name" label="文件名" />
      <el-table-column prop="type" label="类型" width="120" />
    </el-table>
    <BaseButton type="primary" style="margin-top: 16px" @click="handleSubmit">
      {{ isEdit ? '更新' : '提交' }}
    </BaseButton>
  </ContentWrap>
</template>
