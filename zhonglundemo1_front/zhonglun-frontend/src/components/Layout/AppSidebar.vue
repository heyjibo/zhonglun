<template>
  <el-menu
    :default-active="activeMenu"
    router
    class="sidebar-menu"
    :collapse="isCollapse"
    background-color="#304156"
    text-color="#bfcbd9"
    active-text-color="#409EFF"
  >
    <template v-for="item in menus" :key="item.path">
      <el-sub-menu v-if="item.children" :index="item.path">
        <template #title>
          <el-icon>
            <component :is="item.icon" />
          </el-icon>
          <span>{{ item.title }}</span>
        </template>
        <el-menu-item
          v-for="child in item.children"
          :key="child.path"
          :index="child.path"
        >
          {{ child.title }}
        </el-menu-item>
      </el-sub-menu>
      <el-menu-item v-else :index="item.path">
        <el-icon>
          <component :is="item.icon" />
        </el-icon>
        <template #title>{{ item.title }}</template>
      </el-menu-item>
    </template>
  </el-menu>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '@/store/modules/auth'

const route = useRoute()
const authStore = useAuthStore()
const isCollapse = ref(false)

const activeMenu = computed(() => {
  const { path } = route
  return path
})

// 根据用户角色返回不同的菜单
const menus = computed(() => {
  const role = authStore.user?.role
  
  const baseMenus = [
    {
      title: '首页',
      path: '/dashboard',
      icon: 'House'
    }
  ]
  
  if (role === 'ADMIN') {
    return [
      ...baseMenus,
      {
        title: '用户管理',
        path: '/admin/users',
        icon: 'User'
      },
      {
        title: '商家管理',
        path: '/admin/merchants',
        icon: 'Shop'
      },
      {
        title: '系统设置',
        path: '/admin/settings',
        icon: 'Setting'
      }
    ]
  } else if (role === 'MERCHANT') {
    return [
      ...baseMenus,
      {
        title: '商品管理',
        path: '/merchant/products',
        icon: 'Goods'
      },
      {
        title: '订单管理',
        path: '/merchant/orders',
        icon: 'Tickets'
      },
      {
        title: '销售报表',
        path: '/merchant/sales',
        icon: 'DataAnalysis'
      }
    ]
  } else {
    return [
      ...baseMenus,
      {
        title: '商品列表',
        path: '/customer/products',
        icon: 'Goods'
      },
      {
        title: '我的订单',
        path: '/customer/orders',
        icon: 'Tickets'
      },
      {
        title: '个人中心',
        path: '/customer/profile',
        icon: 'User'
      }
    ]
  }
})
</script>

<style scoped>
.sidebar-menu {
  height: 100%;
  border-right: none;
}

.sidebar-menu:not(.el-menu--collapse) {
  width: 220px;
}
</style>