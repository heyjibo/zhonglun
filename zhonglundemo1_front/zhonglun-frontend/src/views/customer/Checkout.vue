<template>
  <div class="checkout-page">
    <el-steps :active="activeStep" finish-status="success" align-center>
      <el-step title="确认订单" />
      <el-step title="支付" />
      <el-step title="完成" />
    </el-steps>
    
    <div v-if="activeStep === 0" class="step-content">
      <el-card>
        <template #header>
          <h3>订单信息</h3>
        </template>
        
        <el-table :data="cartStore.items" border>
          <el-table-column prop="name" label="商品名称" />
          <el-table-column label="单价" width="120">
            <template #default="{row}">¥{{ row.price.toFixed(2) }}</template>
          </el-table-column>
          <el-table-column prop="quantity" label="数量" width="100" />
          <el-table-column label="小计" width="120">
            <template #default="{row}">¥{{ (row.price * row.quantity).toFixed(2) }}</template>
          </el-table-column>
        </el-table>
        
        <div class="order-summary">
          <div class="summary-row">
            <span>商品总价：</span>
            <span>¥{{ cartStore.totalAmount.toFixed(2) }}</span>
          </div>
          <div class="summary-row">
            <span>运费：</span>
            <span>¥0.00</span>
          </div>
          <div class="summary-row total">
            <span>应付总额：</span>
            <span>¥{{ cartStore.totalAmount.toFixed(2) }}</span>
          </div>
        </div>
      </el-card>
      
      <el-card class="address-card">
        <template #header>
          <div class="address-header">
            <h3>收货地址</h3>
            <el-button type="text" @click="showAddressDialog = true">修改地址</el-button>
          </div>
        </template>
        
        <div v-if="selectedAddress" class="address-info">
          <p>{{ selectedAddress.receiver }} {{ selectedAddress.phone }}</p>
          <p>{{ selectedAddress.region }} {{ selectedAddress.detail }}</p>
        </div>
        <div v-else>
          <el-empty description="暂无收货地址" />
        </div>
      </el-card>
      
      <div class="checkout-actions">
        <el-button @click="$router.go(-1)">返回购物车</el-button>
        <el-button type="primary" @click="activeStep = 1">去支付</el-button>
      </div>
    </div>
    
    <div v-if="activeStep === 1" class="step-content">
      <!-- 支付步骤内容 -->
    </div>
    
    <div v-if="activeStep === 2" class="step-content">
      <!-- 完成步骤内容 -->
    </div>
    
    <el-dialog v-model="showAddressDialog" title="选择收货地址">
      <!-- 地址选择对话框内容 -->
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useCartStore } from '@/store/modules/cart'

const cartStore = useCartStore()
const activeStep = ref(0)
const showAddressDialog = ref(false)
const selectedAddress = ref(null)

// 模拟地址数据
const addresses = ref([
  {
    id: 1,
    receiver: '张三',
    phone: '13800138000',
    region: '北京市海淀区',
    detail: '中关村大街1号',
    isDefault: true
  }
])

onMounted(() => {
  selectedAddress.value = addresses.value.find(addr => addr.isDefault)
})
</script>

<style scoped>
.checkout-page {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.step-content {
  margin-top: 30px;
}

.order-summary {
  margin-top: 20px;
  padding: 15px;
  background-color: #f9f9f9;
  border-radius: 4px;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
}

.summary-row.total {
  font-weight: bold;
  font-size: 16px;
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px solid #eee;
}

.address-card {
  margin-top: 20px;
}

.address-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.address-info {
  line-height: 1.8;
}

.checkout-actions {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
  gap: 20px;
}
</style>