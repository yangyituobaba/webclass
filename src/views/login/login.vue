<template>
  <div class="login-container">
    <h2>登录</h2>
    <el-form :model="form" ref="loginForm" label-width="70px">
      <el-form-item label="用户名">
        <el-input v-model="form.username" placeholder="请输入用户名"></el-input>
      </el-form-item>
      <el-form-item label="密码">
        <el-input type="password" v-model="form.password" placeholder="请输入密码"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleLogin">登录</el-button>
        <el-button type="text" @click="goRegister">去注册</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '@/services/user.js'
import useUserStore from "@/store/modules/userStore.js";
import { menulistt } from "@/layout/menuconfig.js"

const router = useRouter()
const form = ref({
  username: '',
  password: ''
})

const handleLogin = async () => {
  try {
    const res = await login(form.value.username, form.value.password)
    if (res.data.code === 200 && res.data.data) {
      ElMessage.success('登录成功')
      const userInfo = res.data.data


      const userStore = useUserStore()

      userStore.setUsername(userInfo.username)
      console.log(userStore.username)

      userStore.setRole(userInfo.role)
      userStore.setToken(userInfo.token)  // 存 token
      console.log(userStore.token)

      userStore.setMenuList(menulistt[userInfo.role])

      // 使用 userInfo.role 判断角色
      if (userInfo.role === 'admin') {
        router.push('/admin/profile')
      } else if (userInfo.role === 'client') {
        router.push('/client/profile')
      } else if (userInfo.role === 'delivery') {
        router.push('/delivery/orders')
      } else {
        ElMessage.error('未知用户角色')
      }
    } else {
      ElMessage.error('用户名或密码错误')
    }
  } catch (error) {
    console.error('登录错误:', error)
    ElMessage.error('登录失败，请检查服务端是否启动')
  }
}

const goRegister = () => {
  router.push('/register')
}
</script>

<style scoped>
.login-container {
  max-width: 400px;
  margin: 100px auto;
}
</style>
