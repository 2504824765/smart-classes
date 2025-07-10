<template>
  <div class="homework-list">
    <el-page-header content="作业列表" @back="router.back" />

    <draggable v-model="homeworks" item-key="id" class="grid" animation="200">
      <template #item="{ element }">
        <el-col :span="6">
          <HomeworkCard :homework="element" @view-detail="goToDetail" />
        </el-col>
      </template>
    </draggable>
  </div>
</template>

<script setup lang="ts">
import HomeworkCard from './components/HomeworkCard.vue'
import { useRoute, useRouter } from 'vue-router'
import { ref, onMounted } from 'vue'
import type { StudentMission } from '@/api/studentMission/types'
import { getStudentMissionByClass } from '@/api/studentMission/index'
import { useUserStore } from '@/store/modules/user'
import { getStudentByUsernameApi } from '@/api/student/index'
import draggable from 'vuedraggable'

const router = useRouter()
const route = useRoute()

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

const homeworks = ref<StudentMission[]>([])

onMounted(async () => {
  const classId = Number(route.query.classId)
  console.log(classId)
  await initialize()
  if (!classId || !studentId.value) return

  const res = await getStudentMissionByClass(classId, studentId.value) // TODO: 接口你来实现
  console.log(res)
  homeworks.value = res.data
})

function goToDetail(hw: StudentMission) {
  router.push({
    name: 'HomeworkDetail',
    query: { missionId: hw.classMission.id, studentMissionId: hw.id }
  })
}
</script>

<style scoped>
.homework-list {
  padding: 20px;
}

.grid {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.grid > .el-col {
  flex: 0 0 25%; /* 等价于 span=6 */
  max-width: 25%;
}
</style>
