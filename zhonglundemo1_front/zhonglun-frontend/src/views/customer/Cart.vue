<template>
  <div class="cart-page">
    <el-card>
      <template #header>
        <div class="cart-header">
          <h2>我的购物车</h2>
          <span>共 {{ cartStore.totalItems }} 件商品</span>
        </div>
      </template>
      
      <el-table :data="cartStore.items" empty-text="购物车为空">
        <el-table-column prop="name" label="商品名称" />
        <el-table-column label="单价" width="120">
          <template #default="{row}">¥{{ row.price.toFixed(2) }}</template>
        </el-table-column>
        <el-table-column label="数量" width="150">
          <template #default="{row}">
            <el-input-number 
              v-model="row.quantity" 
              :min="1" 
              :max="getProductStock(row.productId)"
              @change="cartStore.updateQuantity(row.productId, row.quantity)"
            />
          </template>
        </el-table-column>
        <el-table-column label="小计" width="120">
          <template #default="{row}">¥{{ (row.price * row.quantity).toFixed(2) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="80">
          <template #default="{row}">
            <el-button 
              type="danger" 
              icon="Delete" 
              circle 
              @click="cartStore.removeItem(row.productId)"
            />
          </template>
        </el-table-column>
      </el-table>
      
      <div class="cart-footer">
        <div class="total-amount">
          合计：<span class="price">¥{{ cartStore.totalAmount.toFixed(2) }}</span>
        </div>
        <el-button 
          type="primary" 
          size="large" 
          @click="handleCheckout"
          :disabled="cartStore.items.length === 0"
        >
          去结算
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { useCartStore } from '@/store/modules/cart'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const cartStore = useCartStore()
const router = useRouter()

const getProductStock = (productId) => {
  // 实际项目中应从商品数据获取库存
  return 99
}

const handleCheckout = () => {
  if (cartStore.items.length === 0) {
    ElMessage.warning('购物车为空')
    return
  }
  router.push('/customer/checkout')
}
</script>

<style scoped>
.cart-page {
  padding: 20px;
}

.cart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.cart-footer {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 30px;
}

.total-amount {
  font-size: 18px;
}

.price {
  font-size: 24px;
  color: #f56c6c;
  font-weight: bold;
}

.el-button {
  width: 200px;
}
</style>