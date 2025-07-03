<script setup lang="tsx">
import { ContentWrap } from '@/components/ContentWrap'
import { useI18n } from '@/hooks/web/useI18n'
import { ref, reactive, onMounted } from 'vue'
import { ElCard, ElStatistic, ElProgress, ElMessage } from 'element-plus'
import { BaseButton } from '@/components/Button'
import { useRouter } from 'vue-router'
import { getUserByIdApi } from '@/api/login'
import { useUserStore } from '@/store/modules/user'
import { Icon } from '@/components/Icon'
import { getStudentMissionByClass } from '@/api/studentMission/index'
import { getStudentByUsernameApi } from '@/api/student/index'
import { getAssociatedBySidApi } from '@/api/studentClasses/index'
import type { StudentMission } from '@/api/studentMission/types'

interface Statistics {
  courseCount: number
  homeworkCount: number
  completedHomework: number
  completionRate: number
}

const router = useRouter()
const { t } = useI18n()
const userStore = useUserStore()

// 学生信息
const studentInfo = ref({
  name: '',
  username: '',
  avatar: ''
})

// 近期作业
const recentHomeworks = ref<StudentMission[]>([])
const studentId = ref<number | null>(null)
const loading = ref(false)
const statistics = ref<Statistics>({
  courseCount: 0,
  homeworkCount: 0,
  completedHomework: 0,
  completionRate: 0
})

// 获取当前登录学生信息
const getCurrentStudent = async () => {
  try {
    const currentUser = userStore.getUserInfo
    console.log('当前用户信息:', currentUser)

    if (currentUser?.roleId) {
      console.log('准备调用API获取用户详情, ID:', currentUser.roleId)
      // 调用后端API获取用户信息
      const userRes = await getUserByIdApi(currentUser.roleId)
      console.log('API响应:', userRes)

      if (userRes?.data) {
        console.log('成功获取用户数据:', userRes.data)
        studentInfo.value = {
          name: userRes.data.username, // 使用用户名作为显示名称
          username: userRes.data.username,
          avatar: userRes.data.imageURL || ''
        }
      } else {
        console.log('API调用失败，使用当前用户信息')
        // 如果API调用失败，使用当前用户信息
        studentInfo.value = {
          name: currentUser.username || '同学',
          username: currentUser.username || 'student001',
          avatar: ''
        }
      }
    } else {
      console.log('没有找到用户信息或roleId，使用默认数据')
      // 如果没有用户信息，使用默认数据
      studentInfo.value = {
        name: '张同学',
        username: 'student001',
        avatar: ''
      }
    }

    console.log('最终学生信息:', studentInfo.value)

    // 获取真实统计数据
    await getStatisticsData()

    // 获取学生作业数据
    await getRecentHomeworks()
  } catch (error) {
    console.error('初始化失败:', error)
    // 设置默认数据
    studentInfo.value = {
      name: '张同学',
      username: 'student001',
      avatar: ''
    }
  }
}

// 获取学生ID
const getStudentId = async (username: string) => {
  try {
    const res = await getStudentByUsernameApi(username)
    studentId.value = res.data.id
  } catch (error) {
    console.error('获取学生ID失败:', error)
  }
}

// 获取统计数据
const getStatisticsData = async () => {
  try {
    if (!studentId.value) {
      const loginInfo = userStore.getLoginInfo
      if (loginInfo?.username) {
        await getStudentId(loginInfo.username)
      }
    }

    if (studentId.value) {
      // 获取课程数量
      const coursesRes = await getAssociatedBySidApi(studentId.value)
      const courseCount = coursesRes?.data?.length || 0

      // 获取所有作业数据来计算统计信息
      let allHomeworks: StudentMission[] = []
      if (coursesRes?.data) {
        // 遍历所有课程，获取每个课程的作业
        for (const course of coursesRes.data) {
          try {
            const homeworkRes = await getStudentMissionByClass(course.classes.id, studentId.value)
            if (homeworkRes?.data) {
              allHomeworks = [...allHomeworks, ...homeworkRes.data]
            }
          } catch (error) {
            console.warn(`获取课程${course.classes.id}的作业失败:`, error)
          }
        }
      }

      const homeworkCount = allHomeworks.length
      const completedHomework = allHomeworks.filter((hw) => hw.done === true).length
      const completionRate =
        homeworkCount > 0 ? Math.round((completedHomework / homeworkCount) * 100) : 0

      statistics.value = {
        courseCount,
        homeworkCount,
        completedHomework,
        completionRate
      }

      console.log('统计数据:', statistics.value)
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
    // 设置默认值
    statistics.value = {
      courseCount: 0,
      homeworkCount: 0,
      completedHomework: 0,
      completionRate: 0
    }
  }
}

// 获取近期作业
const getRecentHomeworks = async () => {
  try {
    if (!studentId.value) {
      const loginInfo = userStore.getLoginInfo
      if (loginInfo?.username) {
        await getStudentId(loginInfo.username)
      }
    }

    if (studentId.value) {
      // 获取学生的所有课程
      const coursesRes = await getAssociatedBySidApi(studentId.value)
      let allHomeworks: StudentMission[] = []

      if (coursesRes?.data) {
        // 遍历所有课程，获取每个课程的作业
        for (const course of coursesRes.data) {
          try {
            const homeworkRes = await getStudentMissionByClass(course.classes.id, studentId.value)
            if (homeworkRes?.data) {
              allHomeworks = [...allHomeworks, ...homeworkRes.data]
            }
          } catch (error) {
            console.warn(`获取课程${course.classes.id}的作业失败:`, error)
          }
        }
      }

      // 按截止时间排序，显示最近的3个作业
      allHomeworks.sort((a, b) => {
        const dateA = new Date(a.classMission.deadline).getTime()
        const dateB = new Date(b.classMission.deadline).getTime()
        return dateA - dateB // 升序，最近截止的在前
      })

      recentHomeworks.value = allHomeworks.slice(0, 3)
    }
  } catch (error) {
    console.error('获取作业数据失败:', error)
    recentHomeworks.value = []
  }
}

// 快捷跳转
const gotoHomeworkDetail = (homework: StudentMission) => {
  router.push({
    name: 'HomeworkDetail',
    query: {
      missionId: homework.classMission.id,
      studentMissionId: homework.id
    }
  })
}

const gotoHomeworkList = () => {
  router.push('/student/homework')
}

const gotoHomework = () => {
  router.push('/student/homework')
}

const gotoGrades = () => {
  router.push('/student/grades')
}

const gotoKnowledgeStudy = () => {
  router.push('/student/knowledgeStudy')
}

onMounted(() => {
  setTimeout(() => {
    getCurrentStudent().catch((error) => {
      console.error('初始化失败:', error)
    })
  }, 100)
})
</script>

<template>
  <ContentWrap>
    <!-- 欢迎区域 -->
    <div class="welcome-section">
      <div style="display: flex; align-items: center; gap: 16px; margin-bottom: 16px">
        <span style="font-size: 18px; font-weight: bold">学生工作台</span>
        <el-text class="mx-1" size="large">欢迎，{{ studentInfo.name || '同学' }}</el-text>
      </div>
      <el-divider />
    </div>

    <!-- 数据概览 -->
    <div class="statistics-section">
      <h3>学习数据概览</h3>
      <div style="margin: 20px 0"></div>
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card stat-card-1">
            <el-statistic
              :value="statistics.courseCount"
              :value-style="{ fontSize: '32px', fontWeight: 'bold', color: 'white' }"
            >
              <template #title>
                <span style="font-size: 16px; font-weight: 600; color: white">已选课程</span>
              </template>
            </el-statistic>
            <div class="card-footer">
              <el-button type="text" plain @click="gotoHomeworkList">查看详情</el-button>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card stat-card-2">
            <el-statistic
              :value="statistics.homeworkCount"
              :value-style="{ fontSize: '32px', fontWeight: 'bold', color: 'white' }"
            >
              <template #title>
                <span style="font-size: 16px; font-weight: 600; color: white">总作业数</span>
              </template>
            </el-statistic>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card stat-card-3">
            <el-statistic
              :value="statistics.completedHomework"
              :value-style="{ fontSize: '32px', fontWeight: 'bold', color: 'white' }"
            >
              <template #title>
                <span style="font-size: 16px; font-weight: 600; color: white">已完成作业</span>
              </template>
            </el-statistic>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 近期作业 -->
    <div class="homeworks-section" style="margin-top: 30px">
      <h3>近期作业</h3>
      <div style="margin: 20px 0"></div>
      <el-row :gutter="20" v-if="recentHomeworks.length > 0">
        <el-col :span="8" v-for="homework in recentHomeworks" :key="homework.id">
          <el-card shadow="hover" class="homework-card">
            <template #header>
              <div class="card-header">
                <span style="font-weight: bold; font-size: 16px">
                  {{ homework.classMission.type }}
                </span>
                <el-tag
                  :type="
                    homework.done === true
                      ? 'success'
                      : homework.active === true
                        ? 'warning'
                        : 'info'
                  "
                  size="small"
                >
                  {{
                    homework.done === true
                      ? '已完成'
                      : homework.active === true
                        ? '进行中'
                        : '未开始'
                  }}
                </el-tag>
              </div>
            </template>
            <div>
              <p style="margin-bottom: 8px; color: #666; font-size: 14px">
                {{ homework.classMission.description || '暂无描述' }}
              </p>
              <p>
                <Icon icon="ep:calendar" class="mr-1" />截止时间：{{
                  homework.classMission.deadline
                }}
              </p>
              <p>
                <Icon icon="ep:document" class="mr-1" />课程：{{
                  homework.classMission.classes?.name || '未知课程'
                }}
              </p>
            </div>
            <template #footer>
              <el-button type="primary" size="small" @click="gotoHomeworkDetail(homework)"
                >查看详情</el-button
              >
            </template>
          </el-card>
        </el-col>
      </el-row>
      <div v-else style="text-align: center; padding: 40px; color: #999">
        <Icon icon="ep:document" style="font-size: 48px; margin-bottom: 16px" />
        <p>暂无作业</p>
      </div>
    </div>
  </ContentWrap>
</template>

<style lang="less" scoped>
.welcome-section {
  margin-bottom: 20px;
}

.statistics-section {
  margin-bottom: 30px;
}

.stat-card {
  height: 140px;
  border-radius: 12px;
  position: relative;
  overflow: hidden;

  :deep(.el-card__body) {
    padding: 20px;
    height: 100%;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
  }

  .card-footer {
    margin-top: auto;
    text-align: right;

    .el-button {
      color: white;
      border-color: rgba(255, 255, 255, 0.3);

      &:hover {
        background-color: rgba(255, 255, 255, 0.1);
        border-color: rgba(255, 255, 255, 0.5);
      }
    }
  }
}

.stat-card-1 {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-card-2 {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-card-3 {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.homework-card {
  margin-bottom: 20px;
  border-radius: 8px;

  :deep(.el-card__header) {
    background-color: #f8f9fa;
    border-bottom: 1px solid #e9ecef;
  }

  :deep(.el-card__body) {
    padding: 16px;

    p {
      margin: 8px 0;
      display: flex;
      align-items: center;
      color: #666;
    }
  }
}

h3 {
  color: #333;
  font-size: 18px;
  font-weight: 600;
  margin: 0;
}
</style>
