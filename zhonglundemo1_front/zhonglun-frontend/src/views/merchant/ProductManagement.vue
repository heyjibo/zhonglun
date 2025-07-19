<template>
  <div class="product-management">
    <el-card shadow="never">
      <template #header>
        <div class="table-header">
          <h3>商品管理</h3>
          <div>
            <el-button type="primary" @click="handleAdd" icon="Plus">
              新增商品
            </el-button>
            <el-button @click="refresh" icon="Refresh">
              刷新
            </el-button>
          </div>
        </div>
      </template>
      
      <el-table :data="products" v-loading="loading" border>
        <el-table-column prop="name" label="商品名称" width="180" />
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column label="价格" width="120">
          <template #default="{row}">¥{{ row.price.toFixed(2) }}</template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="100" />
        <el-table-column label="状态" width="100">
          <template #default="{row}">
            <el-switch
              v-model="row.active"
              active-color="#13ce66"
              inactive-color="#ff4949"
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{row}">
            <el-button size="small" @click="handleEdit(row)" icon="Edit" />
            <el-popconfirm
              title="确认删除此商品吗？"
              @confirm="handleDelete(row.id)"
            >
              <template #reference>
                <el-button size="small" type="danger" icon="Delete" />
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { 
  getProductsByMerchant, 
  updateProductStatus,
  deleteProduct 
} from '@/api/merchant'

const router = useRouter()
const products = ref([])
const loading = ref(false)
const pagination = ref({
  current: 1,
  size: 10,
  total: 0
})

const fetchProducts = async () => {
  loading.value = true
  try {
    const res = await getProductsByMerchant({
      page: pagination.value.current - 1,
      size: pagination.value.size
    })
    products.value = res.content
    pagination.value.total = res.totalElements
  } catch (error) {
    ElMessage.error('获取商品列表失败')
  } finally {
    loading.value = false
  }
}

const handleStatusChange = async (product) => {
  try {
    await updateProductStatus(product.id, product.active)
    ElMessage.success(`已${product.active ? '上架' : '下架'}商品`)
  } catch (error) {
    product.active = !product.active // 回滚状态
    ElMessage.error('操作失败')
  }
}

const handleAdd = () => {
  router.push('/merchant/products/add')
}

const handleEdit = (product) => {
  router.push(`/merchant/products/edit/${product.id}`)
}

const handleDelete = async (id) => {
  try {
    await deleteProduct(id)
    ElMessage.success('删除成功')
    fetchProducts()
  } catch (error) {
    ElMessage.error('删除失败')
  }
}

const handleSizeChange = (val) => {
  pagination.value.size = val
  fetchProducts()
}

const handleCurrentChange = (val) => {
  pagination.value.current = val
  fetchProducts()
}

const refresh = () => {
  pagination.value.current = 1
  fetchProducts()
}

onMounted(fetchProducts)
</script>

<style scoped>
.product-management {
  padding: 20px;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>