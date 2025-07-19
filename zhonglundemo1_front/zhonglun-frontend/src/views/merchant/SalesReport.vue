<template>
  <div class="sales-report">
    <el-card>
      <template #header>
        <div class="report-header">
          <h2>销售报表</h2>
          <div class="date-picker">
            <el-date-picker
              v-model="dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              @change="fetchSalesData"
            />
          </div>
        </div>
      </template>
      
      <el-row :gutter="20" class="stats-row">
        <el-col :span="6">
          <el-card shadow="hover">
            <div class="stat-card">
              <div class="stat-title">总销售额</div>
              <div class="stat-value">¥{{ salesData.totalSales.toFixed(2) }}</div>
              <div class="stat-trend">
                <el-icon :color="salesData.salesTrend >= 0 ? '#67C23A' : '#F56C6C'">
                  <CaretTop v-if="salesData.salesTrend >= 0" />
                  <CaretBottom v-else />
                </el-icon>
                <span :style="{ color: salesData.salesTrend >= 0 ? '#67C23A' : '#F56C6C' }">
                  {{ Math.abs(salesData.salesTrend) }}%
                </span>
                <span class="stat-compare">同比</span>
              </div>
            </div>
          </el-card>
        </el-col>
        
        <el-col :span="6">
          <el-card shadow="hover">
            <div class="stat-card">
              <div class="stat-title">订单总数</div>
              <div class="stat-value">{{ salesData.totalOrders }}</div>
              <div class="stat-trend">
                <el-icon :color="salesData.ordersTrend >= 0 ? '#67C23A' : '#F56C6C'">
                  <CaretTop v-if="salesData.ordersTrend >= 0" />
                  <CaretBottom v-else />
                </el-icon>
                <span :style="{ color: salesData.ordersTrend >= 0 ? '#67C23A' : '#F56C6C' }">
                  {{ Math.abs(salesData.ordersTrend) }}%
                </span>
                <span class="stat-compare">同比</span>
              </div>
            </div>
          </el-card>
        </el-col>
        
        <el-col :span="6">
          <el-card shadow="hover">
            <div class="stat-card">
              <div class="stat-title">商品销量</div>
              <div class="stat-value">{{ salesData.totalProductsSold }}</div>
              <div class="stat-trend">
                <el-icon :color="salesData.productsTrend >= 0 ? '#67C23A' : '#F56C6C'">
                  <CaretTop v-if="salesData.productsTrend >= 0" />
                  <CaretBottom v-else />
                </el-icon>
                <span :style="{ color: salesData.productsTrend >= 0 ? '#67C23A' : '#F56C6C' }">
                  {{ Math.abs(salesData.productsTrend) }}%
                </span>
                <span class="stat-compare">同比</span>
              </div>
            </div>
          </el-card>
        </el-col>
        
        <el-col :span="6">
          <el-card shadow="hover">
            <div class="stat-card">
              <div class="stat-title">平均客单价</div>
              <div class="stat-value">¥{{ salesData.avgOrderValue.toFixed(2) }}</div>
              <div class="stat-trend">
                <el-icon :color="salesData.avgOrderTrend >= 0 ? '#67C23A' : '#F56C6C'">
                  <CaretTop v-if="salesData.avgOrderTrend >= 0" />
                  <CaretBottom v-else />
                </el-icon>
                <span :style="{ color: salesData.avgOrderTrend >= 0 ? '#67C23A' : '#F56C6C' }">
                  {{ Math.abs(salesData.avgOrderTrend) }}%
                </span>
                <span class="stat-compare">同比</span>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
      
      <el-card class="chart-card">
        <template #header>
          <h3>销售趋势</h3>
        </template>
        <div ref="salesChart" style="height: 350px;"></div>
      </el-card>
      
      <el-card class="top-products">
        <template #header>
          <h3>热销商品TOP10</h3>
        </template>
        <el-table :data="salesData.topSellingProducts">
          <el-table-column prop="productName" label="商品名称" />
          <el-table-column prop="quantitySold" label="销量" width="120" />
          <el-table-column label="销售额" width="150">
            <template #default="{row}">¥{{ row.totalRevenue.toFixed(2) }}</template>
          </el-table-column>
          <el-table-column label="占比" width="150">
            <template #default="{row}">
              <el-progress 
                :percentage="(row.totalRevenue / salesData.totalSales * 100).toFixed(1)" 
                :show-text="false"
              />
              {{ (row.totalRevenue / salesData.totalSales * 100).toFixed(1) }}%
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'
import { getSalesData } from '@/api/merchant'

const dateRange = ref([new Date(new Date().setDate(1)), new Date()])
const salesData = ref({
  totalSales: 0,
  totalOrders: 0,
  totalProductsSold: 0,
  avgOrderValue: 0,
  salesTrend: 0,
  ordersTrend: 0,
  productsTrend: 0,
  avgOrderTrend: 0,
  topSellingProducts: []
})
const salesChart = ref(null)
let chartInstance = null

const fetchSalesData = async () => {
  try {
    const [start, end] = dateRange.value
    const params = {
      startDate: start.toISOString().split('T')[0],
      endDate: end.toISOString().split('T')[0]
    }
    const res = await getSalesData(params)
    salesData.value = res
    initChart()
  } catch (error) {
    console.error('获取销售数据失败:', error)
  }
}

const initChart = () => {
  if (!salesChart.value) return
  
  if (chartInstance) {
    chartInstance.dispose()
  }
  
  chartInstance = echarts.init(salesChart.value)
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    legend: {
      data: ['销售额', '订单量']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: ['1月', '2月', '3月', '4月', '5月', '6月', '7月']
    },
    yAxis: [
      {
        type: 'value',
        name: '销售额',
        axisLabel: {
          formatter: '¥{value}'
        }
      },
      {
        type: 'value',
        name: '订单量',
        axisLabel: {
          formatter: '{value}'
        }
      }
    ],
    series: [
      {
        name: '销售额',
        type: 'bar',
        data: [12000, 18000, 15000, 22000, 19000, 23000, 25000]
      },
      {
        name: '订单量',
        type: 'line',
        yAxisIndex: 1,
        data: [120, 180, 150, 220, 190, 230, 250]
      }
    ]
  }
  
  chartInstance.setOption(option)
  
  window.addEventListener('resize', () => {
    chartInstance.resize()
  })
}

onMounted(() => {
  fetchSalesData()
})
</script>

<style scoped>
.sales-report {
  padding: 20px;
}

.report-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  padding: 15px;
}

.stat-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 10px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 10px;
}

.stat-trend {
  font-size: 12px;
  display: flex;
  align-items: center;
}

.stat-compare {
  margin-left: 5px;
  color: #909399;
}

.chart-card {
  margin-bottom: 20px;
}

.top-products {
  margin-top: 20px;
}
</style>