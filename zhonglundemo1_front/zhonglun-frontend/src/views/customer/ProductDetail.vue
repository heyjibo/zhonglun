<template>
  <div class="product-detail">
    <el-card>
      <el-row :gutter="40">
        <el-col :span="12">
          <el-image 
            :src="product.image || 'https://via.placeholder.com/500'" 
            fit="contain"
            class="product-image"
          />
        </el-col>
        <el-col :span="12">
          <h1 class="product-title">{{ product.name }}</h1>
          <div class="product-price">
            <span class="current-price">¥{{ product.price.toFixed(2) }}</span>
          </div>
          
          <el-divider />
          
          <div class="product-stock">
            <el-tag :type="product.stock > 0 ? 'success' : 'danger'">
              {{ product.stock > 0 ? '有货' : '缺货' }}
            </el-tag>
            <span v-if="product.stock > 0" class="stock-text">
              库存 {{ product.stock }} 件
            </span>
          </div>
          
          <div class="product-merchant">
            商家：{{ product.merchant?.businessName || '系统商家' }}
          </div>
          
          <div class="product-actions">
            <el-input-number 
              v-model="quantity" 
              :min="1" 
              :max="product.stock"
              :disabled="product.stock <= 0"
            />
            <el-button 
              type="primary" 
              @click="addToCart"
              :disabled="product.stock <= 0"
            >
              {{ product.stock > 0 ? '加入购物车' : '已售罄' }}
            </el-button>
          </div>
          
          <el-divider />
          
          <div class="product-desc">
            <h3>商品描述</h3>
            <p>{{ product.description }}</p>
          </div>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getProductById } from '@/api/customer'
import { useCartStore } from '@/store/modules/cart'
import { ElMessage } from 'element-plus'

const route = useRoute()
const cartStore = useCartStore()
const product = ref({
  id: 0,
  name: '',
  description: '',
  price: 0,
  stock: 0,
  merchant: {}
})
const quantity = ref(1)
const loading = ref(false)

const fetchProduct = async () => {
  try {
    loading.value = true
    const res = await getProductById(route.params.id)
    product.value = res
  } catch (error) {
    ElMessage.error('获取商品详情失败')
  } finally {
    loading.value = false
  }
}

const addToCart = () => {
  cartStore.addItem({
    productId: product.value.id,
    name: product.value.name,
    price: product.value.price,
    quantity: quantity.value
  })
  ElMessage.success('已加入购物车')
}

onMounted(fetchProduct)
</script>

<style scoped>
.product-detail {
  padding: 20px;
}

.product-image {
  width: 100%;
  height: 400px;
  border: 1px solid #eee;
  border-radius: 4px;
}

.product-title {
  font-size: 24px;
  margin-bottom: 20px;
}

.current-price {
  font-size: 28px;
  color: #f56c6c;
  font-weight: bold;
}

.product-stock {
  margin: 20px 0;
}

.stock-text {
  margin-left: 10px;
  color: #666;
}

.product-merchant {
  color: #666;
  margin-bottom: 20px;
}

.product-actions {
  margin: 30px 0;
  display: flex;
  align-items: center;
}

.product-actions .el-input-number {
  margin-right: 20px;
}

.product-actions .el-button {
  width: 150px;
}

.product-desc {
  margin-top: 30px;
}

.product-desc h3 {
  margin-bottom: 15px;
}

.product-desc p {
  line-height: 1.8;
  color: #666;
}
</style>