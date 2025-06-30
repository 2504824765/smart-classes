<template>
  <div class="homework-list">
    <el-page-header content="作业列表" @back="router.back" />
    <el-row>
      <el-col :span="6" v-for="hw in homeworks" :key="hw.title">
        <HomeworkCard :homework="hw" @view-detail="goToDetail(hw)" />
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import HomeworkCard from './components/HomeworkCard.vue'
import { useRoute, useRouter } from 'vue-router'

const router = useRouter()
const route = useRoute()

const homeworks = [
  {
    title: '第1章作业',
    brief: '完成课后第1~5题',
    finished: false
  },
  {
    title: '第2章作业',
    brief: '阅读并回答理解问题',
    finished: true
  }
]

function goToDetail(hw: any) {
  router.push({ name: 'HomeworkDetail', query: { title: hw.title } })
}
</script>

<style scoped>
.homework-list {
  padding: 20px;
}
</style>
