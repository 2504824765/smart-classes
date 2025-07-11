<template>
  <el-card
    class="course-card"
    shadow="hover"
    :class="{ 'disabled-card': !course.isActive }"
    @click="handleClick"
  >
    <el-image :src="course.imageUrl ? course.imageUrl : '/default.png'" alt="课程封面" class="course-image" />
    <div class="course-info">
      <h3 class="course-title">{{ course.name }}</h3>
      <p class="course-description">{{ course.description }}</p>
      <el-tag
        size="small"
        :type="!course.isActive || course.total === 0 ? 'info' : course.unfinished === course.total ? 'success' : 'warning'"
      >
        {{ !course.isActive ? '课程已结束' : course.total === 0 ? '课程无任务' : `已完成 ${course.unfinished}/${course.total} ` }}
      </el-tag>
    </div>
  </el-card>
</template>

<script setup lang="ts">
import { CourseDisplayData } from '@/api/classes/types'
import { ElMessage } from 'element-plus'
const emit = defineEmits(['view-homework'])

const props = defineProps<{
  course: CourseDisplayData
}>()

const handleClick = () => {
  if (props.course.isActive) {
    emit('view-homework', props.course)
  } else {
    ElMessage.warning('该课程已截止')
  }
}
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
  transition: 0.3s;
}

.course-card:hover {
  transform: scale(1.01);
}

/* 封面图 */
.course-image {
  width: 100%;
  height: 150px;
  object-fit: cover;
}

/* 课程信息 */
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
  min-height: 2.8em;
}

/* 禁用状态样式 */
.disabled-card {
  cursor: not-allowed;
  opacity: 0.6;
  pointer-events: auto; /* 保留 hover 效果 */
}
</style>
