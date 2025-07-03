<template>
  <div class="homework-detail">
    <el-page-header content="作业详情" @back="router.back" style="margin-bottom: 10px" />
    <el-card>
      <h2>{{}}</h2>
      <p>这里将展示作业的详细要求、上传入口等内容（待开发）。</p>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { useRoute, useRouter } from 'vue-router'
import { onMounted, ref } from 'vue'
import { ClassMission } from '@/api/classMission/types'
import { getClassMissionByIdApi } from '@/api/classMission/index'
import { getStudentMissionById } from '@/api/studentMission/index'
import { StudentMission } from '@/api/studentMission/types'

const route = useRoute()
const router = useRouter()

const classMission = ref<ClassMission>()
const studentMission = ref<StudentMission>()

const queryDate = async () => {
  const missionId = Number(route.query.missionId)
  const studentMissionId = Number(route.query.studentMissionId)
  const classMissionRes = await getClassMissionByIdApi(missionId)
  if (classMissionRes.code === 200) {
    classMission.value = classMissionRes.data
    console.log(classMission.value)
  }
  const studentMissionRes = await getStudentMissionById(studentMissionId)
  if (studentMissionRes.code === 200) {
    studentMission.value = studentMissionRes.data
    console.log(studentMission.value)
  }
}

onMounted(() => {
  queryDate()
})
</script>

<style scoped>
.homework-detail {
  padding: 20px;
}
</style>
