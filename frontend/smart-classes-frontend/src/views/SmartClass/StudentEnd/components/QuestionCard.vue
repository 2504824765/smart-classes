<template>
  <el-card class="mb-4">
    <div class="mb-2">
      <strong>{{ question }}</strong>
    </div>
    <el-radio-group v-model="selected" @change="emitUpdate">
      <el-radio
        v-for="(label, key) in options"
        :key="key"
        :label="key"
        class="block mb-1"
      >
        {{ key }}. {{ label }}
      </el-radio>
    </el-radio-group>

    <div v-if="showResult && selected" class="mt-2">
      <el-text v-if="selected === answer" type="success">✅ 回答正确！</el-text>
      <el-text v-else type="danger">❌ 回答错误，正确答案是 {{ answer }}</el-text>
    </div>
  </el-card>
</template>

<script setup lang="ts">
import { defineProps, defineEmits } from 'vue'

const props = defineProps<{
  question: string
  options: Record<string, string>
  answer: string
  modelValue: string | null
  showResult: boolean
}>()

const emit = defineEmits(['update:modelValue'])

const selected = defineModel<string>('')

const emitUpdate = () => {
  emit('update:modelValue', selected.value)
}
</script>

<style scoped>
.block {
  display: block;
}
</style>
