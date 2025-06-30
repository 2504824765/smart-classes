import { createRouter, createWebHashHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import type { App } from 'vue'
import { Layout, getParentLayout } from '@/utils/routerHelper'
import { useI18n } from '@/hooks/web/useI18n'
import { NO_RESET_WHITE_LIST } from '@/constants'

const { t } = useI18n()

export const constantRouterMap: AppRouteRecordRaw[] = [
  {
    path: '/',
    component: Layout,
    redirect: '/teacher/dashboard',
    name: 'Root',
    meta: {
      hidden: true
    }
  },
  {
    path: '/redirect',
    component: Layout,
    name: 'RedirectWrap',
    children: [
      {
        path: '/redirect/:path(.*)',
        name: 'Redirect',
        component: () => import('@/views/Redirect/Redirect.vue'),
        meta: {}
      }
    ],
    meta: {
      hidden: true,
      noTagsView: true
    }
  },
  {
    path: '/login',
    component: () => import('@/views/Login/Login.vue'),
    name: 'Login',
    meta: {
      hidden: true,
      title: t('router.login'),
      noTagsView: true
    }
  },
  {
    path: '/personal',
    component: Layout,
    redirect: '/personal/personal-center',
    name: 'Personal',
    meta: {
      title: t('router.personal'),
      hidden: true,
      canTo: true
    },
    children: [
      {
        path: 'personal-center',
        component: () => import('@/views/Personal/PersonalCenter/PersonalCenter.vue'),
        name: 'PersonalCenter',
        meta: {
          title: t('router.personalCenter'),
          hidden: true,
          canTo: true
        }
      }
    ]
  },
  {
    path: '/404',
    component: () => import('@/views/Error/404.vue'),
    name: 'NoFind',
    meta: {
      hidden: true,
      title: '404',
      noTagsView: true
    }
  }
]

// 教师端路由配置
export const getTeacherRoutes = (): AppRouteRecordRaw[] => {
  return [
    {
      path: '/teacher',
      component: Layout,
      redirect: '/teacher/dashboard',
      name: 'Teacher',
      meta: {
        title: t('teacher.dashboard'),
        icon: 'vi-ant-design:dashboard-filled',
        userType: 'teacher',
        alwaysShow: true
      },
      children: [
        {
          path: 'dashboard',
          component: () => import('@/views/Dashboard/Analysis.vue'),
          name: 'TeacherDashboard',
          meta: {
            title: t('teacher.dashboard'),
            userType: 'teacher',
            noCache: true,
            affix: true
          }
        },
        //教师-学生管理
        {
          path: 'studentManage',
          component: () => import('@/views/SmartClass/TeacherEnd/StudentManage.vue'),
          name: 'TeacherStudentManage',
          meta: {
            title: t('teacher.studentManage'),
            userType: 'teacher',
            noCache: true
          }
        }
      ]
    },
    //任务
    {
      path: '/teacher/Mission',
      component: Layout,
      name: 'Mission',
      meta: {
        title: t('teacher.mission'),
        userType: 'teacher',
        noCache: true,
        affix: true
      },
      children: [
        {
          path: '',
          name: 'TeacherTask',
          component: () => import('@/views/SmartClass/TeacherEnd/Mission.vue'),
          meta: {
            title: t('teacher.task'),
            icon: 'vi-ri:function-fill',
            userType: 'teacher',
            noCache: true,
            affix: true
          }
        }
      ]
    },
    {
      path: '/teacher/CreateMission',
      component: Layout,
      name: 'CreateMission',
      meta: {
        title: t('teacher.createMission'),
        userType: 'teacher',
        noCache: true,
        affix: true,
        hidden: true
      },
      children: [
        {
          path: '',
          name: 'TeacherCreateMission',
          component: () => import('@/views/SmartClass/TeacherEnd/CreateMission.vue'),
          meta: {
            title: t('teacher.createMission'),
            userType: 'teacher',
            noCache: true,
            affix: true
          }
        }
      ]
    }
  ]
}

// 学生端路由配置
export const getStudentRoutes = (): AppRouteRecordRaw[] => {
  return [
    {
      path: '/course',
      component: Layout,
      redirect: '/course/content',
      name: 'Course',
      meta: {
        title: t('student.courseList'),
        icon: 'vi-ant-design:book-outlined',
        userType: 'student',
        alwaysShow: true
      },
      children: [
        {
          path: 'content',
          component: () => import('@/views/SmartClass/StudentEnd/Course.vue'),
          name: 'CourseContent',
          meta: {
            title: t('student.courseList'),
            userType: 'student',
            noCache: true,
            affix: true
          }
        },
        {
          path: 'detail',
          component: () => import('@/views/SmartClass/StudentEnd/CourseDetail.vue'),
          name: 'CourseDetail',
          meta: {
            title: t('student.courseDetail'),
            userType: 'student',
            noCache: true,
            hidden: true,
            canTo: true
          }
        },
        {
          path: 'knowledge',
          name: 'CourseKnowledge',
          component: () => import('@/views/SmartClass/StudentEnd/KnowledgeStudy.vue'),
          meta: {
            title: t('student.study'),
            userType: 'student',
            hidden: true,
            canTo: true,
            noCache: true
          }
        }
      ]
    },
    {
      path: '/homework',
      component: Layout,
      name: 'Homework',
      meta: {
        title: t('student.homework'),
        icon: 'vi-ant-design:file-done-outlined',
        userType: 'student',
        alwaysShow: true,
        noCache: true
      },
      children: [
        {
          path: '',
          component: () => import('@/views/SmartClass/StudentEnd/Homework.vue'),
          name: 'HomeworkContent',
          meta: {
            title: t('student.homework'),
            userType: 'student',
            noCache: true
          }
        },
        {
          path: 'list',
          component: () => import('@/views/SmartClass/StudentEnd/HomeworkList.vue'),
          name: 'HomeworkList',
          meta: {
            title: t('student.homeworkList'),
            userType: 'student',
            hidden: true,
            canTo: true,
            noCache: true
          }
        },
        {
          path: 'detail',
          component: () => import('@/views/SmartClass/StudentEnd/HomeworkDetail.vue'),
          name: 'HomeworkDetail',
          meta: {
            title: t('student.homeworkList'),
            userType: 'student',
            hidden: true,
            canTo: true,
            noCache: true
          }
        }
      ]
    },
    {
      path: '/grades',
      component: Layout,
      name: 'Grades',
      meta: {
        title: t('student.grades'),
        icon: 'vi-ant-design:bar-chart-outlined',
        userType: 'student',
        alwaysShow: true,
        noCache: true
      },
      children: [
        {
          path: '',
          component: () => import('@/views/SmartClass/StudentEnd/Grades.vue'),
          name: 'GradesContent',
          meta: {
            title: t('student.grades'),
            userType: 'student',
            noCache: true
          }
        }
      ]
    }
  ]
}

// 根据用户类型获取路由
export const getAsyncRouterMap = (userType: 'teacher' | 'student'): AppRouteRecordRaw[] => {
  if (userType === 'student') {
    return getStudentRoutes()
  } else {
    return getTeacherRoutes()
  }
}

// 保留原有的 asyncRouterMap 以兼容现有代码
export const asyncRouterMap: AppRouteRecordRaw[] = getTeacherRoutes()

const router = createRouter({
  history: createWebHashHistory(),
  strict: true,
  routes: constantRouterMap as RouteRecordRaw[],
  scrollBehavior: () => ({ left: 0, top: 0 })
})

export const resetRouter = (): void => {
  router.getRoutes().forEach((route) => {
    const { name } = route
    if (name && !NO_RESET_WHITE_LIST.includes(name as string)) {
      router.hasRoute(name) && router.removeRoute(name)
    }
  })
}

export const setupRouter = (app: App<Element>) => {
  app.use(router)
}

export default router
