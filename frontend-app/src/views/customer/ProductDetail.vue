<!-- src/views/customer/ProductDetail.vue -->
<template>
  <div class="product-detail-container" v-loading="loading">
    <el-breadcrumb separator="/">
      <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/products' }">商品中心</el-breadcrumb-item>
      <el-breadcrumb-item>{{ product.name }}</el-breadcrumb-item>
    </el-breadcrumb>

    <el-card v-if="product" class="product-detail-card">
      <el-row :gutter="40">
        <el-col :span="12">
          <img :src="product.imageUrl || 'https://via.placeholder.com/600x400.png?text=Product'" class="detail-image"/>
        </el-col>
        <el-col :span="12" class="product-content">
          <h1>{{ product.name }}</h1>
          <p class="description">{{ product.description }}</p>
          <div class="price-section">
            <span class="label">价格:</span>
            <span class="price">¥{{ product.price }}</span>
          </div>
          <div class="quantity-section">
            <span class="label">数量:</span>
            <el-input-number v-model="quantity" :min="1" :max="product.stock || 99" />
          </div>
          <div class="stock-section">
            <span class="label">库存:</span>
            <span>{{ product.stock }} 件</span>
          </div>
          <div class="actions">
            <el-button type="primary" size="large" @click="handleAddToCart">加入购物车</el-button>
          </div>
        </el-col>
      </el-row>
    </el-card>
    <el-empty v-else-if="!loading" description="商品不存在"></el-empty>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { fetchProductById } from '@/api/product';
import { useCartStore } from '@/store/cart';
import { ElMessage } from 'element-plus';

const route = useRoute();
const cartStore = useCartStore();

const product = ref(null);
const loading = ref(true);
const quantity = ref(1);

onMounted(async () => {
  const productId = route.params.id;
  try {
    product.value = await fetchProductById(productId);
  } catch (error) {
    ElMessage.error('加载商品详情失败');
    console.error(error);
  } finally {
    loading.value = false;
  }
});

const handleAddToCart = () => {
  if (product.value) {
    cartStore.addItem({ ...product.value, quantity: quantity.value });
    ElMessage.success(`${product.value.name} 已加入购物车`);
  }
};
</script>

<style scoped>
.product-detail-container {
  padding: 20px;
}
.product-detail-card {
  margin-top: 20px;
}
.detail-image {
  width: 100%;
  border-radius: 4px;
}
.product-content {
  display: flex;
  flex-direction: column;
}
h1 {
  font-size: 28px;
  margin-bottom: 10px;
}
.description {
  color: #666;
  font-size: 14px;
  margin-bottom: 20px;
  line-height: 1.6;
}
.price-section, .quantity-section, .stock-section {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  font-size: 16px;
}
.label {
  width: 80px;
  color: #999;
}
.price {
  font-size: 24px;
  color: #ff4d4f;
  font-weight: bold;
}
.actions {
  margin-top: auto; /* Pushes actions to the bottom */
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}
</style>
