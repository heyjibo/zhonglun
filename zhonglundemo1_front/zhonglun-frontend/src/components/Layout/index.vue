<template>
  <div class="layout-container">
    <app-header />
    <div class="layout-main">
      <app-sidebar />
      <div class="content-wrapper">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script>
import AppHeader from './AppHeader.vue'
import AppSidebar from './AppSidebar.vue'

export default {
  components: { AppHeader, AppSidebar }
}
</script>

<style scoped>
.layout-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
}

.layout-main {
  display: flex;
  flex: 1;
}

.content-wrapper {
  flex: 1;
  padding: 20px;
  overflow: auto;
}
</style>