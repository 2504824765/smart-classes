<script setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import { ref, onMounted, computed } from 'vue'
import { ElDivider, ElImage, ElTag, ElTabPane, ElTabs, ElButton, ElMessage } from 'element-plus'
import defaultAvatar from '@/assets/imgs/avatar.jpg'
import UploadAvatar from './components/UploadAvatar.vue'
import { Dialog } from '@/components/Dialog'
import EditInfo from './components/EditInfo.vue'
import EditPassword from './components/EditPassword.vue'
import { getStudentByUsernameApi } from '@/api/student'
import { getTeacherByUsernameApi } from '@/api/teacher'
import { useUserStore } from '@/store/modules/user'

const userStore = useUserStore()
const userInfo = ref({ username: '', name: '', role: '', avatarUrl: '' })
const role = computed(() => userStore.getUserInfo?.role)

const fetchUserInfo = async () => {
  const username = userStore.getUserInfo?.username
  if (!username) return
  if (role.value === 'student') {
    const res = await getStudentByUsernameApi(username)
    if (res && res.data) {
      userInfo.value = {
        username: res.data.username,
        name: res.data.name,
        role: '学生',
        avatarUrl: ''
      }
    }
  } else if (role.value === 'teacher') {
    const res = await getTeacherByUsernameApi(username)
    if (res && res.data) {
      userInfo.value = {
        username: res.data.username,
        name: res.data.name,
        role: '教师',
        avatarUrl: ''
      }
    }
  }
}

onMounted(() => {
  fetchUserInfo()
})

const activeName = ref('first')
const dialogVisible = ref(false)
const uploadAvatarRef = ref()
const avatarLoading = ref(false)
const saveAvatar = async () => {
  try {
    avatarLoading.value = true
    const base64 = uploadAvatarRef.value?.getBase64()
    // 这里可以调用修改头像接口
    fetchUserInfo()
    ElMessage.success('修改成功')
    dialogVisible.value = false
  } catch (error) {
    console.log(error)
  } finally {
    avatarLoading.value = false
  }
}
</script>

<template>
  <div class="flex w-100% h-100%">
    <ContentWrap title="个人信息" class="w-400px">
      <div class="flex justify-center items-center">
        <div
          class="avatar w-[150px] h-[150px] relative cursor-pointer"
          @click="dialogVisible = true"
        >
          <ElImage
            class="w-[150px] h-[150px] rounded-full"
            :src="userInfo.avatarUrl || defaultAvatar"
            fit="fill"
          />
        </div>
      </div>
      <ElDivider />
      <div class="flex justify-between items-center">
        <div>用户名：</div>
        <div>{{ userInfo.username }}</div>
      </div>
      <ElDivider />
      <div class="flex justify-between items-center">
        <div>姓名：</div>
        <div>{{ userInfo.name }}</div>
      </div>
      <ElDivider />
      <div class="flex justify-between items-center">
        <div>所属角色：</div>
        <div>
          <ElTag>{{ userInfo.role }}</ElTag>
        </div>
      </div>
      <ElDivider />
    </ContentWrap>
    <ContentWrap title="基本资料" class="flex-[3] ml-20px">
      <ElTabs v-model="activeName">
        <ElTabPane label="基本信息" name="first">
          <EditInfo :user-info="userInfo" :role="role" />
        </ElTabPane>
        <ElTabPane label="修改密码" name="second">
          <EditPassword />
        </ElTabPane>
      </ElTabs>
    </ContentWrap>
  </div>

  <Dialog v-model="dialogVisible" title="修改头像" width="800px">
    <UploadAvatar ref="uploadAvatarRef" :url="userInfo.avatarUrl || defaultAvatar" />
    <template #footer>
      <ElButton type="primary" :loading="avatarLoading" @click="saveAvatar"> 保存 </ElButton>
      <ElButton @click="dialogVisible = false">关闭</ElButton>
    </template>
  </Dialog>
</template>

<style lang="less" scoped>
.avatar {
  position: relative;
  &::after {
    position: absolute;
    top: 0;
    left: 0;
    display: flex;
    width: 100%;
    height: 100%;
    font-size: 50px;
    color: #fff;
    background-color: rgb(0 0 0 / 40%);
    border-radius: 50%;
    content: '+';
    opacity: 0;
    justify-content: center;
    align-items: center;
  }
  &:hover {
    &::after {
      opacity: 1;
    }
  }
}
</style>
