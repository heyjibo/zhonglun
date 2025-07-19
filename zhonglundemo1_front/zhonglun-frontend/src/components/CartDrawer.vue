<template>
  <el-drawer v-model="visible" title="购物车" size="400px">
    <div class="cart-content">
      <el-table :data="items" empty-text="购物车为空" show-summary>
        <el-table-column prop="name" label="商品" />
        <el-table-column label="单价" width="100">
          <template #default="{row}">¥{{ row.price.toFixed(2) }}</template>
        </el-table-column>
        <el-table-column label="数量" width="120">
          <template #default="{row}">
            <el-input-number 
              v-model="row.quantity" 
              :min="1" 
              size="small"
              @change="updateQuantity(row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="60">
          <template #default="{row}">
            <el-button 
              type="danger" 
              size="small" 
              icon="Delete"
              @click="removeItem(row.productId)"
            />
          </template>
        </el-table-column>
      </el-table>
      
      <div class="checkout-actions">
        <el-button 
          type="primary" 
          size="large" 
          @click="handleCheckout"
          :disabled="items.length === 0"
        >
          去结算 (¥{{ totalAmount.toFixed(2) }})
        </el-button>
      </div>
    </div>
  </el-drawer>
</template>

<script setup>
import { computed } from 'vue'
import { useCartStore } from '@/store/modules/cart'
import { ElMessage } from 'element-plus'

const cartStore = useCartStore()
const visible = ref(false)

const items = computed(() => cartStore.items)
const totalAmount = computed(() => cartStore.totalAmount)

const updateQuantity = (item) => {
  cartStore.updateQuantity(item.productId, item.quantity)
}

const removeItem = (productId) => {
  cartStore.removeItem(productId)
  ElMessage.success('已移除商品')
}

const handleCheckout = () => {
  // 跳转到结算页面
  visible.value = false
  router.push('/customer/checkout')
}

defineExpose({
  open: () => visible.value = true,
  close: () => visible.value = false
})
</script>

<style scoped>
.cart-content {
  padding: 20px;
  display: flex;
  flex-direction: column;
  height: 100%;
}

.checkout-actions {
  margin-top: 20px;
  text-align: center;
}

.el-button {
  width: 100%;
}
</style>