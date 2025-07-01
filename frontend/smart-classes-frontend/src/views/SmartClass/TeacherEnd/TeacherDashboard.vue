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
import { createClassMissionApi } from '@/api/mission'
import { UserType } from '@/api/login/types'
import { ElMessage } from 'element-plus'
import {
  getStudentListApi,
  deleteStudentApi,
  updateStudentApi,
  createStudentApi,
  getStudentByUsernameApi,
  getStudentByIdApi
} from '@/api/student'
import { getTeacherByUsernameApi } from '@/api/teacher'
import { Icon } from '@/components/Icon'

interface Params {
  pageIndex?: number
  pageSize?: number
}

interface Course {
  id: number
  name: string
  time: string
  classroom: string
}

interface Statistics {
  studentCount: number
  courseCount: number
  assignmentCount: number
  completionRate: number
}

const router = useRouter()
const { t } = useI18n()

// 教师信息
const teacherInfo = ref({
  name: '',
  username: '',
  avatar: ''
})

// 近期课程
const recentCourses = ref<Course[]>([])
const loading = ref(false)
const statistics = ref<Statistics>({
  studentCount: 0,
  courseCount: 0,
  assignmentCount: 0,
  completionRate: 0
})

// // 获取当前登录教师信息
// const getCurrentTeacher = async () => {
//   try {
//     const currentUsername = localStorage.getItem('username') || sessionStorage.getItem('username')
//     if (currentUsername) {
//       const res = await getTeacherByUsernameApi(currentUsername)
//       if (res && res.data) {
//         teacherInfo.value = res.data
//         // 获取教师统计数据
//         await getTeacherStatistics()
//         // 获取近期课程
//         await getRecentCourses()
//       }
//     }
//   } catch (error) {
//     console.error('获取教师信息失败:', error)
//     ElMessage.error('获取教师信息失败')
//   }
// }

// // 获取教师统计数据
// const getTeacherStatistics = async () => {
//   try {
//     loading.value = true
//     const res = await getTeacherStatisticsApi(teacherInfo.value.username)
//     if (res && res.data) {
//       statistics.value = res.data
//     }
//   } catch (error) {
//     console.error('获取统计数据失败:', error)
//     ElMessage.error('获取统计数据失败')
//   } finally {
//     loading.value = false
//   }
// }

// // 获取近期课程
// const getRecentCourses = async () => {
//   try {
//     loading.value = true
//     const res = await getRecentCoursesApi(teacherInfo.value.username)
//     if (res && res.data) {
//       recentCourses.value = res.data
//     }
//   } catch (error) {
//     console.error('获取近期课程失败:', error)
//     ElMessage.error('获取近期课程失败')
//   } finally {
//     loading.value = false
//   }
// }

// 跳转到课程管理
const gotoCourseManagement = () => {
  router.push('/course/manage')
}

// 跳转到学生管理
const gotoStudentManagement = () => {
  router.push('/student/manage')
}

// 跳转到作业管理
const gotoAssignmentManagement = () => {
  router.push('/assignment/manage')
}

// onMounted(() => {
//   getCurrentTeacher()
// })
</script>

<template>
  <ContentWrap>
    <!-- 欢迎区域 -->
    <div class="welcome-section">
      <div style="display: flex; align-items: center; gap: 16px; margin-bottom: 16px">
        <span style="font-size: 18px; font-weight: bold">{{ t('teacher.dashboard') }}</span>
        <el-text class="mx-1" size="large">欢迎，{{ teacherInfo.name || '老师' }}</el-text>
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
            <el-statistic title="学生人数" :value="statistics.studentCount" />
            <div class="card-footer">
              <el-button type="text" @click="gotoStudentManagement">查看详情</el-button>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card stat-card-2">
            <el-statistic title="课程数量" :value="statistics.courseCount" />
            <div class="card-footer">
              <el-button type="text" @click="gotoCourseManagement">查看详情</el-button>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card stat-card-3">
            <el-statistic title="作业数量" :value="statistics.assignmentCount" />
            <div class="card-footer">
              <el-button type="text" @click="gotoAssignmentManagement">查看详情</el-button>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card stat-card-4">
            <el-statistic title="作业完成率" :value="statistics.completionRate" />
            <div class="card-footer">
              <el-button type="text" @click="gotoStudentManagement">查看详情</el-button>
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
        <el-table-column prop="name" label="课程名称" width="180" />
        <el-table-column label="上课时间">
          <template #default="{ row }">
            <div style="display: flex; align-items: center">
              <el-icon><clock /></el-icon>
              <span style="margin-left: 8px">{{ row.time }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="classroom" label="教室" />
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="router.push(`/course/detail/${row.id}`)">
              详情
            </el-button>
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
            @click="router.push('/teacher/studentManage')"
          >
            <div class="quick-action-item">
              <el-icon size="24px"><calendar /></el-icon>
              <div class="action-text">
                <span>学生管理</span>
                <Icon icon="ep:arrow-right" class="arrow-icon" />
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card
            shadow="hover"
            class="quick-card quick-card-2"
            @click="router.push('/teacher/Mission')"
          >
            <div class="quick-action-item">
              <el-icon size="24px"><document /></el-icon>
              <div class="action-text">
                <span>任务管理</span>
                <Icon icon="ep:arrow-right" class="arrow-icon" />
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card
            shadow="hover"
            class="quick-card quick-card-3"
            @click="router.push('/grade/manage')"
          >
            <div class="quick-action-item">
              <el-icon size="24px"><trophy /></el-icon>
              <div class="action-text">
                <span>课程管理</span>
                <Icon icon="ep:arrow-right" class="arrow-icon" />
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card
            shadow="hover"
            class="quick-card quick-card-4"
            @click="router.push('/message/send')"
          >
            <div class="quick-action-item">
              <el-icon size="24px"><message /></el-icon>
              <div class="action-text">
                <span>成绩管理</span>
                <Icon icon="ep:arrow-right" class="arrow-icon" />
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
  margin-top: 12px;
  text-align: right;
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
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  cursor: pointer;
  height: 100px;
  color: white;
  font-weight: bold;
}

.quick-action-item span {
  font-size: 16px;
  font-weight: bold;
  color: white;
}

/* 主图标样式 */
.quick-action-item .el-icon {
  color: white;
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
  color: white;
  transition: transform 0.3s ease;
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
}

.stat-card:hover {
  transform: translateY(-5px);
}

.stat-card-1 {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.stat-card-2 {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: white;
}

.stat-card-3 {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  color: white;
}

.stat-card-4 {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
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
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.quick-card-1 {
  background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%);
}

.quick-card-2 {
  background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);
}

.quick-card-3 {
  background: linear-gradient(135deg, #f5cbd2 0%, #d299c2 100%);
}

.quick-card-4 {
  background: linear-gradient(135deg, #89f7fe 0%, #66a6ff 100%);
}

/* 调整统计数据显示颜色 */
.stat-card .el-statistic__content {
  color: white !important;
}

.stat-card .el-statistic__head {
  color: white !important;
}

.stat-card .el-statistic__title {
  color: white !important;
}

.stat-card .card-footer .el-button {
  color: white !important;
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
</style>
