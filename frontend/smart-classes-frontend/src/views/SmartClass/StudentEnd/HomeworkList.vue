<template>
  <div class="homework-list">
    <el-page-header content="作业列表" @back="router.back" />
    <el-row>
      <el-col :span="6" v-for="hw in homeworks" :key="hw.id">
        <HomeworkCard :homework="hw" @view-detail="goToDetail(hw)" />
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import HomeworkCard from './components/HomeworkCard.vue'
import { useRoute, useRouter } from 'vue-router'
import { ref, onMounted } from 'vue'
import type { StudentMission } from '@/api/studentMission/types' // 根据你项目中的路径调整
import { getStudentMissionByClass } from '@/api/studentMission/index' // TODO: 接口名你来补全
import { useUserStore } from '@/store/modules/user'
import { getStudentByUsernameApi } from '@/api/student/index'

const router = useRouter()
const route = useRoute()

const studentId = ref<number | null>(null)
const getStudentId = async (username: string) => {
  const res = await getStudentByUsernameApi(username)
  studentId.value = res.data.id
}

const userStore = useUserStore()
const loginInfo = userStore.getLoginInfo
const initialize = async ()  => {
  if (loginInfo) {
    const username = loginInfo.username
    await getStudentId(username)
  }
}

const homeworks = ref<StudentMission[]>([])

onMounted(async () => {
  await initialize()  
  const classId = Number(route.query.classId)
  if (!classId || !studentId.value) return

  const res = await getStudentMissionByClass(classId, studentId.value) // TODO: 接口你来实现
  homeworks.value = res.data
})

function goToDetail(hw: StudentMission) {
  router.push({
    name: 'HomeworkDetail',
    query: { missionId: hw.missionId }
  })
}
</script>

<style scoped>
.homework-list {
  padding: 20px;
}
</style>
