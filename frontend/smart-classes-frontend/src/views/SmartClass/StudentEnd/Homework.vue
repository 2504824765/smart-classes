<template>
  <draggable v-model="courses" item-key="id" class="card-grid" animation="200">
    <template #item="{ element }">
      <CourseHCard :course="element" @view-homework="goToHomework(element)" />
    </template>
  </draggable>
</template>

<script setup lang="ts">
import CourseHCard from './components/CourseHCard.vue'
import { useRouter } from 'vue-router'
import draggable from 'vuedraggable'
import { Classes, CourseDisplayData } from '@/api/classes/types'
import { getClassesByIdApi } from '@/api/classes/index'
import { getStudentByUsernameApi } from '@/api/student/index'
import { getAssociatedBySidApi } from '@/api/studentClasses/index'
import { getStudentMissionByClass } from '@/api/studentMission/index'
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/store/modules/user'

const { push } = useRouter()

const studentId = ref<number | null>(null)
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

const classes = ref<Classes[]>([])
const courses = ref<CourseDisplayData[]>([])

const loadCourses = async () => {
  // 假设当前用户名存在 store 中
  if (!studentId.value) return
  // 获取所有课程
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
    classes.value = classResults.map((res) => res.data)

    console.log('选课课程列表：', classes.value)
  } catch (err) {
    console.error('加载课程列表失败', err)
  }

  const courseList: CourseDisplayData[] = []

  // 遍历课程，获取任务统计数据
  for (const cls of classes.value) {
    const missionRes = await getStudentMissionByClass(cls.id, studentId.value)

    const missions = missionRes.data || []
    const total = missions.length
    const unfinished = missions.filter((m) => !m.active).length

    courseList.push({
      id: cls.id,
      name: cls.name,
      description: cls.description,
      imageUrl: cls.imageUrl || '/default.png',
      total,
      unfinished,
      active: cls.active
    })
  }

  courses.value = courseList
}

function goToHomework(course: CourseDisplayData) {
  push({ path: '/homework/list', query: { classId: course.id } })
}

onMounted(async () => {
  await initialize()
  await loadCourses()
})
</script>

<style scoped>
.card-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
}
</style>
