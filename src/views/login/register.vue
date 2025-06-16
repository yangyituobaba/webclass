<template>
  <div class="register-container">
    <h2>注册</h2>
    <el-form :model="form" :rules="rules" ref="formRef" label-width="70px">
      <el-form-item label="姓名" prop="name">
        <el-input v-model="form.name" />
      </el-form-item>
      <el-form-item label="用户名" prop="username">
        <el-input v-model="form.username" />
      </el-form-item>
      <el-form-item label="密码" prop="password">
        <el-input type="password" v-model="form.password" />
      </el-form-item>
      <el-form-item label="手机号" prop="phone">
        <el-input v-model="form.phone" />
      </el-form-item>
      <el-form-item label="角色" prop="role">
        <el-select v-model="form.role" placeholder="选择角色">
          <el-option label="客户" value="client" />
          <el-option label="管理员" value="admin" />
          <el-option label="配送员" value="delivery" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="loading" @click="handleRegister">注册</el-button>
        <el-button type="text" @click="goLogin">返回登录</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {register} from '@/services/user.js'  // 改成你的路径

// 表单状态
const router = useRouter()
const formRef = ref(null)
const loading = ref(false)
const form = ref({
  name: '',
  username: '',
  password: '',
  phone: '',
  role: ''
})

// 表单验证规则
const rules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

// 注册提交
const handleRegister = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      const payload = { ...form.value } // 深拷贝，防止响应式对象被污染
      const res = await register(payload)
      if (res.data.code === 200) {
        ElMessage.success('注册成功')
        router.push('/login')
      } else {
        ElMessage.error(res.data.msg || '注册失败')
      }
    } catch (error) {
      ElMessage.error('注册请求失败，请稍后重试')
    } finally {
      loading.value = false
    }
  })
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
