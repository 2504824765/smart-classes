<template>
  <el-card shadow="never" class="mission-list">
    <draggable v-model="missions" item-key="id" animation="200">
      <template #item="{ element }">
        <MissionCard :mission="element" />
      </template>
    </draggable>
  </el-card>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import draggable from 'vuedraggable'
import MissionCard from './components/MissionCard.vue'
import { getClassMissionByCidApi } from '@/api/classMission/index'
import type { ClassMission } from '@/api/classMission/types'

const props = defineProps<{ classId: number }>()

const missions = ref<ClassMission[]>([])

const fetchMissions = async () => {
  const res = await getClassMissionByCidApi(props.classId)
  missions.value = res.data
}

watch(() => props.classId, fetchMissions, { immediate: true })
</script>

<style scoped>
.mission-list {
  padding: 16px;
}
</style>