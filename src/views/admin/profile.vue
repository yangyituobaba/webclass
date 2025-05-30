<template>
  <div class="profile-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>管理员个人主页</span>
        </div>
      </template>

      <el-skeleton :loading="loading" animated>
        <template #default>
          <el-descriptions title="基本信息" :column="1" border>
            <el-descriptions-item label="姓名">{{ profile.name || '未填写' }}</el-descriptions-item>
            <el-descriptions-item label="用户名">{{ profile.username || '未填写' }}</el-descriptions-item>
            <el-descriptions-item label="手机号">{{ profile.phone || '未填写' }}</el-descriptions-item>
            <el-descriptions-item label="角色">{{ roleMap[profile.role] || profile.role || '未知' }}</el-descriptions-item>
          </el-descriptions>
        </template>
      </el-skeleton>
    </el-card>
  </div>

</template>

<script setup>
import { ref, onMounted } from 'vue'
import Layoutt from "@/layout/clientlayout.vue";
import { ElMessage } from 'element-plus'
import { getUserProfile } from '@/services/user'

const profile = ref({})
const loading = ref(true)

const roleMap = {
  client: '客户',
  admin: '管理员',
  delivery: '配送员'
}

onMounted(async () => {
  console.log('我到了')
  try {
    const username = localStorage.getItem('username') || ''
    console.log('现在在线的用户是 username',username)

    if (!username) {
      ElMessage.error('未找到登录信息，请重新登录')
      loading.value = false
      return
    }

    const res = await getUserProfile(username)
    console.log('获取用户接口信息返回 res',res)
    if (res.code === 200) {
      profile.value = res.data
    } else {
      ElMessage.error(res.msg || '获取用户信息失败')
    }
  } catch (err) {
    ElMessage.error('请求用户信息出错')
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.profile-container {
  margin: 5px auto; /* 水平居中，顶部底部间距50px */
  padding: 5px;
}

.card-header {
  font-weight: bold;
}
</style>
