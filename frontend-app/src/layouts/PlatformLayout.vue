<!-- src/layouts/PlatformLayout.vue -->
<template>
  <el-container class="admin-layout">
    <!-- 侧边栏菜单 -->
    <el-aside width="200px" class="aside">
      <div class="aside-logo">
        <img src="/logo.svg" alt="Logo" class="logo-img" />
        <span>平台管理系统</span>
      </div>
      <el-menu
          :default-active="activeMenu"
          class="el-menu-vertical"
          router
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409EFF"
      >
        <el-menu-item index="/platform/dashboard">
          <el-icon><DataLine /></el-icon>
          <span>仪表盘</span>
        </el-menu-item>
        <el-menu-item index="/platform/merchants">
          <el-icon><Shop /></el-icon>
          <span>商家审核</span>
        </el-menu-item>
        <el-menu-item index="/platform/products">
          <el-icon><Goods /></el-icon>
          <span>商品管理</span>
        </el-menu-item>
        <el-menu-item index="/platform/orders">
          <el-icon><List /></el-icon>
          <span>订单总览</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <!-- 顶部栏 -->
      <el-header class="header">
        <div class="breadcrumb">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/platform/dashboard' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="$route.meta.title">{{ $route.meta.title }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="user-info">
          <el-dropdown>
            <span class="el-dropdown-link">
              {{ authStore.userInfo.username }}
              <el-icon class="el-icon--right"><arrow-down /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 主内容区域 -->
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useAuthStore } from '@/store/auth';
// 确保已安装 @element-plus/icons-vue
import { DataLine, Shop, Goods, List, ArrowDown } from '@element-plus/icons-vue';

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();

const activeMenu = computed(() => route.path);

const handleLogout = () => {
  authStore.logout();
  router.push('/login');
};
</script>

<style scoped>
/* 与 MerchantLayout.vue 共享样式 */
.admin-layout {
  height: 100vh;
}
.aside {
  background-color: #304156;
  color: #fff;
}
.aside-logo {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 15px 0;
  border-bottom: 1px solid #4a5a71;
}
.logo-img {
  height: 32px;
  margin-right: 8px;
}
.el-menu-vertical {
  border-right: none;
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
  border-bottom: 1px solid #d8dce5;
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.12), 0 0 3px 0 rgba(0, 0, 0, 0.04);
}
.main-content {
  background-color: #f0f2f5;
  padding: 20px;
}
.el-dropdown-link {
  cursor: pointer;
  display: flex;
  align-items: center;
}
</style>
