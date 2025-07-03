<template>
  <div class="homework-detail">
    <el-page-header content="作业详情" @back="router.back" style="margin-bottom: 16px;" />

    <el-card>
      <template #header>
        <div class="flex items-center justify-between">
          <h2 class="text-xl font-bold">{{ classMission?.classes.name }} - {{ classMission?.type }}</h2>
          <el-tag type="success" v-if="studentMission?.done">已完成</el-tag>
          <el-tag type="warning" v-else>未完成</el-tag>
        </div>
      </template>

      <el-descriptions title="作业信息" :column="2" border>
        <el-descriptions-item label="课程">{{ classMission?.classes.name }}</el-descriptions-item>
        <el-descriptions-item label="作业类型">{{ classMission?.type }}</el-descriptions-item>
        <el-descriptions-item label="截止时间">{{ classMission?.deadline }}</el-descriptions-item>
        <el-descriptions-item label="提交方式">{{ classMission?.submitMethod }}</el-descriptions-item>
        <el-descriptions-item label="作业说明" :span="2">
          {{ classMission?.description }}
        </el-descriptions-item>
      </el-descriptions>

      <el-divider>学生完成情况</el-divider>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="学生">{{ studentMission?.student.name }}</el-descriptions-item>
        <el-descriptions-item label="得分">{{ studentMission?.score ?? '未评分' }}</el-descriptions-item>
      </el-descriptions>

      <!-- 可选：上传入口、文件查看等 -->
      <div class="mt-4">
        <el-alert type="info" show-icon title="提交入口功能暂未开发" />
      </div>
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
