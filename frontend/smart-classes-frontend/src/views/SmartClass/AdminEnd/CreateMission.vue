<script setup lang="tsx">
import { ContentWrap } from '@/components/ContentWrap'
import { useI18n } from '@/hooks/web/useI18n'
import { Table, TableColumn } from '@/components/Table'
import { getTableListApi } from '@/api/table'
import { TableData } from '@/api/table/types'
import { ref, h, reactive } from 'vue'
import { ElTag } from 'element-plus'
import { Form, FormSchema } from '@/components/Form'
import { BaseButton } from '@/components/Button'
import { useRouter } from 'vue-router'

const router = useRouter()
const { t } = useI18n()
const value = ref('')
const defaultTime = new Date(2000, 1, 1, 12, 0, 0)

const shortcuts = [
  {
    text: 'Today',
    value: new Date()
  },
  {
    text: 'Yesterday',
    value: () => {
      const date = new Date()
      date.setDate(date.getDate() - 1)
      return date
    }
  },
  {
    text: 'A week ago',
    value: () => {
      const date = new Date()
      date.setDate(date.getDate() - 7)
      return date
    }
  }
]

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

// const classList = ref([])
// const getClasses = async () => {
//   const res = await getClassesApi()
//   classList.value = res.data
// }
// getClasses()

const schema: FormSchema[] = [
  { field: 'cid', label: '课程ID', component: 'Select' },
  { field: 'type', label: '任务类型', component: 'Input' },
  { field: 'description', label: '描述', component: 'Input', componentProps: { type: 'textarea' } },
  {
    field: 'deadline',
    label: '截止时间',
    component: 'DatePicker',
    componentProps: { type: 'datetime' }
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
  //deadline: [{ required: true, message: '请选择截止时间' }],
  submit_method: [{ required: true, message: '请输入提交方式' }]
  //score: [{ required: true, message: '请输入分数' }]
}

const form = reactive({
  name: '',
  region: '',
  date1: '',
  date2: '',
  delivery: false,
  type: [],
  resource: '',
  desc: ''
})

const create = () => {
  console.log(formData)
}

const goback = () => {
  router.push('/teacher/Mission')
}
</script>

<template>
  <Form ref="formRef" :schema="schema" v-model="formData" :rules="rules" />
  <div style="text-align: center; margin-top: 20px">
    <BaseButton type="primary" @click="create">确定</BaseButton>
    <BaseButton type="primary" @click="goback">返回</BaseButton>
  </div>
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
