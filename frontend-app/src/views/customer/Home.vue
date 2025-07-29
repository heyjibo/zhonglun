<!-- src/views/customer/Home.vue -->
<template>
  <div class="home-container">
    <el-carousel height="400px" class="welcome-carousel">
      <el-carousel-item>
        <div class="carousel-item-content banner-1">
          <h1>欢迎来到我们的电商平台</h1>
          <p>发现万千好物，享受便捷购物体验</p>
          <el-button type="primary" size="large" @click="$router.push('/products')">立即选购</el-button>
        </div>
      </el-carousel-item>
      <el-carousel-item>
        <div class="carousel-item-content banner-2">
          <h1>新商家入驻，优惠多多</h1>
          <p>探索新店铺，发现独家商品</p>
        </div>
      </el-carousel-item>
    </el-carousel>

    <div class="featured-products">
      <h2>精选商品</h2>
      <el-row :gutter="20" v-if="products.length">
        <el-col :span="6" v-for="product in products" :key="product.id">
          <el-card shadow="hover" class="product-card" @click="goToDetail(product.id)">
            <img :src="product.imageUrl || 'https://via.placeholder.com/300x200.png?text=Product'" class="product-image" />
            <div class="product-info">
              <h3 class="product-name">{{ product.name }}</h3>
              <p class="product-price">¥{{ product.price }}</p>
            </div>
          </el-card>
        </el-col>
      </el-row>
      <el-empty v-else description="暂无精选商品"></el-empty>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { fetchProducts } from '@/api/product';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';

const router = useRouter();
const products = ref([]);

onMounted(async () => {
  try {
    // 获取前4个商品作为精选商品
    const data = await fetchProducts({ page: 1, size: 4 });
    products.value = data.items;
  } catch (error) {
    ElMessage.error('加载精选商品失败');
  }
});

const goToDetail = (id) => {
  router.push(`/products/${id}`);
};
</script>

<style scoped>
.home-container {
  padding: 0;
}
.welcome-carousel .carousel-item-content {
  color: white;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100%;
  text-align: center;
}
.banner-1 {
  background: linear-gradient(rgba(0,0,0,0.5), rgba(0,0,0,0.5)), url('https://images.unsplash.com/photo-1555529669-e69e7aa0ba9e?q=80&w=2070') center/cover;
}
.banner-2 {
  background: linear-gradient(rgba(0,0,0,0.5), rgba(0,0,0,0.5)), url('https://images.unsplash.com/photo-1522204523234-8729aa6e3d54?q=80&w=2070') center/cover;
}

.featured-products {
  padding: 40px 20px;
  text-align: center;
}
h2 {
  margin-bottom: 30px;
  font-size: 24px;
}
.product-card {
  cursor: pointer;
  transition: transform 0.2s;
}
.product-card:hover {
  transform: translateY(-5px);
}
.product-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
}
.product-info {
  padding: 14px;
}
.product-name {
  font-size: 16px;
  margin: 10px 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.product-price {
  font-size: 18px;
  color: #ff4d4f;
  font-weight: bold;
}
</style>
