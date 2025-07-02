<template>
  <el-card shadow="never" class="mission-list">
    <ElButton
      type="primary"
      style="margin-bottom: 10px"
      @click="push({ path: '/course/detail/form' })"
      >添加任务</ElButton
    >
    <draggable v-model="missions" item-key="id" animation="200">
      <template #item="{ element }">
        <MissionCard :mission="element" />
      </template>
    </draggable>
  </el-card>
  <el-card>
    <KnowledgeGraph v-if="classId !== undefined" :classId="classId" />
  </el-card>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import draggable from 'vuedraggable'
import MissionCard from './components/MissionCard.vue'
import { getClassMissionByCidApi } from '@/api/classMission/index'
import type { ClassMission } from '@/api/classMission/types'
import { useRouter, useRoute } from 'vue-router'
import KnowledgeGraph from './components/KnowledgeGraph.vue'

const route = useRoute()
const { push } = useRouter()
const classId = ref<number>()

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
