<template>
  <div class="merchant-dashboard">
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
            <span>库存预警</span>
          </template>
          <el-table :data="lowStockProducts" v-loading="loading">
            <el-table-column prop="name" label="商品名称" />
            <el-table-column prop="stock" label="库存" width="100" />
            <el-table-column label="操作" width="120">
              <template #default="{row}">
                <el-button 
                  size="small" 
                  type="primary"
                  @click="$router.push(`/merchant/products/edit/${row.id}`)"
                >
                  补货
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
  ShoppingCart, 
  Goods, 
  Money,
  Warning
} from '@element-plus/icons-vue'
import OrderTable from '@/components/OrderTable.vue'
import { getMerchantDashboardStats } from '@/api/merchant'

const stats = ref([
  { title: '今日订单', value: 0, icon: ShoppingCart, color: '#409EFF' },
  { title: '在售商品', value: 0, icon: Goods, color: '#67C23A' },
  { title: '今日销售额', value: 0, icon: Money, color: '#E6A23C' },
  { title: '库存预警', value: 0, icon: Warning, color: '#F56C6C' }
])

const recentOrders = ref([])
const lowStockProducts = ref([])
const loading = ref(false)

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getMerchantDashboardStats()
    stats.value[0].value = res.todayOrders
    stats.value[1].value = res.activeProducts
    stats.value[2].value = res.todaySales
    stats.value[3].value = res.lowStockCount
    
    recentOrders.value = res.recentOrders
    lowStockProducts.value = res.lowStockProducts
  } catch (error) {
    console.error('获取仪表盘数据失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(fetchData)
</script>

<style scoped>
.merchant-dashboard {
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