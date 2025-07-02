<template>
  <el-card
    class="mission-card"
    shadow="hover"
    @click="goToDetail"
    :class="{ 'is-disabled': isExpired }"
  >
    <el-text tag="h4">{{ mission.class.name }}</el-text>
    <el-text class="mission-desc" size="small">{{ mission.description }}</el-text>
    <el-text type="info" size="small">截止时间：{{ mission.deadline }}</el-text>
    <el-tag
      :type="isExpired ? 'danger' : 'success'"
      class="status"
      effect="light"
    >
      {{ isExpired ? '已截止' : '进行中' }}
    </el-tag>
  </el-card>
</template>

<script setup lang="ts">
import { defineProps, computed } from 'vue'
import { useRouter } from 'vue-router'
import type { ClassMission } from '@/api/classMission/types'

const props = defineProps<{ mission: ClassMission }>()

const { push } = useRouter()

const isExpired = computed(() => new Date(props.mission.deadline) < new Date())

const goToDetail = () => {
  push({ path: '/mission/detail', query: { id: props.mission.id } })
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
  margin: 8px 0;
  color: #666;
}
.status {
  margin-top: 8px;
  display: block;
}
.mission-card.is-disabled {
  opacity: 0.6;
}
</style>
