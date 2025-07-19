<template>
  <div class="home-page">
    <el-carousel height="400px" :interval="5000">
      <el-carousel-item v-for="item in banners" :key="item.id">
        <img :src="item.image" class="banner-image" />
      </el-carousel-item>
    </el-carousel>

    <el-divider />

    <h2>热门商品</h2>
    <product-list :products="hotProducts" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import ProductList from '@/components/ProductList.vue'
import { getActiveProducts } from '@/api/customer'

const banners = ref([
  { id: 1, image: 'https://via.placeholder.com/1200x400?text=Banner+1' },
  { id: 2, image: 'https://via.placeholder.com/1200x400?text=Banner+2' },
  { id: 3, image: 'https://via.placeholder.com/1200x400?text=Banner+3' }
])

const hotProducts = ref([])

const fetchHotProducts = async () => {
  try {
    const res = await getActiveProducts({
      page: 0,
      size: 8,
      sort: 'sales,desc'
    })
    hotProducts.value = res.content
  } catch (error) {
    console.error('Failed to fetch hot products:', error)
  }
}

onMounted(fetchHotProducts)
</script>

<style scoped>
.home-page {
  padding: 20px;
}

.banner-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

h2 {
  margin: 20px 0;
  font-size: 24px;
  color: #333;
}
</style>