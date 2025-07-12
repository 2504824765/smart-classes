<template>
  <el-card shadow="hover" style="margin: 20px; padding: 20px">
    <h2 style="text-align: center; margin-bottom: 20px">学生能力评估</h2>
    <template v-if="loaded">
      <AbilityRadarChart v-if="hasAbilityData" :abilities="studentAbilities" />
      <div v-else style="text-align: center; color: gray">暂无能力评估数据</div>
    </template>
    <div v-else>正在加载能力数据...</div>
  </el-card>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import AbilityRadarChart from './components/AbilityRadarChart.vue'
import { useUserStore } from '@/store/modules/user'
import { getStudentByUsernameApi } from '@/api/student/index'

const studentId = ref<number | null>(null)

const studentAbilities = ref({
  conceptUnderstanding: 60,
  logicalReasoning: 60,
  problemSolving: 60,
  innovativeThinking: 60,
  expressionNorms: 60
})

const loaded = ref(false)
const hasAbilityData = ref(false)

const userStore = useUserStore()
const loginInfo = userStore.getLoginInfo

const getStudentInfo = async (username: string) => {
  try {
    const res = await getStudentByUsernameApi(username)
    const studentData = res.data
    studentId.value = studentData.id

    if (studentData.studentData) {
      const ability = studentData.studentData
      studentAbilities.value = {
        conceptUnderstanding: ability.conceptUnderstanding >= 60 ? ability.conceptUnderstanding : 60,
        logicalReasoning: ability.logicalReasoning >= 60 ? ability.logicalReasoning : 60,
        problemSolving: ability.problemSolving >= 60 ? ability.problemSolving : 60,
        innovativeThinking: ability.innovativeThinking >= 60 ? ability.innovativeThinking : 60,
        expressionNorms: ability.expressionNorms >= 60 ? ability.expressionNorms : 60
      }
      hasAbilityData.value = true
    } else {
      console.warn('该学生尚未填写能力评估数据')
      hasAbilityData.value = false
    }

    loaded.value = true
  } catch (error) {
    console.error('获取学生信息失败:', error)
    loaded.value = true
  }
}

const initialize = async () => {
  if (loginInfo && loginInfo.username) {
    await getStudentInfo(loginInfo.username)
  }
}

onMounted(() => {
  initialize()
})
</script>
