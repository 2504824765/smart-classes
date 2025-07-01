<template>
  <div class="course-list">
    <el-row>
      <el-col :span="6" v-for="course in courses" :key="course.name">
        <CourseHCard :course="course" @view-homework="goToHomework(course)" />
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import CourseHCard from './components/CourseHCard.vue'
import { useRouter } from 'vue-router'
import { Classes, CourseDisplayData } from '@/api/classes/types'
import { getAllClassesApi } from '@/api/classes/index'
import { getStudentByUsernameApi } from '@/api/student/index'
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
if (loginInfo) {
  const username = loginInfo.username
  getStudentId(username)
}

const classes = ref<Classes[]>([])
const courses = ref<CourseDisplayData[]>([])

const loadCourses = async () => {
  // 假设当前用户名存在 store 中
  if (!studentId.value) return

  // 获取所有课程
  const classRes = await getAllClassesApi()
  classes.value = classRes.data

  const courseList: CourseDisplayData[] = []

  // 遍历课程，获取任务统计数据
  for (const cls of classes.value) {
    const missionRes = await getStudentMissionByClass(cls.id, studentId.value,)

    const missions = missionRes.data || []
    const total = missions.length
    const unfinished = missions.filter(m => !m.isActive).length

    courseList.push({
      name: cls.name,
      description: cls.description,
      imageUrl: cls.imageUrl || '/default.png',
      total,
      unfinished
    })
  }

  courses.value = courseList
}

function goToHomework(course: any) {
  push({ path: '/homework/list', query: { course: course.name } })
}

onMounted(() => {
  loadCourses()
})
</script>

<style scoped>
.course-list {
  padding: 20px;
}
</style>
