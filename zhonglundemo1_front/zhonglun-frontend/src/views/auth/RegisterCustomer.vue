<template>
  <div class="register-container">
    <el-card class="register-card">
      <template #header>
        <div class="register-header">
          <h2>顾客注册</h2>
        </div>
      </template>
      
      <el-form 
        :model="form" 
        :rules="rules" 
        ref="registerForm"
        label-width="100px"
      >
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password placeholder="至少6位字符" />
        </el-form-item>
        
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password" show-password placeholder="请再次输入密码" />
        </el-form-item>
        
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入您的姓名" />
        </el-form-item>
        
        <el-form-item label="电话" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入联系电话" />
        </el-form-item>
        
        <el-form-item label="地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入详细地址" />
        </el-form-item>
        
        <el-form-item>
          <el-button 
            type="primary" 
            @click="handleRegister"
            :loading="loading"
          >
            立即注册
          </el-button>
          <el-button @click="$router.push('/login')">返回登录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { registerCustomer } from '@/api/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const form = ref({
  email: '',
  password: '',
  confirmPassword: '',
  name: '',
  phone: '',
  address: ''
})
const loading = ref(false)

const validatePassword = (rule, value, callback) => {
  if (value !== form.value.password) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validatePassword, trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  try {
    loading.value = true
    await registerCustomer(form.value)
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '注册失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  padding: 40px 0;
}

.register-card {
  width: 600px;
}

.register-header {
  text-align: center;
  padding: 10px 0;
}

.el-button {
  width: 120px;
}
</style>