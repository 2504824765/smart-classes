import type { RouteMeta } from 'vue-router'
import { Icon } from '@/components/Icon'
import { useI18n } from '@/hooks/web/useI18n'
import { useRoute } from 'vue-router'

export const useRenderMenuTitle = () => {
  const renderMenuTitle = (meta: RouteMeta) => {
    const { t } = useI18n()
    const route = useRoute()
    const { title = 'Please set title', icon, userType } = meta

    // 从当前路由获取用户类型
    const getCurrentUserType = () => {
      // 优先使用 meta 中的 userType
      if (userType) {
        return userType
      }
      // 从当前路由路径判断用户类型
      if (route.path.startsWith('/teacher')) {
        return 'teacher'
      }
      if (route.path.startsWith('/student')) {
        return 'student'
      }
      return 'teacher' // 默认教师端
    }

    // 根据用户类型获取对应的翻译
    const getTranslatedTitle = () => {
      const currentUserType = getCurrentUserType()
      if (title) {
        // 如果标题是简化的键名，根据用户类型获取对应翻译
        if (typeof title === 'string' && !title.includes('.')) {
          return t(`${currentUserType}.${title}`)
        }
        // 如果标题是完整的翻译键，直接使用
        return t(title as string)
      }
      return t(title as string)
    }

    return icon ? (
      <>
        <Icon icon={meta.icon}></Icon>
        <span class="v-menu__title overflow-hidden overflow-ellipsis whitespace-nowrap">
          {getTranslatedTitle()}
        </span>
      </>
    ) : (
      <span class="v-menu__title overflow-hidden overflow-ellipsis whitespace-nowrap">
        {getTranslatedTitle()}
      </span>
    )
  }

  return {
    renderMenuTitle
  }
}
