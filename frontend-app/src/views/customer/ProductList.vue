<!-- src/views/customer/ProductList.vue -->
<template>
  <div>
    <h1>商品中心</h1>
    <el-row :gutter="20" v-loading="loading">
      <el-col :span="6" v-for="product in productList" :key="product.id" style="margin-bottom: 20px;">
        <el-card shadow="hover" class="product-card" @click="goToDetail(product.id)">
          <img :src="product.imageUrl || 'https://via.placeholder.com/300x200.png?text=Product'" class="product-image" />
          <div class="product-info">
            <h3 class="product-name">{{ product.name }}</h3>
            <p class="product-description">{{ product.description }}</p>
            <div class="bottom">
              <span class="product-price">¥{{ product.price }}</span>
              <el-button type="primary" plain @click.stop="addToCart(product)">加入购物车</el-button>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-empty v-if="!loading && productList.length === 0" description="暂无商品"></el-empty>

    <el-pagination
        v-if="total > 0"
        background
        layout="prev, pager, next, jumper, ->, total"
        :total="total"
        :page-size="queryParams.size"
        :current-page="queryParams.page"
        @current-change="handlePageChange"
        style="margin-top: 20px; justify-content: center;"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { fetchProducts } from '@/api/product';
import { useCartStore } from '@/store/cart'; // 假设已创建
import { ElMessage } from 'element-plus';

const router = useRouter();
const cartStore = useCartStore();

const productList = ref([]);
const loading = ref(true);
const total = ref(0);
const queryParams = reactive({
  page: 1,
  size: 8,
});

const getProducts = async () => {
  loading.value = true;
  try {
    const data = await fetchProducts(queryParams);
    productList.value = data.items;
    total.value = data.total;
  } catch (error) {
    ElMessage.error('获取商品列表失败');
  } finally {
    loading.value = false;
  }
};

onMounted(getProducts);

const handlePageChange = (newPage) => {
  queryParams.page = newPage;
  getProducts();
};

const goToDetail = (id) => {
  router.push(`/products/${id}`);
};

const addToCart = (product) => {
  cartStore.addItem({ ...product, quantity: 1 });
  ElMessage.success(`${product.name} 已加入购物车`);
};
</script>

<style scoped>
/* 复用 Home.vue 的部分样式，可以考虑提取到全局 CSS */
.product-card {
  cursor: pointer;
  display: flex;
  flex-direction: column;
  height: 100%;
}
.product-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
}
.product-info {
  padding: 14px;
  flex-grow: 1;
  display: flex;
  flex-direction: column;
}
.product-name {
  font-size: 16px;
}
.product-description {
  color: #999;
  font-size: 12px;
  margin: 5px 0 15px;
  flex-grow: 1;
}
.bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.product-price {
  font-size: 18px;
  color: #ff4d4f;
  font-weight: bold;
}
</style>
