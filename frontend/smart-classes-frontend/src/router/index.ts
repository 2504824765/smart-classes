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
      name: 'Teacher',
      meta: {
        title: t('teacher.dashboard'),
        icon: 'vi-ant-design:dashboard-filled',
        userType: 'teacher'
      },
      children: [
        {
          path: 'dashboard',
          component: () => import('@/views/SmartClass/TeacherEnd/TeacherDashboard.vue'),
          name: 'TeacherDashboard',
          meta: {
            title: t('teacher.dashboard'),
            userType: 'teacher',
            noCache: true,
            icon: 'vi-ant-design:dashboard-filled',
            affix: true
          }
        }
      ]
    },
    //教师-学生管理
    {
      path: '/studentManage',
      component: Layout,
      name: 'StudentManage',
      meta: {
        title: t('teacher.studentManage'),
        icon: 'vi-ant-design:user-outlined',
        userType: 'teacher',
        noCache: true,
        alwaysShow: true,
      },
      children: [
        {
          path: 'list',
          name: 'StudentManageList',
          component: () => import('@/views/SmartClass/TeacherEnd/StudentCourse.vue'),
          meta: {
            title: t('teacher.studentManage'),
            userType: 'teacher',
            noCache: true
          },
        },
        {
          path: 'detail',
          name: 'StudentManageDetail',
          component: () => import('@/views/SmartClass/TeacherEnd/StudentManage.vue'),
          meta: {
            title: t('teacher.studentManage'),
            userType: 'teacher',
            noCache: true,
            hidden: true,
            canTo: true
          }
        }
      ]
    },
    {
      path: '/course',
      component: Layout,
      redirect: '/course/content',
      name: 'Course',
      meta: {
        title: t('teacher.courseList'),
        icon: 'vi-ant-design:book-outlined',
        userType: 'teacher',
        alwaysShow: true,
        noCache: true
      },
      children: [
        {
          path: 'content',
          component: () => import('@/views/SmartClass/TeacherEnd/Course.vue'),
          name: 'CourseContent',
          meta: {
            title: t('teacher.courseList'),
            userType: 'teacher',
            noCache: true
          }
        },
        {
          path: 'detail',
          component: () => import('@/views/SmartClass/TeacherEnd/CourseDetail.vue'),
          name: 'CourseDetail',
          meta: {
            title: t('teacher.courseDetail'),
            userType: 'teacher',
            noCache: true,
            hidden: true,
            canTo: true
          },
          children: [
            {
              path: 'form',
              component: () => import('@/views/SmartClass/TeacherEnd/components/MissionForm.vue'),
              name: 'CourseDetailForm',
              meta: {
                title: t('teacher.missionCreate'),
                userType: 'teacher',
                noCache: true,
                hidden: true,
                canTo: true
              }
            },
            {
              path: 'mission',
              component: () => import('@/views/SmartClass/TeacherEnd/MissionDetail.vue'),
              name: 'CourseDetailMission',
              meta: {
                title: t('teacher.missionDetail'),
                userType: 'teacher',
                noCache: true,
                hidden: true,
                canTo: true
              }
            },
            {
              path: 'studentDetail',
              component: () => import('@/views/SmartClass/TeacherEnd/StudentMissionDetail.vue'),
              name: 'CourseDetailStudentMissionDetail',
              meta: {
                title: t('teacher.studentMissionDetail'),
                userType: 'teacher',
                noCache: true,
                hidden: true,
                canTo: true
              }
            }
          ]
        }
      ]
    }
  ]
}

// 学生端路由配置
export const getStudentRoutes = (): AppRouteRecordRaw[] => {
  return [
    {
      path: '/student',
      component: Layout,
      name: 'StudentDashboard',
      meta: {
        title: t('teacher.dashboard'),
        icon: 'vi-ant-design:book-outlined',
        userType: 'student'
      },
      children: [
        {
          path: 'dashboard',
          component: () => import('@/views/SmartClass/StudentEnd/StudentDashboard.vue'),
          name: 'StdDashboard',
          meta: {
            title: t('teacher.dashboard'),
            userType: 'student',
            noCache: true,
            affix: true
          }
        }
      ]
    },
    {
      path: '/course',
      component: Layout,
      redirect: '/course/content',
      name: 'Course',
      meta: {
        title: t('student.courseList'),
        icon: 'vi-ant-design:book-outlined',
        userType: 'student'
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
          path: 'select',
          component: () => import('@/views/SmartClass/StudentEnd/CourseSelect.vue'),
          name: 'CourseSelect',
          meta: {
            title: t('student.courseSelect'),
            userType: 'student',
            noCache: true
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
      path: '/personal',
      component: Layout,
      redirect: '/personal/personal-center',
      name: 'Personal',
      meta: {
        title: t('router.personal'),
        hidden: true,
        canTo: true,
        userType: 'student'
      },
      children: [
        {
          path: 'personal-center',
          component: () => import('@/views/Personal/PersonalCenter/PersonalCenter.vue'),
          name: 'PersonalCenter',
          meta: {
            title: t('router.personalCenter'),
            hidden: true,
            canTo: true,
            userType: 'student'
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
            title: t('student.homeworkDetail'),
            userType: 'student',
            hidden: true,
            canTo: true,
            noCache: true
          }
        }
      ]
    },
    {
      path: '/data',
      component: Layout,
      name: 'Data',
      meta: {
        title: t('student.data'),
        icon: 'vi-ant-design:bar-chart-outlined',
        userType: 'student'
      },
      children: [
        {
          path: 'grades',
          component: () => import('@/views/SmartClass/StudentEnd/Grades.vue'),
          name: 'DataGrades',
          meta: {
            title: t('student.grades'),
            userType: 'student',
            noCache: true
          }
        },
        {
          path: 'assessment',
          component: () => import('@/views/SmartClass/StudentEnd/Assessment.vue'),
          name: 'DataAssessment',
          meta: {
            title: t('student.assessment'),
            userType: 'student',
            noCache: true
          }
        }
      ]
    }
  ]
}

export const getAdminRoutes = (): AppRouteRecordRaw[] => {
  return [
    {
      path: '/admin',
      component: Layout,
      redirect: '/admin/teacherManage',
      name: 'Admin',
      meta: {
        title: t('admin.admin'),
        icon: 'vi-ant-design:setting-outlined',
        userType: 'admin',
        alwaysShow: true
      },
      children: [
        {
          path: 'teacherManage',
          component: () => import('@/views/SmartClass/AdminEnd/TeacherManage.vue'),
          name: 'AdminTeacherManage',
          meta: {
            title: t('admin.teacher'),
            userType: 'admin',
            noCache: true
          },
          children: [
            {
              path: 'form',
              component: () => import('@/views/SmartClass/AdminEnd/components/TeacherForm.vue'),
              name: 'AdminTeacherManageForm',
              meta: {
                title: t('admin.createform'),
                userType: 'admin',
                noCache: true,
                hidden: true,
                canTo: true
              }
            }
          ]
        },
        {
          path: 'studentManage',
          component: () => import('@/views/SmartClass/AdminEnd/StudentManage.vue'),
          name: 'AdminStudentManage',
          meta: {
            title: t('admin.student'),
            userType: 'admin',
            noCache: true
          },
          children: [
            {
              path: 'form',
              component: () => import('@/views/SmartClass/AdminEnd/components/StudentForm.vue'),
              name: 'AdminStudentManageForm',
              meta: {
                title: t('admin.createform'),
                userType: 'admin',
                noCache: true,
                hidden: true,
                canTo: true
              }
            }
          ]
        },
        {
          path: 'courseManage',
          component: () => import('@/views/SmartClass/AdminEnd/CourseManage.vue'),
          name: 'AdminCourseManage',
          meta: {
            title: t('admin.course'),
            userType: 'admin',
            noCache: true
          },
          children: [
            {
              path: 'form',
              component: () => import('@/views/SmartClass/AdminEnd/components/CourseForm.vue'),
              name: 'AdminCourseManageForm',
              meta: {
                title: t('admin.createform'),
                userType: 'admin',
                noCache: true,
                hidden: true,
                canTo: true
              }
            }
          ]
        },
        {
          path: 'missionManage',
          name: 'AdminMissionManage',
          component: () => import('@/views/SmartClass/AdminEnd/MissionManage.vue'),
          meta: {
            title: t('admin.mission'),
            userType: 'admin',
            noCache: true
          },
          children: [
            {
              path: 'form',
              name: 'MissionCreate',
              component: () => import('@/views/SmartClass/AdminEnd/components/MissionForm.vue'),
              meta: {
                title: t('admin.createMission'),
                userType: 'admin',
                noCache: true,
                hidden: true,
                canTo: true
              }
            }
          ]
        },
        {
          path: 'departmentManage',
          component: () => import('@/views/SmartClass/AdminEnd/DepartmentManage.vue'),
          name: 'AdminDepartmentManage',
          meta: {
            title: t('admin.department'),
            userType: 'admin',
            noCache: true
          }
        }
      ]
    }
  ]
}

// 根据用户类型获取路由
export const getAsyncRouterMap = (
  userType: 'teacher' | 'student' | 'admin'
): AppRouteRecordRaw[] => {
  if (userType === 'student') {
    return getStudentRoutes()
  } else if (userType === 'teacher') {
    return getTeacherRoutes()
  } else if (userType === 'admin') {
    return getAdminRoutes()
  } else {
    return []
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
