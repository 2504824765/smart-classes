<template>
  <div>
    <div class="flex items-center mb-4 gap-4">
      <el-input v-model="searchKeyword" placeholder="搜索课程名" clearable />
      <el-switch v-model="onlyShowActive" active-text="仅显示启用课程" />
    </div>

    <div v-if="displayList.length === 0" class="flex flex-col items-center justify-center mt-10">
      <el-empty description="你还没有选择任何课程">
        <router-link to="/course/select">
          <el-button type="primary">去选课</el-button>
        </router-link>
      </el-empty>
    </div>
    <draggable v-else v-model="courses" item-key="id" class="card-grid" animation="200">
      <template #item="{ element }">
        <CourseHCard :course="element" @view-homework="goToHomework(element)" />
      </template>
    </draggable>
  </div>
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
import { ref, onMounted, watch } from 'vue'
import { useUserStore } from '@/store/modules/user'
import { ElMessage } from 'element-plus'

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

const searchKeyword = ref('')
const onlyShowActive = ref(false)

const displayList = ref<Classes[]>([])

const loadCourses = async () => {
  // 假设当前用户名存在 store 中
  if (!studentId.value) return
  // 获取所有课程
  try {
    // 获取所有选课关联记录
    if (!studentId.value) {
      return
    }

    const associatedRes = await getAssociatedBySidApi(studentId.value)

    const associatedList = associatedRes.data // 每项应包含 cid

    const cidList: number[] = associatedList.map((item) => item.classes.id)

    // 并发获取所有课程信息
    const classPromises = cidList.map((cid) => getClassesByIdApi(cid))
    const classResults = await Promise.all(classPromises)

    // 提取课程数据
    classes.value = classResults.map((res) => res.data)

    console.log('选课课程列表：', classes.value)
  } catch (err) {
    ElMessage.warning('获取课程列表失败，请先选课')
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

watch([classes, searchKeyword, onlyShowActive], () => {
  displayList.value = classes.value.filter((course) => {
    const matchKeyword = course.name?.includes(searchKeyword.value)
    const matchActive = onlyShowActive.value ? course.active : true
    return matchKeyword && matchActive
  })
})

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
