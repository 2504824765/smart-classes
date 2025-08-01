<script setup lang="tsx">
import { Form, FormSchema } from '@/components/Form'
import { reactive, ref, unref } from 'vue'
import { useI18n } from '@/hooks/web/useI18n'
import { registerApi } from '@/api/login'
import { UserType } from '@/api/login/types'
import { useForm } from '@/hooks/web/useForm'
import { ElInput, FormRules } from 'element-plus'
import { useValidator } from '@/hooks/web/useValidator'
import { BaseButton } from '@/components/Button'
import { IAgree } from '@/components/IAgree'

const emit = defineEmits(['to-login'])

const { formRegister, formMethods } = useForm()
const { getFormData, getElFormExpose } = formMethods

const { t } = useI18n()

const { required, check } = useValidator()

const getCodeTime = ref(60)
const getCodeLoading = ref(false)
const getCode = () => {
  getCodeLoading.value = true
  const timer = setInterval(() => {
    getCodeTime.value--
    if (getCodeTime.value <= 0) {
      clearInterval(timer)
      getCodeTime.value = 60
      getCodeLoading.value = false
    }
  }, 1000)
}

const schema = reactive<FormSchema[]>([
  {
    field: 'title',
    colProps: {
      span: 24
    },
    formItemProps: {
      slots: {
        default: () => {
          return <h2 class="text-2xl font-bold text-center w-[100%]">{t('login.register')}</h2>
        }
      }
    }
  },
  {
    field: 'username',
    label: t('login.username'),
    value: '',
    component: 'Input',
    colProps: {
      span: 24
    },
    componentProps: {
      placeholder: t('login.usernamePlaceholder')
    }
  },
  {
    field: 'password',
    label: t('login.password'),
    value: '',
    component: 'InputPassword',
    colProps: {
      span: 24
    },
    componentProps: {
      style: {
        width: '100%'
      },
      placeholder: t('login.passwordPlaceholder')
    }
  },
  {
    field: 'check_password',
    label: t('login.checkPassword'),
    value: '',
    component: 'InputPassword',
    colProps: {
      span: 24
    },
    componentProps: {
      style: {
        width: '100%'
      },
      placeholder: t('login.passwordPlaceholder')
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
      placeholder: '请选择角色',
      options: [
        {
          label: '学生',
          value: 'student'
        },
        {
          label: '教师',
          value: 'teacher'
        }
      ]
    }
  },
  {
    field: 'iAgree',
    colProps: {
      span: 24
    },
    formItemProps: {
      slots: {
        default: (formData: any) => {
          return (
            <>
              <IAgree
                v-model={formData.iAgree}
                text="我同意《用户协议》"
                link={[
                  {
                    text: '《用户协议》',
                    url: 'https://element-plus.org/'
                  }
                ]}
              />
            </>
          )
        }
      }
    }
  },
  {
    field: 'register',
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
                  type="primary"
                  class="w-[100%]"
                  loading={loading.value}
                  onClick={loginRegister}
                >
                  {t('login.register')}
                </BaseButton>
              </div>
              <div class="w-[100%] mt-15px">
                <el-button class="w-[100%]" onClick={toLogin}>
                  {t('login.hasUser')}
                </el-button>
              </div>
            </>
          )
        }
      }
    }
  }
])

const rules: FormRules = {
  username: [required()],
  password: [
    required(),
    {
      min: 4,
      max: 20,
      message: '密码长度必须在4到20个字符之间',
      trigger: 'blur' // 触发时机（blur或change）
    }
  ],
  check_password: [
    required(),
    {
      validator: (rule, value, callback) => {
        formMethods
          .getFormData()
          .then((formData) => {
            if (value !== formData.password) {
              callback(new Error('两次输入的密码不一致'))
            } else {
              callback()
            }
          })
          .catch(() => callback())
      },
      trigger: 'blur'
    }
  ],
  code: [required()],
  iAgree: [required(), check()]
}

const toLogin = () => {
  emit('to-login')
}

const loading = ref(false)

const loginRegister = async () => {
  const formRef = await getElFormExpose()
  formRef?.validate(async (valid) => {
    if (valid) {
      try {
        const formData = await getFormData<UserType>()
        console.log(formData)
        const res = await registerApi(formData)
        if (res.data === true) {
          loading.value = true
          toLogin()
        }
      } finally {
        loading.value = false
      }
    }
  })
}
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
