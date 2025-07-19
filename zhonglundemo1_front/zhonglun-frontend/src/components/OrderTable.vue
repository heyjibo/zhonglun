<template>
  <el-table :data="orders" style="width: 100%" v-loading="loading">
    <el-table-column prop="orderNumber" label="订单号" width="180" />
    <el-table-column label="下单时间" width="180">
      <template #default="{row}">
        {{ formatDate(row.orderDate) }}
      </template>
    </el-table-column>
    <el-table-column label="订单状态" width="120">
      <template #default="{row}">
        <el-tag :type="statusTagType(row.status)">
          {{ statusText(row.status) }}
        </el-tag>
      </template>
    </el-table-column>
    <el-table-column label="总金额" width="120">
      <template #default="{row}">
        ¥{{ row.totalAmount.toFixed(2) }}
      </template>
    </el-table-column>
    <el-table-column label="操作" width="120" fixed="right">
      <template #default="{row}">
        <el-button 
          size="small" 
          @click="handleView(row.id)"
        >
          详情
        </el-button>
      </template>
    </el-table-column>
  </el-table>
</template>

<script setup>
import { formatDate } from '@/utils/date'

const props = defineProps({
  orders: {
    type: Array,
    default: () => []
  },
  loading: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['view'])

const statusText = (status) => {
  const statusMap = {
    PENDING: '待处理',
    PROCESSING: '处理中',
    SHIPPED: '已发货',
    DELIVERED: '已完成',
    CANCELLED: '已取消'
  }
  return statusMap[status] || status
}

const statusTagType = (status) => {
  const typeMap = {
    PENDING: 'warning',
    PROCESSING: 'primary',
    SHIPPED: '',
    DELIVERED: 'success',
    CANCELLED: 'danger'
  }
  return typeMap[status] || 'info'
}

const handleView = (orderId) => {
  emit('view', orderId)
}
</script>