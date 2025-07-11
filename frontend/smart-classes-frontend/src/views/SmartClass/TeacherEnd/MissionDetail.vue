<template>
  <div class="mission-detail">
    <el-page-header content="任务详情" @back="router.back" />

    <!-- 任务详情 -->
    <el-card class="mb-4" style="margin-top: 10px">
      <template #header>
        <span>任务信息</span>
      </template>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="任务类型">{{ mission?.type }}</el-descriptions-item>
        <el-descriptions-item label="截止时间">{{ mission?.deadline }}</el-descriptions-item>
        <el-descriptions-item label="提交方式">{{ mission?.submitMethod }}</el-descriptions-item>
        <el-descriptions-item label="总分">{{ mission?.score }}</el-descriptions-item>
        <el-descriptions-item label="任务描述" :span="2">{{
          mission?.description
        }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <!-- 资源展示区 -->
    <el-card class="mb-4">
      <template #header>
        <span>相关资源</span>
      </template>
      <div v-if="fileCards.length">
        <div v-for="file in fileCards" :key="file.url" class="mb-2">
          <FileDisplay :url="file.url" />
        </div>
      </div>
      <div v-else>
        <el-empty description="暂无资源" />
      </div>
    </el-card>

    <!-- 学生完成情况 -->
    <el-card>
      <template #header>
        <span>学生完成情况</span>
      </template>
      <el-row :gutter="20">
        <el-col
          :span="4"
          v-for="studentMission in studentMissions"
          :key="studentMission.id"
          @click="viewStudentDetail(studentMission.id)"
        >
          <div
            class="student-card"
            :class="{ done: studentMission.isDone, undone: !studentMission.isDone }"
          >
            <el-avatar :size="60">{{ studentMission.student.name.slice(0, 1) }}</el-avatar>
            <div class="student-name">{{ studentMission.student.name }}</div>
          </div>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getStudentMissionByMission } from '@/api/studentMission/index'
import { getClassMissionByIdApi } from '@/api/classMission/index'
import { getResourceByIdApi } from '@/api/resource/index'
import type { ClassMission } from '@/api/classMission/types'
import FileDisplay from './components/FileDisplay.vue'
import { PREFIX } from '@/constants'
import { StudentMission } from '@/api/studentMission/types'
import { getAllClassMissionResourcesByClassMissionId } from '@/api/classMissionResource'

const route = useRoute()
const router = useRouter()
const { push } = useRouter()

const mission = ref<ClassMission>()
const fileCards = ref<{ name: string; url: string }[]>([])
const studentMissions = ref<(StudentMission & { isDone: boolean })[]>([])

onMounted(async () => {
  const missionId = Number(route.query.id)
  const res = await getClassMissionByIdApi(missionId)
  console.log(res.data)
  mission.value = res.data

  // 获取资源文件（TODO: 请替换为实际资源接口）
  if (mission.value) {
    const resourceRes = await getAllClassMissionResourcesByClassMissionId(mission.value.id)
    console.log('res', resourceRes)

    if (Array.isArray(resourceRes.data)) {
      fileCards.value = resourceRes.data.map((item: any) => ({
        url: PREFIX + item.path,  // 如果你有 OSS 前缀
        name: item.name || '未命名资源'
      }))
    } else {
      fileCards.value = []
    }
  }

  // 获取所有学生完成情况（已适配接口）
  const studentRes = await getStudentMissionByMission(missionId)
  console.log(studentRes.data)
  studentMissions.value = studentRes.data.map((s) => ({
    ...s,
    isDone: s.isDone
  }))
  console.log(studentMissions.value)
  console.log(missionId)
})

const viewStudentDetail = (studentMissionId: number) => {
  console.log(studentMissionId)
  if(!studentMissionId){
    return
  } 
  push({ path: '/course/detail/studentDetail', query: { studentMissionId: studentMissionId} })
}
</script>

<style scoped>  
.mission-detail {
  padding: 20px;
}

.mb-4 {
  margin-bottom: 20px;
}

.student-card {
  text-align: center;
  padding: 15px;
  border-radius: 8px;
  cursor: pointer;
  transition: 0.3s;
  background-color: #fdf6ec;
}

.student-card.done {
  background-color: #e1f3d8;
}

.student-card.undone {
  background-color: #fff7e6;
}

.student-card:hover {
  box-shadow: 0 0 8px rgba(0, 0, 0, 0.1);
}

.student-name {
  margin-top: 10px;
  font-weight: 500;
}
</style>
