<template>
  <div class="course-list">
    <CourseCard v-for="item in courseList" :key="item.id" :course="item" :disabled="!item.is_active" />
  </div>
</template>

<script setup lang="ts">
import CourseCard from './components/CourseCard.vue'
import { Classes } from '@/api/classes/classes'
import { getAllClassesApi } from '@/api/classes/index'
import { onMounted, ref } from 'vue'

const courseList = ref<Classes[]>([])

const queryCourseList = async () => {
  const res = await getAllClassesApi()
  courseList.value = res.data
  console.log(courseList)
}

onMounted(() => {
  queryCourseList()
})
</script>

<style scoped>
.course-list {
  display: flex;
  flex-wrap: wrap;
  gap: 24px;
}
</style>
