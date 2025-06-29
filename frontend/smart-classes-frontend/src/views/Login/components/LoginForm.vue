<script setup lang="tsx">
import { reactive, ref, watch, onMounted, unref } from 'vue'
import { Form, FormSchema } from '@/components/Form'
import { useI18n } from '@/hooks/web/useI18n'
import { ElCheckbox, ElLink } from 'element-plus'
import { useForm } from '@/hooks/web/useForm'
import { loginApi, getTestRoleApi, getAdminRoleApi } from '@/api/login'
import { useAppStore } from '@/store/modules/app'
import { usePermissionStore } from '@/store/modules/permission'
import { useRouter } from 'vue-router'
import type { RouteLocationNormalizedLoaded, RouteRecordRaw } from 'vue-router'
import { UserType } from '@/api/login/types'
import { useValidator } from '@/hooks/web/useValidator'
import { useUserStore } from '@/store/modules/user'
import { BaseButton } from '@/components/Button'
import { getAsyncRouterMap } from '@/router'
import { studentList, teacherList } from './list'
import { ro } from 'element-plus/es/locale'

const { required } = useValidator()

const emit = defineEmits(['to-register'])

const appStore = useAppStore()

const userStore = useUserStore()

const permissionStore = usePermissionStore()

const { currentRoute, addRoute, push } = useRouter()

const { t } = useI18n()

const rules = {
  username: [required()],
  password: [required()],
  role: [required()]
}

const remember = ref(userStore.getRememberMe)

const loading = ref(false)

const redirect = ref<string>('')

const { formRegister, formMethods } = useForm()
const { getFormData, getElFormExpose, setValues } = formMethods

// 去注册页面
const toRegister = () => {
  emit('to-register')
}

// 跳过登录校验直接进入教师端
const skipToTeacher = async () => {
  loading.value = true
  try {
    // 设置默认用户信息（跳过登录校验）
    const defaultUserInfo = {
      username: 'teacher',
      password: 'teacher',
      role: 'teacher',
      roleId: '1',
      userType: 'teacher'
    }

    userStore.setUserInfo(defaultUserInfo)

    // 设置用户类型到权限 store
    permissionStore.setUserType('teacher')

    // 是否使用动态路由
    if (appStore.getDynamicRouter) {
      // 获取默认角色路由
      const params = {
        roleName: 'teacher'
      }
      const res = await getTestRoleApi(params)
      if (res) {
        const routers = res.data || []
        userStore.setRoleRouters(routers)
        await permissionStore.generateRoutes('frontEnd', routers).catch(() => {})

        permissionStore.getAddRouters.forEach((route) => {
          addRoute(route as RouteRecordRaw) // 动态添加可访问路由表
        })
        permissionStore.setIsAddRouters(true)
        push({ path: '/teacher/dashboard' })
      }
    } else {
      await permissionStore.generateRoutes('static').catch(() => {})
      permissionStore.getAddRouters.forEach((route) => {
        addRoute(route as RouteRecordRaw) // 动态添加可访问路由表
      })
      permissionStore.setIsAddRouters(true)
      push({ path: '/teacher/dashboard' })
    }
  } finally {
    loading.value = false
  }
}

// 跳过登录校验直接进入学生端
const skipToStudent = async () => {
  loading.value = true
  try {
    // 设置默认用户信息（跳过登录校验）
    const defaultUserInfo = {
      username: 'student',
      password: 'student',
      role: 'student',
      roleId: '2',
      userType: 'student'
    }

    userStore.setUserInfo(defaultUserInfo)

    // 设置用户类型到权限 store
    permissionStore.setUserType('student')

    // 是否使用动态路由
    if (appStore.getDynamicRouter) {
      // 获取默认角色路由
      const params = {
        roleName: 'student'
      }
      const res = await getTestRoleApi(params)
      if (res) {
        const routers = res.data || []
        userStore.setRoleRouters(routers)
        await permissionStore.generateRoutes('frontEnd', routers).catch(() => {})

        permissionStore.getAddRouters.forEach((route) => {
          addRoute(route as RouteRecordRaw) // 动态添加可访问路由表
        })
        permissionStore.setIsAddRouters(true)
        push({ path: '/student/courseList' })
      }
    } else {
      await permissionStore.generateRoutes('static').catch(() => {})
      permissionStore.getAddRouters.forEach((route) => {
        addRoute(route as RouteRecordRaw) // 动态添加可访问路由表
      })
      permissionStore.setIsAddRouters(true)
      push({ path: '/student/courseList' })
    }
  } finally {
    loading.value = false
  }
}

// 登录
const signIn = async () => {
  const formRef = await getElFormExpose()
  await formRef?.validate(async (isValid) => {
    if (isValid) {
      loading.value = true
      const formData = await getFormData<UserType>()
      const role = formData.role === 'teacher' ? 'teacher' : 'student'
      const roleList = formData.role === 'teacher' ? teacherList : studentList
      permissionStore.setUserType(role)
      try {
        const res = await loginApi(formData)
        console.log(res)
        if (res.data === true) {
          // 是否记住我
          if (unref(remember)) {
            userStore.setLoginInfo({
              username: formData.username,
              password: formData.password,
              role: formData.role
            })
          } else {
            userStore.setLoginInfo(undefined)
          }
          userStore.setRememberMe(unref(remember))
          const extendedFormData = {
            ...formData,
            permissions: ['*.*.*']
          }
          userStore.setUserInfo(extendedFormData)
          // 是否使用动态路由
          if (appStore.getDynamicRouter) {
            // getRole()
            console.log(roleList)
            userStore.setRoleRouters(roleList)
            await permissionStore.generateRoutes('frontEnd', roleList).catch(() => {})
            // 动态添加路由
            permissionStore.getAddRouters.forEach((route) => {
              addRoute(route as RouteRecordRaw)
              console.log(route)
            })
            permissionStore.setIsAddRouters(true)
            console.log('redirect', redirect.value)
            console.log('permissionStore.addRouters', permissionStore.addRouters[0].path)

            // 跳转首页
            push({ path: permissionStore.addRouters[0].path })
            console.log('push s')
          } else {
            await permissionStore.generateRoutes('static').catch(() => {})
            permissionStore.getAddRouters.forEach((route) => {
              addRoute(route as RouteRecordRaw) // 动态添加可访问路由表
            })
            permissionStore.setIsAddRouters(true)
            push({ path: redirect.value || permissionStore.addRouters[0].path })
          }
        }
      } finally {
        loading.value = false
      }
    }
  })
}

const initLoginInfo = () => {
  const loginInfo = userStore.getLoginInfo
  if (loginInfo) {
    const { username, password } = loginInfo
    setValues({ username, password })
  }
}

onMounted(() => {
  initLoginInfo()
})

watch(
  () => currentRoute.value,
  (route: RouteLocationNormalizedLoaded) => {
    redirect.value = route?.query?.redirect as string
  },
  {
    immediate: true
  }
)

const schema = reactive<FormSchema[]>([
  {
    field: 'title',
    colProps: {
      span: 24
    },
    formItemProps: {
      slots: {
        default: () => {
          return <h2 class="text-2xl font-bold text-center w-[100%]">{t('login.login')}</h2>
        }
      }
    }
  },
  {
    field: 'username',
    label: t('login.username'),
    component: 'Input',
    colProps: {
      span: 24
    },
    componentProps: {
      placeholder: '请输入学号或工号'
    }
  },
  {
    field: 'password',
    label: t('login.password'),
    component: 'InputPassword',
    colProps: {
      span: 24
    },
    componentProps: {
      style: {
        width: '100%'
      },
      placeholder: '请输入密码',
      // 按下enter键触发登录
      onKeydown: (_e: any) => {
        if (_e.key === 'Enter') {
          _e.stopPropagation() // 阻止事件冒泡
          signIn()
        }
      }
    }
  },
  {
    field: 'role',
    label: t('login.role'),
    component: 'Select',
    colProps: {
      span: 24
    },
    componentProps: {
      options: [
        {
          label: '教师',
          value: 'teacher'
        },
        {
          label: '学生',
          value: 'student'
        }
      ],
      placeholder: '请选择角色'
    }
  },
  {
    field: 'tool',
    colProps: {
      span: 24
    },
    formItemProps: {
      slots: {
        default: () => {
          return (
            <>
              <div class="flex justify-between items-center w-[100%]">
                <ElCheckbox v-model={remember.value} label={t('login.remember')} size="small" />
                <div class="flex space-x-2">
                  <ElLink type="primary" underline={false} onClick={skipToTeacher}>
                    教师端
                  </ElLink>
                  <ElLink type="success" underline={false} onClick={skipToStudent}>
                    学生端
                  </ElLink>
                </div>
              </div>
            </>
          )
        }
      }
    }
  },
  {
    field: 'login',
    colProps: {
      span: 24
    },
    formItemProps: {
      slots: {
        default: () => {
          return (
            <>
              <div class="w-[100%]">
                <BaseButton
                  loading={loading.value}
                  type="primary"
                  class="w-[100%]"
                  onClick={signIn}
                >
                  {t('login.login')}
                </BaseButton>
              </div>
              <div class="w-[100%] mt-15px">
                <BaseButton class="w-[100%]" onClick={toRegister}>
                  {t('login.register')}
                </BaseButton>
              </div>
            </>
          )
        }
      }
    }
  }
])
</script>

<template>
  <Form
    :schema="schema"
    :rules="rules"
    label-position="top"
    hide-required-asterisk
    size="large"
    class="dark:(border-1 border-[var(--el-border-color)] border-solid)"
    @register="formRegister"
  />
</template>
