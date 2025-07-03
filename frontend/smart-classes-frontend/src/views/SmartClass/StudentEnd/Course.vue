<template>
  <div class="flex items-center mb-4 gap-4">
    <el-input v-model="searchKeyword" placeholder="搜索课程名" clearable />
    <el-switch v-model="onlyShowActive" active-text="仅显示启用课程" />
  </div>

  <draggable v-model="displayList" item-key="id" class="course-list" animation="200">
    <template #item="{ element }">
      <CourseCard :course="element" :key="element.id" :disabled="!element.active" />
    </template>
  </draggable>
</template>

<script setup lang="ts">
import CourseCard from './components/CourseCard.vue'
import { Classes } from '@/api/classes/types'
import { onMounted, ref, watch } from 'vue'
import { getStudentByUsernameApi } from '@/api/student/index'
import draggable from 'vuedraggable'
import { useUserStore } from '@/store/modules/user'
import { getAssociatedBySidApi } from '@/api/studentClasses/index'
import { getClassesByIdApi } from '@/api/classes/index'

const studentId = ref<number>()
const getStudentId = async (username: string) => {
  const res = await getStudentByUsernameApi(username)
  studentId.value = res.data.id
}

const userStore = useUserStore()
const loginInfo = userStore.getLoginInfo
const initialize = async () => {
  if (loginInfo) {
    const username = loginInfo.username
    await getStudentId(username)
  }
}

const searchKeyword = ref('')
const onlyShowActive = ref(false)

const displayList = ref<Classes[]>([])

const courseList = ref<Classes[]>([])

const queryCourseList = async () => {
  try {
    // 获取所有选课关联记录
    if(!studentId.value){ 
      return
    }
    console.log(studentId.value)
    const associatedRes = await getAssociatedBySidApi(studentId.value)
    console.log('选课关联记录：', associatedRes)
    const associatedList = associatedRes.data // 每项应包含 cid

    const cidList: number[] = associatedList.map((item) => item.classes.id)
    console.log('选课课程ID列表：', cidList)
    // 并发获取所有课程信息
    const classPromises = cidList.map((cid) => getClassesByIdApi(cid))
    const classResults = await Promise.all(classPromises)

    // 提取课程数据
    courseList.value = classResults.map((res) => res.data)

    console.log('选课课程列表：', courseList.value)
  } catch (err) {
    console.error('加载课程列表失败', err)
  }
}

watch([courseList, searchKeyword, onlyShowActive], () => {
  displayList.value = courseList.value.filter((course) => {
    const matchKeyword = course.name?.includes(searchKeyword.value)
    const matchActive = onlyShowActive.value ? course.active : true
    return matchKeyword && matchActive
  })
})

onMounted(async () => {
  await initialize()
  await queryCourseList()
})
</script>

<style scoped>
.course-list {
  display: flex;
  flex-wrap: wrap;
  gap: 24px;
}
</style>
