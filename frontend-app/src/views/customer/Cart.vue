<!-- src/views/customer/Cart.vue -->
<template>
  <div class="cart-container">
    <h1>我的购物车</h1>
    <div v-if="cartStore.items.length > 0">
      <el-table :data="cartStore.items" style="width: 100%">
        <el-table-column prop="name" label="商品" width="300">
          <template #default="scope">
            <div style="display: flex; align-items: center;">
              <img :src="scope.row.imageUrl || 'https://via.placeholder.com/50'" style="width: 50px; height: 50px; margin-right: 10px;">
              <span>{{ scope.row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="price" label="单价" width="150">
          <template #default="scope">¥{{ scope.row.price }}</template>
        </el-table-column>
        <el-table-column label="数量" width="200">
          <template #default="scope">
            <el-input-number
                :model-value="scope.row.quantity"
                @change="(currentValue) => handleQuantityChange(scope.row.id, currentValue)"
                :min="1"
                :max="scope.row.stock || 99"
            />
          </template>
        </el-table-column>
        <el-table-column label="小计" width="150">
          <template #default="scope">¥{{ (scope.row.price * scope.row.quantity).toFixed(2) }}</template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="scope">
            <el-button type="danger" link @click="handleRemoveItem(scope.row.id)">移除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="cart-summary">
        <div class="total-price">
          <span>总计: </span>
          <span class="amount">¥{{ cartStore.totalPrice.toFixed(2) }}</span>
        </div>
        <el-button type="primary" size="large" @click="goToCheckout" :disabled="cartStore.items.length === 0">去结算</el-button>
      </div>
    </div>
    <el-empty v-else description="购物车还是空的，快去逛逛吧！"></el-empty>
  </div>
</template>

<script setup>
import { useCartStore } from '@/store/cart';
import { useRouter } from 'vue-router';

const cartStore = useCartStore();
const router = useRouter();

const handleQuantityChange = (productId, quantity) => {
  cartStore.updateItemQuantity(productId, quantity);
};

const handleRemoveItem = (productId) => {
  cartStore.removeItem(productId);
};

const goToCheckout = () => {
  router.push('/checkout');
};
</script>

<style scoped>
.cart-container {
  padding: 20px;
}
.cart-summary {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  padding: 20px;
  background-color: #f5f7fa;
}
.total-price {
  margin-right: 20px;
  font-size: 18px;
}
.total-price .amount {
  font-size: 24px;
  font-weight: bold;
  color: #ff4d4f;
}
</style>
