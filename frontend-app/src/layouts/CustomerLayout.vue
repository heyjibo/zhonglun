<!-- src/layouts/CustomerLayout.vue -->
<template>
  <el-container class="customer-layout">
    <!-- 顶部导航栏 -->
    <el-header class="header">
      <div class="header-content">
        <div class="logo-area" @click="$router.push('/')">
          <img src="/logo.svg" alt="Platform Logo" class="logo-img" />
          <span class="logo-text">电商平台</span>
        </div>
        <el-menu mode="horizontal" :ellipsis="false" router>
          <el-menu-item index="/">首页</el-menu-item>
          <el-menu-item index="/products">商品中心</el-menu-item>
          <el-menu-item index="/cart">购物车</el-menu-item>
        </el-menu>
        <div class="user-area">
          <el-dropdown v-if="authStore.isLoggedIn">
            <span class="el-dropdown-link">
              欢迎, {{ authStore.userInfo.username }}
              <el-icon class="el-icon--right"><arrow-down /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="$router.push('/customer/profile')">个人中心</el-dropdown-item>
                <el-dropdown-item @click="$router.push('/customer/orders')">我的订单</el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
          <div v-else>
            <el-button type="primary" link @click="$router.push('/login')">登录</el-button>
            <el-button link @click="$router.push('/register')">注册</el-button>
          </div>
        </div>
      </div>
    </el-header>

    <!-- 主内容区域 -->
    <el-main class="main-content">
      <router-view />
    </el-main>

    <!-- 底部信息栏 -->
    <el-footer class="footer">
      <p>&copy; 2025 电商平台. All Rights Reserved.</p>
    </el-footer>
  </el-container>
</template>

<script setup>
import { useAuthStore } from '@/store/auth';
import { useRouter } from 'vue-router';
import { ArrowDown } from '@element-plus/icons-vue';

const authStore = useAuthStore();
const router = useRouter();

const handleLogout = () => {
  authStore.logout();
  router.push('/login');
};
</script>

<style scoped>
.customer-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  border-bottom: 1px solid #e4e7ed;
  background-color: #fff;
  padding: 0 50px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
}

.logo-area {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.logo-img {
  height: 40px;
  margin-right: 10px;
}

.logo-text {
  font-size: 20px;
  font-weight: bold;
}

.user-area {
  display: flex;
  align-items: center;
}

.el-dropdown-link {
  cursor: pointer;
  color: var(--el-color-primary);
  display: flex;
  align-items: center;
}

.main-content {
  flex: 1; /* 让主内容区域占据剩余所有空间 */
  padding: 20px;
  background-color: #f5f7fa;
}

.footer {
  text-align: center;
  padding: 20px;
  color: #909399;
  background-color: #fff;
  border-top: 1px solid #e4e7ed;
}

.el-menu {
  border-bottom: none;
}
</style>
