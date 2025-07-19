<template>
  <div class="dashboard-container">
    <el-row :gutter="20">
      <el-col :span="6" v-for="stat in stats" :key="stat.title">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" :style="{ backgroundColor: stat.color }">
              <el-icon :size="24">
                <component :is="stat.icon" />
              </el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-title">{{ stat.title }}</div>
              <div class="stat-value">{{ stat.value }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <span>最近订单</span>
          </template>
          <order-table :orders="recentOrders" />
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <span>待审核商家</span>
          </template>
          <el-table :data="pendingMerchants" v-loading="loading">
            <el-table-column prop="businessName" label="商家名称" />
            <el-table-column prop="contactPerson" label="联系人" />
            <el-table-column label="操作" width="120">
              <template #default="{row}">
                <el-button 
                  size="small" 
                  type="primary"
                  @click="approveMerchant(row.id)"
                >
                  批准
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { 
  User, 
  ShoppingCart, 
  Shop, 
  Money 
} from '@element-plus/icons-vue'
import OrderTable from '@/components/OrderTable.vue'
import { 
  getDashboardStats,
  getRecentOrders,
  getPendingMerchants,
  approveMerchant 
} from '@/api/admin'

const stats = ref([
  { title: '总用户数', value: 0, icon: User, color: '#409EFF' },
  { title: '总订单数', value: 0, icon: ShoppingCart, color: '#67C23A' },
  { title: '总商家数', value: 0, icon: Shop, color: '#E6A23C' },
  { title: '总销售额', value: 0, icon: Money, color: '#F56C6C' }
])

const recentOrders = ref([])
const pendingMerchants = ref([])
const loading = ref(false)

const fetchData = async () => {
  loading.value = true
  try {
    const [statsRes, ordersRes, merchantsRes] = await Promise.all([
      getDashboardStats(),
      getRecentOrders(),
      getPendingMerchants()
    ])
    
    stats.value[0].value = statsRes.totalCustomers
    stats.value[1].value = statsRes.totalOrders
    stats.value[2].value = statsRes.totalMerchants
    stats.value[3].value = statsRes.totalSales
    
    recentOrders.value = ordersRes
    pendingMerchants.value = merchantsRes
  } catch (error) {
    console.error('获取仪表盘数据失败:', error)
  } finally {
    loading.value = false
  }
}

const approveMerchant = async (id) => {
  try {
    await approveMerchant(id, true)
    ElMessage.success('商家已批准')
    fetchData()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

onMounted(fetchData)
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}

.stat-card {
  margin-bottom: 20px;
}

.stat-content {
  display: flex;
  align-items: center;
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  margin-right: 15px;
}

.stat-info {
  flex: 1;
}

.stat-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 5px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
}
</style>