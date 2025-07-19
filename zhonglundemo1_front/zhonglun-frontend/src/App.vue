<template>
  <router-view />
</template>

<script setup>
import { onMounted } from 'vue'
import { useAuthStore } from '@/store/modules/auth'

const authStore = useAuthStore()

onMounted(() => {
  // 初始化用户信息
  if (authStore.token) {
    authStore.getCurrentUser()
  }
})
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
}

body {
  margin: 0;
  padding: 0;
}
</style>