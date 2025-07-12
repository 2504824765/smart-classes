<script setup lang="tsx">
import { ContentWrap } from '@/components/ContentWrap'
import { ref, reactive, onMounted, watch } from 'vue'
import { Form, FormSchema } from '@/components/Form'
import { BaseButton } from '@/components/Button'
import { useRouter } from 'vue-router'
import { getClassMissionByIdApi } from '@/api/classMission'
import { getResourceByClassIdApi } from '@/api/resource'
import type { Resource } from '@/api/resource/types'

const router = useRouter()

interface FormData {
  cid: number | null
  type: string
  description: string
  deadline: string
  submit_method: string
  score: number | null
}

const formRef = ref()
const formData = reactive<FormData>({
  cid: null,
  type: '',
  description: '',
  deadline: '',
  submit_method: '',
  score: null
})

const route = useRouter().currentRoute
const missionId = ref<number | null>(null)
const classResources = ref<Resource[]>([])

watch(() => formData.deadline, (val) => {
  console.log('截止时间变更:', val)
})

onMounted(async () => {
  // 假设编辑时url带有id参数
  const id = route.value.query.id
  if (id) {
    missionId.value = Number(id)
    const missionRes = await getClassMissionByIdApi(missionId.value)
    const mission = missionRes.data
    // 拉取课程所有资源
    if (mission.classes && mission.classes.id) {
      const res = await getResourceByClassIdApi(mission.classes.id)
      classResources.value = res.data
    }
  }
})

const schema: FormSchema[] = [
  { field: 'cid', label: '课程ID', component: 'Select' },
  { field: 'type', label: '任务类型', component: 'Input' },
  { field: 'description', label: '描述', component: 'Input', componentProps: { type: 'textarea' } },
  {
    field: 'deadline',
    label: '截止时间',
    component: 'DatePicker',
    componentProps: { 
      type: 'datetime',
      format: 'YYYY-MM-DD HH:mm:ss',
      valueFormat: 'YYYY-MM-DD HH:mm:ss',
      disabledDate: (time: Date) => {
        return time.getTime() < new Date(new Date().setHours(0, 0, 0, 0)).getTime()
      }
    }
  },
  {
    field: 'submit_method',
    label: '提交方式',
    component: 'Input',
    componentProps: { type: 'textarea' }
  },
  { field: 'score', label: '分数', component: 'InputNumber' }
]

//必填校验
const rules = {
  cid: [{ required: true, message: '请输入课程ID' }],
  type: [{ required: true, message: '请输入任务类型' }],
  description: [{ required: true, message: '请输入描述' }],
  submit_method: [{ required: true, message: '请输入提交方式' }]
}

const create = () => {
  console.log(formData)
}

const goback = () => {
  router.push('/teacher/Mission')
}
</script>

<template>
  <ContentWrap>
    <Form ref="formRef" :schema="schema" v-model="formData" :rules="rules" />
    <div v-if="classResources.length" style="margin: 24px 0 0 0">
      <div style="font-weight: bold; margin-bottom: 8px">课程所有资源：</div>
      <div style="display: flex; flex-wrap: wrap; gap: 16px">
        <div
          v-for="file in classResources"
          :key="file.id"
          style="
            border: 1px solid #eee;
            border-radius: 6px;
            padding: 12px 18px;
            min-width: 180px;
            background: #fafbfc;
          "
        >
          <div><b>文件名：</b>{{ file.name }}</div>
          <div><b>类型：</b>{{ file.type }}</div>
          <div v-if="file.path"
            ><a :href="file.path" target="_blank" style="color: #409eff">下载/查看</a></div
          >
        </div>
      </div>
    </div>
    <div style="text-align: center; margin-top: 20px">
      <BaseButton type="primary" @click="create">确定</BaseButton>
      <BaseButton type="info" @click="goback">返回</BaseButton>
    </div>
  </ContentWrap>
</template>

<style scoped>
.demo-datetime-picker {
  display: flex;
  width: 100%;
  padding: 0;
  flex-wrap: wrap;
}
.demo-datetime-picker .block {
  padding: 30px 0;
  text-align: center;
  border-right: solid 1px var(--el-border-color);
  flex: 1;
}
.demo-datetime-picker .block:last-child {
  border-right: none;
}
.demo-datetime-picker .demonstration {
  display: block;
  color: var(--el-text-color-secondary);
  font-size: 14px;
  margin-bottom: 20px;
}
</style>
