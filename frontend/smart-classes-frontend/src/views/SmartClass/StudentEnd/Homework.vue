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
import { addStudentMission, getStudentMissionByClass, updateStudentMission } from '@/api/studentMission/index'
import { ref, onMounted, watch } from 'vue'
import { useUserStore } from '@/store/modules/user'
import { ElMessage } from 'element-plus'
import { getClassMissionByCidApi } from '@/api/classMission'
import { ClassMission } from '@/api/classMission/types'
import { StudentMission } from '@/api/studentMission/types'
import { tr } from 'element-plus/es/locale'

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
    // 获取课程任务
    const classMissionRes = await getClassMissionByCidApi(cls.id)
    const classMissions: ClassMission[] = classMissionRes.data || []

    if (classMissions.length === 0) {
      // 没有任务，跳过统计
      courseList.push({
        id: cls.id,
        name: cls.name,
        description: cls.description,
        imageUrl: cls.imageUrl || '/default.png',
        total: 0,
        unfinished: 0,
        isActive: cls.isActive
      })
      continue
    }

    // 获取该学生在该课程下的学生任务
    const studentMissionRes = await getStudentMissionByClass(cls.id, studentId.value)
    const studentMissions: StudentMission[] = studentMissionRes.data || []

    const studentMissionMap = new Map<number, StudentMission>()
    studentMissions.forEach((sm) => {
      studentMissionMap.set(sm.classMission.id, sm) // 以 classMissionId 为 key
    })
    const now = new Date()

    // 补充创建缺失的 studentMission
    for (const mission of classMissions) {
      const studentMission = studentMissionMap.get(mission.id)

      const deadline = new Date(mission.deadline)
      const isExpired = deadline < now

      if (!studentMission) {
        // 学生任务不存在，创建
        await addStudentMission({
          studentId: studentId.value,
          classMissionId: mission.id,
          isActive: !isExpired, // 若过期则直接设为 false
          score: 0,
          isDone: false,
          reportUrl: '',
          aiCommentUrl: ''
        })
      } else if (studentMission.isActive && isExpired) {
        // 任务存在，但已经过期还没设置 inactive，更新为 inactive
        await updateStudentMission({ ...studentMission, isActive: false })
      }
    }

    // 获取最新学生任务列表（包含刚补充的）
    const updatedStudentMissionRes = await getStudentMissionByClass(cls.id, studentId.value)
    const updatedMissions: StudentMission[] = updatedStudentMissionRes.data || []

    const total = updatedMissions.length
    const unfinished = updatedMissions.filter((m) => m.isDone).length

    courseList.push({
      id: cls.id,
      name: cls.name,
      description: cls.description,
      imageUrl: cls.imageUrl || '/default.png',
      total,
      unfinished,
      isActive: cls.isActive
    })
  }

  courses.value = courseList
}

watch([classes, searchKeyword, onlyShowActive], () => {
  displayList.value = classes.value.filter((course) => {
    const matchKeyword = course.name?.includes(searchKeyword.value)
    const matchActive = onlyShowActive.value ? course.isActive : true
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
