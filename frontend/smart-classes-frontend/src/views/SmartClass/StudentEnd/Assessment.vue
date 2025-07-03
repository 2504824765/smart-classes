<template>
  <el-card shadow="hover" style="margin: 20px; padding: 20px">
    <h2 style="text-align: center; margin-bottom: 20px">学生能力评估</h2>
    <AbilityRadarChart :abilities="studentAbilities" />
    <!-- <AbilityRadarChart :abilities="studentAbilities" v-if="loaded" /> -->
    <!-- <div v-else>正在加载能力数据...</div> -->
  </el-card>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import AbilityRadarChart from './components/AbilityRadarChart.vue'
// import { getStudentAbilityDataApi } from '@/api/assessment' // 假设接口名称

// const studentAbilities = ref({
//   conceptUnderstanding: 0,
//   logicalReasoning: 0,
//   problemSolving: 0,
//   creativeThinking: 0,
//   expressionClarity: 0
// })

const studentAbilities = ref({
  conceptUnderstanding: 85,
  logicalReasoning: 78,
  problemSolving: 90,
  creativeThinking: 70,
  expressionClarity: 88
})

// const loaded = ref(false)

// const fetchStudentAbility = async () => {
//   try {
//     const res = await getStudentAbilityDataApi()
//     studentAbilities.value = {
//       conceptUnderstanding: res.data.conceptUnderstanding || 0,
//       logicalReasoning: res.data.logicalReasoning || 0,
//       problemSolving: res.data.problemSolving || 0,
//       creativeThinking: res.data.creativeThinking || 0,
//       expressionClarity: res.data.expressionClarity || 0
//     }
//     loaded.value = true
//   } catch (error) {
//     console.error('获取能力评估数据失败:', error)
//   }
// }

// onMounted(() => {
//   fetchStudentAbility()
// })
</script>
