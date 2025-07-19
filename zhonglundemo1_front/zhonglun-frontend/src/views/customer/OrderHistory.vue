<template>
  <div class="order-history">
    <el-card>
      <template #header>
        <div class="header">
          <span>我的订单</span>
          <el-button @click="refresh" icon="Refresh">刷新</el-button>
        </div>
      </template>
      
      <el-tabs v-model="activeTab">
        <el-tab-pane label="全部订单" name="all"></el-tab-pane>
        <el-tab-pane label="待付款" name="pending"></el-tab-pane>
        <el-tab-pane label="待发货" name="processing"></el-tab-pane>
        <el-tab-pane label="待收货" name="shipped"></el-tab-pane>
        <el-tab-pane label="已完成" name="delivered"></el-tab-pane>
      </el-tabs>
      
      <order-table 
        :orders="filteredOrders" 
        :loading="loading"
        @view="handleViewOrder"
      />
      
      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        layout="prev, pager, next"
        @current-change="fetchOrders"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import OrderTable from '@/components/OrderTable.vue'
import { getCustomerOrders } from '@/api/customer'

const router = useRouter()
const activeTab = ref('all')
const orders = ref([])
const loading = ref(false)
const pagination = ref({
  current: 1,
  size: 10,
  total: 0
})

const filteredOrders = computed(() => {
  if (activeTab.value === 'all') return orders.value
  return orders.value.filter(order => order.status === activeTab.value.toUpperCase())
})

const fetchOrders = async () => {
  try {
    loading.value = true
    const res = await getCustomerOrders({
      page: pagination.value.current - 1,
      size: pagination.value.size
    })
    orders.value = res.content
    pagination.value.total = res.totalElements
  } catch (error) {
    ElMessage.error('获取订单列表失败')
  } finally {
    loading.value = false
  }
}

const handleViewOrder = (orderId) => {
  router.push(`/customer/orders/${orderId}`)
}

const refresh = () => {
  pagination.value.current = 1
  fetchOrders()
}

onMounted(fetchOrders)
</script>

<style scoped>
.order-history {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.el-pagination {
  margin-top: 20px;
  justify-content: center;
}
</style>