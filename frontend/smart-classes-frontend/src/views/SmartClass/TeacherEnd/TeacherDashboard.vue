<script setup lang="tsx">
import { ContentWrap } from '@/components/ContentWrap'
import { useI18n } from '@/hooks/web/useI18n'
import { Table, TableColumn } from '@/components/Table'
import { getTableListApi } from '@/api/table'
import { TableData } from '@/api/table/types'
import { ref, h, reactive, onMounted } from 'vue'
import { ElTag, ElCard, ElStatistic, ElProgress } from 'element-plus'
import { BaseButton } from '@/components/Button'
import { useRouter } from 'vue-router'
import { UserType } from '@/api/login/types'
import { ElMessage } from 'element-plus'
import {
  getStudentListApi,
  deleteStudentApi,
  updateStudentApi,
  createStudentApi,
  getStudentByUsernameApi,
  getStudentByIdApi,
  getStudentCountApi
} from '@/api/student'
import { getTeacherByUsernameApi } from '@/api/teacher'
import { Icon } from '@/components/Icon'
import { useUserStore } from '@/store/modules/user'
import { Teacher } from '@/api/teacher/types'
import { getAllClassesApi } from '@/api/classes'
import { Classes } from '@/api/classes/types'
import { getClassMissionByCidApi } from '@/api/classMission'


// 教师信息
const teacherInfo = ref<Teacher>()

const getId = async (username: string) => {
  const res = await getTeacherByUsernameApi(username)
  teacherInfo.value = res.data
}

const userStore = useUserStore()
const loginInfo = userStore.getLoginInfo
const initialize = async () => {
  if (loginInfo) {
    const username = loginInfo.username
    await getId(username)
  }
}

interface Statistics {
  studentCount: number
  courseCount: number
  assignmentCount: number
  completionRate: number
}

const router = useRouter()
const { t } = useI18n()

// 近期课程
const recentCourses = ref<Classes[]>([])
const loading = ref(false)
const statistics = ref<Statistics>({
  studentCount: 0,
  courseCount: 0,
  assignmentCount: 0,
  completionRate: 0
})

// 获取当前登录教师信息
const getCurrentTeacher = async () => {
  try {

    // 设置统计数据
    statistics.value = {
      studentCount: 0,
      courseCount: 0,
      assignmentCount: 0,
      completionRate: 0
    }

  } catch (error) {
    console.error('初始化失败:', error)
  }
}

// 获取教师统计数据
const getTeacherStatistics = async () => {
  if(!teacherInfo.value) {
    ElMessage.error('教师信息为空')
    return
  }

  try {
    loading.value = true

    // 获取学生数量
    try {
      const studentCountRes = await getStudentCountApi()
      console.log('学生数量API响应:', studentCountRes)
      if (studentCountRes && studentCountRes.data !== undefined) {
        statistics.value.studentCount = Number(studentCountRes.data)
        console.log('成功获取学生数量:', studentCountRes.data)
      } else {
        statistics.value.studentCount = 0
        console.warn('API响应格式异常，学生数量设为0')
      }
    } catch (error) {
      console.error('获取学生数量失败:', error)
      statistics.value.studentCount = 0
    }

    try {
      // 获取课程数量
      const res = await getAllClassesApi()
      const teacherId = teacherInfo.value.id
      const courseList = res.data.filter((course: Classes) => course.teacher.id === teacherId)
      recentCourses.value = courseList
      console.log('课程数量API响应:', res)
      if (res && courseList) {
        statistics.value.courseCount = Number(courseList.length)
        console.log('成功获取课程数量:', courseList.length)
      }
      
    } catch (error) {
      console.error('获取课程数量失败:', error)
      statistics.value.courseCount = 0
    }

    try {
      // 获取作业数量
      const res = await getAllClassesApi()
      const classes = res.data  // 假设这里是课程数组

      let totalMissions = 0

      await Promise.all(
        classes.map(async (cls) => {
          const missionRes = await getClassMissionByCidApi(cls.id)
          totalMissions += missionRes.data.length
        })
      )

      console.log('所有课程任务总数', totalMissions)
      statistics.value.assignmentCount = totalMissions
    } catch (error) {
      console.error('获取作业数量失败:', error)
      statistics.value.assignmentCount = 0
    }

    try {
      // 获取完成率
      const res = await getAllClassesApi()
      const classes = res.data 

      const startedMissionCounts: Record<number, number> = {}

      await Promise.all(
        classes.map(async (cls) => {
          const missionRes = await getClassMissionByCidApi(cls.id)
          const missions = missionRes.data

          const startedMissions = missions.filter(mission => mission.deadline && new Date(mission.deadline) < new Date)

          startedMissionCounts[cls.id] = startedMissions.length
        })
      )

      console.log('每个课程已开始任务数量', startedMissionCounts)
      statistics.value.completionRate = Object.values(startedMissionCounts).reduce((acc, count) => acc + count, 0) / (classes.length * 2) * 100
    } catch (error) {
      console.error('获取完成率失败:', error)
      statistics.value.completionRate = 0
    }

  } catch (error) {
    console.error('获取统计数据失败:', error)
    statistics.value = {
      studentCount: 0,
      courseCount: 0,
      assignmentCount: 0,
      completionRate: 0
    }
  } finally {
    loading.value = false
  }
}
// 快捷跳转
const gotoCourseManagement = () => {
  router.push('/course/content')
}

const gotoStudentManagement = () => {
  router.push('/course/content')
}

const gotoAssignmentManagement = () => {
  router.push('/course/content')
}

const gotoMissionManagement = () => {
  router.push('/course/content')
}

onMounted(() => {
  initialize()
  setTimeout(() => {
    getCurrentTeacher().catch((error) => {
      console.error('初始化失败:', error)
    })
    getTeacherStatistics().catch((error) => {
      console.error('获取统计数据失败:', error)
    })
  }, 100)
})
</script>

<template>
  <ContentWrap>
    <!-- 欢迎区域 -->
    <div class="welcome-section">
      <div style="display: flex; align-items: center; gap: 16px; margin-bottom: 16px">
        <span style="font-size: 18px; font-weight: bold">{{ t('teacher.dashboard') }}</span>
        <el-text class="mx-1" size="large">欢迎，{{ teacherInfo?.name || '老师' }}</el-text>
      </div>
      <el-divider />
    </div>

    <!-- 数据概览 -->
    <div class="statistics-section">
      <h3>教学数据概览</h3>
      <div style="margin: 20px 0"></div>
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card stat-card-1">
            <el-statistic
              :value="statistics.studentCount"
              :value-style="{ fontSize: '32px', fontWeight: 'bold', color: 'white' }"
            >
              <template #title>
                <span style="font-size: 16px; font-weight: 600; color: white">学生人数</span>
              </template>
            </el-statistic>
            <div class="card-footer">
              <el-button type="text" plain @click="gotoStudentManagement">查看详情</el-button>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card stat-card-2">
            <el-statistic
              :value="statistics.courseCount"
              :value-style="{ fontSize: '32px', fontWeight: 'bold', color: 'white' }"
            >
              <template #title>
                <span style="font-size: 16px; font-weight: 600; color: white">课程数量</span>
              </template>
            </el-statistic>
            <div class="card-footer">
              <el-button type="text" plain @click="gotoCourseManagement">查看详情</el-button>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card stat-card-3">
            <el-statistic
              :value="statistics.assignmentCount"
              :value-style="{ fontSize: '32px', fontWeight: 'bold', color: 'white' }"
            >
              <template #title>
                <span style="font-size: 16px; font-weight: 600; color: white">任务数量</span>
              </template>
            </el-statistic>
            <div class="card-footer">
              <el-button type="text" plain @click="gotoMissionManagement">查看详情</el-button>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card stat-card-4">
          <el-statistic
            :value="statistics.completionRate"
            :value-style="{ fontSize: '32px', fontWeight: 'bold', color: 'white' }"
            :formatter="value => Number(value).toFixed(2) + '%'"
          >
            <template #title>
              <span style="font-size: 16px; font-weight: 600; color: white">作业完成率</span>
            </template>
          </el-statistic>
            <div class="card-footer">
              <el-button type="text" plain @click="gotoStudentManagement">查看详情</el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 近期课程 -->
    <div class="recent-courses">
      <h3>近期课程</h3>
      <div style="margin: 20px 0"></div>
      <el-table :data="recentCourses" v-loading="loading" style="width: 100%">
        <el-table-column prop="name" label="课程名称" min-width="280" align="center" />
        <el-table-column label="课时" width="180" align="center">
          <template #default="{ row }">
            <div style="display: flex; align-items: center; justify-content: center">
              <Icon icon="ep:clock" />
              <span style="margin-left: 8px">{{ row.classHours }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" width="150" align="center" />
        <el-table-column label="状态" width="160" align="center">
          <template #default="{ row }">
            <el-tag :type="row.isActive ? 'success' : 'danger'">
              {{ row.isActive ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 快捷操作 -->
    <div class="quick-actions">
      <h3>快捷操作</h3>
      <div style="margin: 20px 0"></div>
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card
            shadow="hover"
            class="quick-card quick-card-1"
            @click="router.push('/studentManage/list')"
          >
            <div class="quick-action-item">
              <Icon icon="ep:connection" :size="24" />
              <div class="action-text">
                <span>学生管理</span>
                <Icon icon="ep:d-arrow-right" class="arrow-icon" />
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card
            shadow="hover"
            class="quick-card quick-card-2"
            @click="router.push('/course/content')"
          >
            <div class="quick-action-item">
              <Icon icon="ep:edit-pen" :size="24" />
              <div class="action-text">
                <span>发布任务</span>
                <Icon icon="ep:d-arrow-right" class="arrow-icon" />
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card
            shadow="hover"
            class="quick-card quick-card-3"
            @click="router.push('/course/content')"
          >
            <div class="quick-action-item">
              <Icon icon="ep:upload" :size="24" />
              <div class="action-text">
                <span>上传资料</span>
                <Icon icon="ep:d-arrow-right" class="arrow-icon" />
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card
            shadow="hover"
            class="quick-card quick-card-4"
            @click="router.push('/course/content')"
          >
            <div class="quick-action-item">
              <Icon icon="ep:trophy" :size="24" />
              <div class="action-text">
                <span>成绩管理</span>
                <Icon icon="ep:d-arrow-right" class="arrow-icon" />
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </ContentWrap>
</template>

<style scoped>
.welcome-section {
  margin-bottom: 24px;
}

.statistics-section {
  margin-bottom: 32px;
}

.recent-courses {
  margin-bottom: 32px;
}

.quick-actions {
  margin-bottom: 24px;
}

.card-footer {
  margin-top: 6px;
  text-align: right;
  flex-shrink: 0;
}

.card-footer .el-button {
  font-size: 12px !important;
  padding: 4px 8px !important;
  height: 24px !important;
}

.completion-rate {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-title {
  margin-bottom: 16px;
  font-size: 14px;
  color: var(--el-text-color-secondary);
}

.quick-action-item {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;
  gap: 8px;
  cursor: pointer;
  height: 60px;
  color: #333;
  font-weight: bold;
  padding: 12px 16px;
}

.quick-action-item span {
  font-size: 14px;
  font-weight: bold;
  color: #333;
}

/* 主图标样式 */
.quick-action-item .el-icon {
  color: #666;
  font-size: 20px;
}

/* 文字和箭头容器 */
.action-text {
  display: flex;
  align-items: center;
  gap: 6px;
}

/* 箭头图标样式 */
.arrow-icon {
  font-size: 14px;
  font-weight: bold;
  transition: transform 0.3s ease;
}

.quick-card-1 .arrow-icon {
  color: #f7797d;
}

.quick-card-2 .arrow-icon {
  color: #556af5;
}

.quick-card-3 .arrow-icon {
  color: #68d1c8;
}

.quick-card-4 .arrow-icon {
  color: #a38aff;
}

/* 悬停时箭头动画 */
.quick-card:hover .arrow-icon {
  transform: translateX(3px);
}

/* 统计卡片彩色样式 */
.stat-card {
  position: relative;
  overflow: hidden;
  transition: transform 0.3s ease;
  height: 140px;
}

.stat-card:hover {
  transform: translateY(-3px);
}

.stat-card .el-card__body {
  padding: 12px 16px !important;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.stat-card-1 {
  background: linear-gradient(45deg, #ffbfa3 0%, #f7797d 100%);
  color: white;
}

.stat-card-2 {
  background: linear-gradient(45deg, #a3cbff 0%, #556af5 100%);
  color: white;
}

.stat-card-3 {
  background: linear-gradient(45deg, #b2ffd5 0%, #68d1c8 100%);
  color: white;
}

.stat-card-4 {
  background: linear-gradient(45deg, #eba3ff 0%, #a38aff 100%);
  color: white;
}

/* 快捷操作卡片彩色样式 */
.quick-card {
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
  cursor: pointer;
}

.quick-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
}

.quick-card .el-card__body {
  padding: 0 !important;
}

.quick-card-1 {
  background: rgba(255, 216, 205, 0.1);
  border: 2px solid #f7797d;
}

.quick-card-2 {
  background: rgba(198, 206, 255, 0.1);
  border: 2px solid #556af5;
}

.quick-card-3 {
  background: rgba(199, 255, 220, 0.1);
  border: 2px solid #68d1c8;
}

.quick-card-4 {
  background: rgba(251, 210, 255, 0.1);
  border: 2px solid #a38aff;
}

/* 调整统计数据显示颜色和字体大小 */
.stat-card .el-statistic__content {
  color: white !important;
  font-size: 42px !important;
  font-weight: bold !important;
}

.stat-card .el-statistic__content .el-statistic__number {
  font-size: 42px !important;
  font-weight: bold !important;
  color: white !important;
}

.stat-card .el-statistic__head {
  color: white !important;
}

.stat-card .el-statistic__title {
  color: white !important;
  font-size: 24px !important;
  font-weight: 600 !important;
}

.stat-card .el-statistic .el-statistic__head .el-statistic__title {
  font-size: 24px !important;
  font-weight: 600 !important;
  color: white !important;
}

.stat-card .card-footer .el-button {
  color: white !important;
  font-size: 16px !important;
  font-weight: 500 !important;
}

.stat-card .card-footer .el-button:hover {
  background-color: rgba(255, 255, 255, 0.1) !important;
}

.stat-card .el-statistic {
  color: white !important;
}

.stat-card .el-statistic * {
  color: white !important;
}

/* 更精确的选择器确保标题和数字都变大 */
.stat-card .el-statistic {
  text-align: center;
}

.stat-card .el-statistic .el-statistic__title,
.stat-card .el-statistic__title,
.stat-card [class*='el-statistic__title'] {
  font-size: 24px !important;
  font-weight: 600 !important;
  color: white !important;
  margin-bottom: 10px !important;
}

.stat-card .el-statistic .el-statistic__number,
.stat-card .el-statistic__number,
.stat-card [class*='el-statistic__number'] {
  font-size: 42px !important;
  font-weight: bold !important;
  color: white !important;
}

.stat-card .el-statistic .el-statistic__content,
.stat-card [class*='el-statistic__content'] {
  font-size: 42px !important;
  font-weight: bold !important;
  color: white !important;
}
</style>
