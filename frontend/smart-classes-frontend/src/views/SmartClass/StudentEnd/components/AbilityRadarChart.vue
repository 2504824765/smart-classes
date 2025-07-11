<template>
  <div ref="chartRef" class="radar-chart"></div>
</template>

<script setup lang="ts">
import { onMounted, ref, watch, nextTick } from 'vue'
import * as echarts from 'echarts'

const props = defineProps<{
  abilities: {
    conceptUnderstanding: number
    logicalReasoning: number
    problemSolving: number
    innovativeThinking: number
    expressionNorms: number
  }
}>()

const chartRef = ref<HTMLDivElement | null>(null)
let chartInstance: echarts.ECharts | null = null

const initChart = () => {
  if (!chartRef.value) return

  chartInstance = echarts.init(chartRef.value)

  chartInstance.setOption({
    tooltip: {},
    radar: {
      indicator: [
        { name: '概念理解', min: 60, max: 100 },
        { name: '逻辑推理', min: 60, max: 100 },
        { name: '问题解决', min: 60, max: 100 },
        { name: '创新思维', min: 60, max: 100 },
        { name: '表达规范', min: 60, max: 100 }
      ],
      radius: '65%',
      splitNumber: 5,
      shape: 'polygon',
      splitLine: {
        lineStyle: {
          color: '#ddd'
        }
      },
      axisLine: {
        lineStyle: {
          color: '#999'
        }
      }
    },
    series: [
      {
        type: 'radar',
        data: [
          {
            value: [
              props.abilities.conceptUnderstanding,
              props.abilities.logicalReasoning,
              props.abilities.problemSolving,
              props.abilities.innovativeThinking,
              props.abilities.expressionNorms
            ],
            name: '能力评估',
            areaStyle: {
              color: 'rgba(85, 106, 245, 0.3)'
            },
            lineStyle: {
              color: '#556af5'
            },
            symbol: 'circle',
            symbolSize: 6
          }
        ]
      }
    ]
  })
}

watch(
  () => props.abilities,
  () => {
    nextTick(() => {
      if (chartInstance) {
        chartInstance.dispose()
      }
      initChart()
    })
  },
  { immediate: true, deep: true }
)

onMounted(() => {
  initChart()
})
</script>

<style scoped>
.radar-chart {
  width: 100%;
  height: 400px;
}
</style>
