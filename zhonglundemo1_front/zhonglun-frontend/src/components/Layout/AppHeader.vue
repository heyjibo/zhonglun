<template>
  <header class="app-header">
    <div class="logo-container">
      <img src="@/assets/logo.png" alt="Logo" class="logo">
      <h1>中仑管理系统</h1>
    </div>
    
    <div class="header-actions">
      <el-badge :value="cartStore.totalItems" :max="99" v-if="authStore.user?.role === 'CUSTOMER'">
        <el-button circle @click="cartDrawer.open">
          <el-icon><ShoppingCart /></el-icon>
        </el-button>
      </el-badge>
      
      <el-dropdown>
        <span class="user-info">
          <el-avatar :size="32" :src="authStore.user?.avatar" />
          <span class="user-name">{{ authStore.user?.name || authStore.user?.businessName }}</span>
          <el-icon><ArrowDown /></el-icon>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item @click="handleProfile">个人中心</el-dropdown-item>
            <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
    
    <cart-drawer ref="cartDrawer" />
  </header>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/store/modules/auth'
import { useCartStore } from '@/store/modules/cart'
import { ElMessageBox } from 'element-plus'
import CartDrawer from '@/components/CartDrawer.vue'

const router = useRouter()
const authStore = useAuthStore()
const cartStore = useCartStore()
const cartDrawer = ref()

const handleProfile = () => {
  const role = authStore.user?.role?.toLowerCase()
  router.push(`/${role}/profile`)
}

const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await authStore.logout()
    router.push('/login')
  } catch (error) {
    // 用户取消
  }
}
</script>

<style scoped>
.app-header {
  height: 60px;
  background-color: #fff;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  position: sticky;
  top: 0;
  z-index: 1000;
}

.logo-container {
  display: flex;
  align-items: center;
}

.logo {
  height: 40px;
  margin-right: 10px;
}

.app-header h1 {
  font-size: 18px;
  margin: 0;
  color: #333;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 20px;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.user-name {
  margin: 0 10px;
  font-size: 14px;
}
</style>