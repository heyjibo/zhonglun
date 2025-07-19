<template>
  <el-card class="product-card" shadow="hover" @click="$emit('view-detail', product.id)">
    <template #header>
      <div class="card-header">
        <span>{{ product.name }}</span>
        <el-tag v-if="product.stock <= 10" type="danger" size="small">
          仅剩{{ product.stock }}件
        </el-tag>
      </div>
    </template>
    
    <el-image 
      :src="product.image || 'https://via.placeholder.com/200'" 
      fit="cover"
      class="product-image"
    />
    
    <div class="product-info">
      <div class="price">¥{{ product.price.toFixed(2) }}</div>
      <div class="view-count">
        <el-icon><View /></el-icon>
        {{ product.viewCount }}次浏览
      </div>
    </div>
    
    <template #footer>
      <el-button 
        type="primary" 
        size="small" 
        @click.stop="$emit('add-to-cart', product)"
        :disabled="product.stock <= 0"
      >
        {{ product.stock > 0 ? '加入购物车' : '已售罄' }}
      </el-button>
    </template>
  </el-card>
</template>

<script setup>
defineProps({
  product: {
    type: Object,
    required: true
  }
})

defineEmits(['add-to-cart', 'view-detail'])
</script>

<style scoped>
.product-card {
  margin-bottom: 20px;
  cursor: pointer;
  transition: transform 0.3s;
}

.product-card:hover {
  transform: translateY(-5px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.product-image {
  width: 100%;
  height: 180px;
  display: block;
}

.product-info {
  margin-top: 10px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price {
  font-size: 18px;
  font-weight: bold;
  color: #f56c6c;
}

.view-count {
  font-size: 12px;
  color: #909399;
  display: flex;
  align-items: center;
}

.el-icon {
  margin-right: 5px;
}
</style>