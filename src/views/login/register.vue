<template>
  <div class="register-container">
    <h2>注册</h2>
    <el-form :model="form" label-width="70px">
      <el-form-item label="姓名">
        <el-input v-model="form.name" />
      </el-form-item>
      <el-form-item label="用户名">
        <el-input v-model="form.username" />
      </el-form-item>
      <el-form-item label="密码">
        <el-input type="password" v-model="form.password" />
      </el-form-item>
      <el-form-item label="手机号">
        <el-input v-model="form.phone" />
      </el-form-item>
      <el-form-item label="角色">
        <el-select v-model="form.role" placeholder="选择角色">
          <el-option label="客户" value="client" />
          <el-option label="管理员" value="admin" />
          <el-option label="配送员" value="delivery" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleRegister">注册</el-button>
        <el-button type="text" @click="goLogin">返回登录</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const router = useRouter()
const form = ref({
  name: '',
  username: '',
  password: '',
  phone: '',
  role: ''
})

const handleRegister = async () => {
  try {
    const res = await axios.post('http://localhost:8080/register', form.value)
    if (res.data.code === 200) {
      ElMessage.success('注册成功')
      router.push('/login')
    } else {
      ElMessage.error(res.data.msg || '注册失败')
    }
  } catch (error) {
    ElMessage.error('注册失败')
  }
}

const goLogin = () => {
  router.push('/login')
}
</script>

<style scoped>
.register-container {
  max-width: 400px;
  margin: 100px auto;
}
</style>
