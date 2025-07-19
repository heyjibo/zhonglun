<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <div class="login-header">
          <h1>中论系统登录</h1>
        </div>
      </template>
      
      <el-form :model="form" :rules="rules" ref="loginForm">
        <el-form-item prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" prefix-icon="User">
            <template #prepend>
              <el-select v-model="userType" style="width: 110px">
                <el-option label="顾客" value="CUSTOMER" />
                <el-option label="商家" value="MERCHANT" />
                <el-option label="管理员" value="ADMIN" />
              </el-select>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" show-password 
                   placeholder="请输入密码" prefix-icon="Lock" />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleLogin" :loading="loading" round>
            登录
          </el-button>
        </el-form-item>
      </el-form>
      
      <div class="login-footer">
        <el-link @click="$router.push('/register/customer')">注册顾客账号</el-link>
        <el-link @click="$router.push('/register/merchant')">注册商家账号</el-link>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/store/modules/auth'
import { ElMessage } from 'element-plus'
import {createRouter as $router} from "vue-router/dist/vue-router.esm-browser.js";

const router = useRouter()
const authStore = useAuthStore()

const userType = ref('CUSTOMER')
const form = ref({
  email: '',
  password: ''
})
const loading = ref(false)

const rules = {
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  loading.value = true
  try {
    await authStore.login(form.value)
    const redirect = router.currentRoute.value.query.redirect || 
                   `/${authStore.user.role.toLowerCase()}`
    router.push(redirect)
    ElMessage.success('登录成功')
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

.login-card {
  width: 420px;
  border-radius: 8px;
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.1);
}

.login-header {
  text-align: center;
  padding: 10px 0;
}

.login-footer {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
}

</style>