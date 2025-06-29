<template>
  <div class="course-list">
    <CourseCard
      v-for="item in courseList"
      :key="item.id"
      :course="item"
    />
  </div>
</template>

<script setup lang="ts">
import CourseCard from './components/CourseCard.vue'

const courseList = [
  {
    id: 1,
    name: 'JavaScript 基础',
    image: '',
    description: '学习 JS 的基础语法和逻辑结构',
    open: true
  },
  {
    id: 2,
    name: 'Vue3 框架精讲',
    image: '',
    description: '深入理解 Vue3 的响应式原理与实践',
    open: false
  }
]
</script>

<style scoped>
.course-list {
  display: flex;
  flex-wrap: wrap;
  gap: 24px;
}
</style>