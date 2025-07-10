<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ContentWrap } from '@/components/ContentWrap'
import { useI18n } from '@/hooks/web/useI18n'
import { Table } from '@/components/Table'
import { getAllClassesApi } from '@/api/classes/index'
import { addClassRecordApi } from '@/api/studentClasses/index'
import { getStudentByUsernameApi } from '@/api/student/index'
import { ElMessage, ElLink, ElTag, ElDivider, ElInput, ElSwitch } from 'element-plus'
import type { Classes } from '@/api/classes/types'
import type { StudentClassesCreateDTO } from '@/api/studentClasses/types'
import { useUserStore } from '@/store/modules/user'
import { getAssociatedBySidApi } from '@/api/studentClasses/index'
const { t } = useI18n()

const studentId = ref<number | null>(null)
const getStudentId = async (username: string) => {
  const res = await getStudentByUsernameApi(username)
  studentId.value = res.data.id
}

const userStore = useUserStore()
const loginInfo = userStore.getLoginInfo
const initialize = async () => {
  if (loginInfo) {
    const username = loginInfo.username
    await getStudentId(username)
  }
}

const loading = ref(true)
const classList = ref<Classes[]>([])
const searchKeyword = ref('')
const onlyShowActive = ref(false)

const selectedCids = ref<number[]>([]) // 学生已选课程 id 列表

// 获取学生已选课程 id
const fetchSelectedCids = async () => {
  if (!studentId.value) return
  console.log(studentId.value)
  const associatedRes = await getAssociatedBySidApi(studentId.value)
  console.log(associatedRes)
  if (associatedRes.code === 200) {
    console.log('fine1')
    if (associatedRes.data) {
      selectedCids.value = associatedRes.data.map((item) => item.classes.id)
    }
  }
  console.log('fine')
}

// 获取所有课程，并排除已选课程
const getClassList = async () => {
  loading.value = true
  try {
    await fetchSelectedCids() // 先获取已选课程 id
    console.log('1s')
    const res = await getAllClassesApi()

    const allClasses: Classes[] = res.data || []
    // 排除已选课程
    classList.value = allClasses.filter((cls) => !selectedCids.value.includes(cls.id))
  } catch (e) {
    ElMessage.error('获取课程失败')
  } finally {
    loading.value = false
  }
}

// 最终展示的课程：未选过 + 搜索匹配 + 状态匹配
const filteredList = computed(() => {
  return classList.value
    .filter((cls): cls is Classes => !!cls && typeof cls.active !== 'undefined')
    .filter((cls) => {
      const matchKeyword = cls.name.includes(searchKeyword.value)
      const matchActive = onlyShowActive.value ? cls.active : true
      return matchKeyword && matchActive
    })
})

// 点击选课
const handleSelect = async (cls: Classes) => {
  if (!cls.active) {
    ElMessage.warning('该课程尚未开放')
    return
  }
  if (!studentId.value) return
  const dto: StudentClassesCreateDTO = {
    cid: cls.id,
    sid: studentId.value,
    grade: 0
  }
  try {
    await addClassRecordApi(dto)
    ElMessage.success('选课成功')
    await getClassList() // 重新加载列表，更新状态
  } catch (e) {
    ElMessage.error('选课失败，可能已选该课程')
  }
}

onMounted(async () => {
  await initialize()
  await getClassList()
})
</script>

<template>
  <ContentWrap title="课程选择">
    <div class="flex items-center mb-4 gap-4">
      <ElInput v-model="searchKeyword" placeholder="搜索课程名" clearable />
      <ElSwitch v-model="onlyShowActive" active-text="仅显示已开放课程" />
    </div>

    <Table
      v-if="!loading && filteredList.length > 0"
      :columns="[]"
      :data="filteredList"
      :loading="loading"
      custom-content
      :card-wrap-style="{
        width: '240px',
        marginBottom: '20px',
        marginRight: '20px'
      }"
    >
      <template #content="cls">
        <div class="flex cursor-pointer gap-3">
          <img
            :src="cls.imageUrl ? cls.imageUrl : 'default.png'"
            class="w-64px h-64px rounded object-cover"
            alt="课程封面"
          />
          <div class="flex flex-col">
            <div class="title">{{ cls.name }}</div>
            <div class="teacher">教师：{{ cls.teacher?.name ?? '无' }}</div>
            <div class="desc">{{ cls.description }}</div>
          </div>
        </div>
      </template>

      <template #content-footer="cls">
        <div v-if="cls" class="flex justify-between items-center">
          <ElTag
            :type="cls.active === true ? 'success' : cls.active === false ? 'warning' : 'danger'"
          >
            {{ cls.active === true ? '已开放' : cls.active === false ? '未开放' : '未知状态' }}
          </ElTag>
          <ElLink
            :underline="false"
            :class="{ 'cursor-not-allowed text-gray-400': !cls.active }"
            @click="() => handleSelect(cls)"
            >选课</ElLink
          >
        </div>
        <div v-else class="text-gray-400 text-sm">课程数据异常</div>
      </template>
    </Table>

    <el-empty v-else-if="!loading && filteredList.length === 0" description="暂无可选课程" />
  </ContentWrap>
</template>
<style scoped>
.line-clamp-2 {
  display: -webkit-box;
  -webkit-box-orient: vertical;
  overflow: hidden;
  -webkit-line-clamp: 2;
}

.title {
  display: -webkit-box;
  -webkit-box-orient: vertical;
  overflow: hidden;
  -webkit-line-clamp: 2; /* 固定两行 */
  max-width: 9rem;
  min-height: 2.8rem;
  font-weight: bold;
  font-size: 1.1rem;
  line-height: 1.3em;
  margin-bottom: 0.25em;
}

.teacher {
  white-space: nowrap; /* 一行内显示 */
  min-height: 1rem;
  max-width: 9rem;
  overflow: hidden;
  text-overflow: ellipsis;
  font-size: 0.9rem;
  color: #555;
  margin-bottom: 0.25em;
}

.desc {
  white-space: nowrap; /* 一行内显示 */
  min-height: 1rem;
  max-width: 9rem;
  overflow: hidden;
  text-overflow: ellipsis;
  font-size: 0.85rem;
  color: #888;
}
</style>
