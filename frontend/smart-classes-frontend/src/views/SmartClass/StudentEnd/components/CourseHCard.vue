<template>
  <el-card class="course-card" shadow="hover" @click="$emit('view-homework', course)">
    <el-image
      :src="course.imageUrl"
      alt="课程封面"
      class="course-image"
    />
    <div class="course-info">
      <h3 class="course-title">{{ course.name }}</h3>
      <p class="course-description">
        {{ course.description }}
      </p>
      <el-tag
        size="small"
        :type="course.unfinished === course.total ? 'success' : 'warning'"
      >
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
  height: 300px;
  margin: 20px;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 0;
}

.course-image {
  width: 100%;
  height: 150px;
  object-fit: cover;
}

.course-info {
  padding: 10px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  flex-grow: 1;
}

/* 标题一行省略 */
.course-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 6px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 简介最多两行省略 */
.course-description {
  font-size: 14px;
  color: gray;
  opacity: 0.8;
  margin-bottom: 8px;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}
</style>
