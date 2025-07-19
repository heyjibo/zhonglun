<template>
  <div class="add-product">
    <el-card>
      <template #header>
        <div class="header">
          <span>添加新商品</span>
          <el-button @click="$router.go(-1)">返回</el-button>
        </div>
      </template>
      
      <el-form 
        :model="form" 
        :rules="rules" 
        ref="productForm"
        label-width="120px"
      >
        <el-form-item label="商品名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入商品名称" />
        </el-form-item>
        
        <el-form-item label="商品描述" prop="description">
          <el-input 
            v-model="form.description" 
            type="textarea" 
            :rows="4"
            placeholder="请输入商品详细描述"
          />
        </el-form-item>
        
        <el-form-item label="价格" prop="price">
          <el-input-number 
            v-model="form.price" 
            :min="0.01" 
            :precision="2"
            :step="0.1"
          />
        </el-form-item>
        
        <el-form-item label="库存数量" prop="stock">
          <el-input-number v-model="form.stock" :min="0" />
        </el-form-item>
        
        <el-form-item label="商品图片">
          <el-upload
            action="/api/upload"
            list-type="picture-card"
            :on-success="handleUploadSuccess"
            :before-upload="beforeUpload"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="submitForm" :loading="loading">
            提交
          </el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { createProduct } from '@/api/merchant'
import { ElMessage } from 'element-plus'

const router = useRouter()
const form = ref({
  name: '',
  description: '',
  price: 0,
  stock: 0,
  image: ''
})
const loading = ref(false)

const rules = {
  name: [
    { required: true, message: '请输入商品名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在2到50个字符', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入商品描述', trigger: 'blur' }
  ],
  price: [
    { required: true, message: '请输入价格', trigger: 'blur' }
  ],
  stock: [
    { required: true, message: '请输入库存数量', trigger: 'blur' }
  ]
}

const handleUploadSuccess = (response) => {
  form.value.image = response.data.url
}

const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件')
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过2MB')
  }

  return isImage && isLt2M
}

const submitForm = async () => {
  try {
    loading.value = true
    await createProduct(form.value)
    ElMessage.success('商品添加成功')
    router.push('/merchant/products')
  } catch (error) {
    ElMessage.error('添加商品失败')
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  form.value = {
    name: '',
    description: '',
    price: 0,
    stock: 0,
    image: ''
  }
}
</script>

<style scoped>
.add-product {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.el-input-number {
  width: 200px;
}
</style>