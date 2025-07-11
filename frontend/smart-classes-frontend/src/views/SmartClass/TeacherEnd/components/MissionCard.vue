<template>
  <el-card
    class="mission-card"
    shadow="hover"
    @click="goToDetail"
    :class="{ 'is-disabled': isExpired }"
  >
    <el-text tag="h4" class="font-bold">任务 {{ mission.id }}</el-text>

    <el-text class="mission-desc" size="small">
      {{ mission.description || '暂无任务描述' }}
    </el-text>

    <el-text type="info" size="small" class="mission-deadline">
      截止时间：{{ mission.deadline || '未设置' }}
    </el-text>

    <el-tag :type="isExpired ? 'danger' : 'success'" class="status" effect="light">
      {{ isExpired ? '已截止' : '进行中' }}
    </el-tag>
  </el-card>
</template>

<script setup lang="ts">
import { defineProps, computed } from 'vue'
import { useRouter } from 'vue-router'
import type { ClassMission } from '@/api/classMission/types'

const props = defineProps<{ mission: ClassMission }>()
console.log('接收到的 mission：', props.mission)
const { push } = useRouter()

const isExpired = computed(() => new Date(props.mission.deadline) < new Date())

const goToDetail = () => {
  push({ path: '/course/detail/mission', query: { id: props.mission.id } })
}
</script>

<style scoped>
.mission-card {
  margin-bottom: 16px;
  cursor: pointer;
  transition: transform 0.2s ease;
}

.mission-card:hover {
  transform: translateY(-3px);
}

.mission-desc {
  margin-top: 8px;
  color: #666;
  display: block;
}

.mission-deadline {
  display: block;
  margin-top: 4px;
  color: #999;
}

.status {
  margin-top: 10px;
  display: block;
}

.mission-card.is-disabled {
  opacity: 0.6;
}
</style>
