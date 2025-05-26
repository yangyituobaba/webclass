<template>
  <div class="profile-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>客户个人主页</span>
        </div>
      </template>

      <el-descriptions title="基本信息" :column="1" border>
        <el-descriptions-item label="姓名">{{ profile.name }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ profile.username }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ profile.phone }}</el-descriptions-item>
        <el-descriptions-item label="角色">{{ roleMap[profile.role] || profile.role }}</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getUserProfile } from '@/services/user'

const profile = ref({})
const roleMap = {
  client: '客户',
  admin: '管理员',
  delivery: '配送员'
}

onMounted(async () => {
  try {
    const username = localStorage.getItem('username') || ''  // 假设登录后保存了用户名
    const res = await getUserProfile(username)
    if (res.code === 200) {
      profile.value = res.data
    } else {
      ElMessage.error(res.msg)
    }
  } catch (err) {
    ElMessage.error('获取用户信息失败')
  }
})


</script>

<style scoped>
.profile-container {
  max-width: 600px;
  margin: 40px auto;
}
</style>
