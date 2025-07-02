<template>
  <el-card class="course-card" shadow="hover" @click="$emit('view-homework', course)">
    <el-image
      :src="course.imageUrl"
      alt="课程封面"
      style="width: 100%; height: 160px; object-fit: cover"
    />
    <div class="course-info">
      <h3>{{ course.name }}</h3>
      <p style="color: gray; font-size: 15px; opacity: 0.8; margin: 10px 10px">{{
        course.description
      }}</p>
      <el-tag :type="course.unfinished === course.total ? 'success' : 'warning'">
        {{ course.unfinished }}/{{ course.total }} 作业未完成
      </el-tag>
    </div>
  </el-card>
</template>

<script setup lang="ts">
import { CourseDisplayData } from '@/api/classes/types'
defineProps<{
  course: CourseDisplayData
}>()
defineEmits(['view-homework'])
</script>

<style scoped>
.course-card {
  width: 300px;
  margin: 20px;
  cursor: pointer;
}
.course-image {
  width: 100%;
  height: 150px;
  object-fit: cover;
  border-radius: 8px;
}
.course-info {
  margin-top: 10px;
}
</style>
