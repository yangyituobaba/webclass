<template>
  <div class="user-info-edit">
    <el-form :model="form" label-width="100px" size="medium">
      <el-form-item label="姓名">
        <el-input v-model="form.name" />
      </el-form-item>

      <el-form-item label="用户名">
        <el-input v-model="form.username" />
      </el-form-item>

      <el-form-item label="手机号">
        <el-input v-model="form.phone" />
      </el-form-item>

      <el-form-item label="当前密码" required>
        <el-input
            v-model="form.currentPassword"
            type="password"
            autocomplete="off"
            placeholder="请输入当前密码用于验证"
        />
      </el-form-item>

      <el-form-item label="新密码">
        <el-input
            v-model="form.newPassword"
            type="password"
            autocomplete="off"
            placeholder="如不修改密码，请留空"
        />
      </el-form-item>

      <el-form-item>
        <el-button type="success" @click="submit">保存修改</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { updateUser, getUserProfile } from '@/services/user'
import useUserStore from '@/store/modules/userStore.js'

const userStore = useUserStore()

const form = ref({
  name: '',
  username: '',
  phone: '',
  currentPassword: '', // 当前密码，用于身份验证，必填
  newPassword: '',     // 新密码，不修改则为空字符串
})

// 封装刷新用户信息的方法
async function refreshUserProfile() {
  try {
    const res = await getUserProfile(userStore.token)
    console.log('刷新用户信息返回:', res)
    if (res.code === 200 && res.data) {
      form.value.name = res.data.name || ''
      form.value.username = res.data.username || ''
      form.value.phone = res.data.phone || ''
    } else {
      ElMessage.error(res.msg || '刷新用户信息失败')
    }
  } catch (err) {
    console.error('刷新用户信息失败:', err)
  }
}

async function submit() {
  console.log('提交表单数据:', form.value)

  if (!form.value.currentPassword) {
    ElMessage.error('请先输入当前密码进行验证')
    return
  }

  try {
    const res = await updateUser(
        {
          name: form.value.name,
          username: form.value.username,
          phone: form.value.phone,
          password: form.value.newPassword || '',
        },
        form.value.currentPassword
    )
    console.log('修改用户返回:', res)

    // 修改判断条件，适配接口返回格式
    if (res.data.code === 200) {
      ElMessage.success('修改成功')
      form.value.currentPassword = ''
      form.value.newPassword = ''
      await refreshUserProfile() // 修改成功后刷新用户信息
    } else {
      ElMessage.error(res.data.msg || '修改失败')
    }
  } catch (err) {
    console.error('请求出错:', err)
    ElMessage.error('请求出错，请稍后重试')
  }
}

onMounted(async () => {
  const username = userStore.username
  const token = userStore.token
  console.log('当前登录用户名:', username)
  console.log('当前登录token:', token)

  if (!username || !token) {
    ElMessage.error('未找到登录信息，请重新登录')
    return
  }

  try {
    const res = await getUserProfile(token)
    console.log('获取用户信息返回:', res)

    if (res.code === 200 && res.data) {
      form.value.name = res.data.name || ''
      form.value.username = res.data.username || ''
      form.value.phone = res.data.phone || ''
    } else {
      ElMessage.error(res.msg || '获取用户信息失败')
    }
  } catch (err) {
    console.error('请求用户信息出错:', err)
    ElMessage.error('请求用户信息出错')
  }
})
</script>
