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
import { ref, onMounted } from 'vue'
import draggable from 'vuedraggable'
import MissionCard from './components/MissionCard.vue'
import { getClassMissionByCidApi } from '@/api/classMission/index'
import type { ClassMission } from '@/api/classMission/types'
import { useRoute } from 'vue-router'

const route = useRoute()
const classId = ref<number | null>(null)

const missions = ref<ClassMission[]>([])

const fetchMissions = async () => {
  if (!route.query.classId) return

  classId.value = Number(route.query.classId)
  const res = await getClassMissionByCidApi(classId.value)
  missions.value = res.data
}

onMounted(fetchMissions)
</script>

<style scoped>
.mission-list {
  padding: 16px;
}
</style>