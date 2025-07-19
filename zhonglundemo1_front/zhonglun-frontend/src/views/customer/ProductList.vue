<template>
  <div class="product-list">
    <el-row :gutter="20">
      <el-col :span="6" v-for="product in products" :key="product.id">
        <product-card 
          :product="product" 
          @add-to-cart="handleAddToCart"
          @view-detail="handleViewDetail"
        />
      </el-col>
    </el-row>
    
    <div class="pagination-wrapper">
      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        layout="prev, pager, next"
        @current-change="fetchProducts"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import ProductCard from '@/components/ProductCard.vue'
import { getActiveProducts } from '@/api/customer'
import { useCartStore } from '@/store/modules/cart'

const router = useRouter()
const cartStore = useCartStore()
const products = ref([])
const pagination = ref({
  current: 1,
  size: 12,
  total: 0
})

const fetchProducts = async () => {
  try {
    const res = await getActiveProducts({
      page: pagination.value.current - 1,
      size: pagination.value.size
    })
    products.value = res.content
    pagination.value.total = res.totalElements
  } catch (error) {
    ElMessage.error('获取商品列表失败')
  }
}

const handleAddToCart = (product) => {
  cartStore.addItem({
    productId: product.id,
    name: product.name,
    price: product.price,
    quantity: 1
  })
  ElMessage.success('已加入购物车')
}

const handleViewDetail = (productId) => {
  router.push(`/customer/products/${productId}`)
}

onMounted(fetchProducts)
</script>

<style scoped>
.product-list {
  padding: 20px;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>