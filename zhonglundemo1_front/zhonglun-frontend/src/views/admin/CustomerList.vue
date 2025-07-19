<template>
  <div class="customer-list">
    <el-card>
      <template #header>
        <div class="table-header">
          <h3>客户管理</h3>
          <div class="search-bar">
            <el-input
              v-model="searchQuery"
              placeholder="搜索客户姓名/邮箱"
              clearable
              style="width: 300px"
              @clear="fetchCustomers"
              @keyup.enter="fetchCustomers"
            />
            <el-button type="primary" @click="fetchCustomers" icon="Search">
              搜索
            </el-button>
          </div>
        </div>
      </template>
      
      <el-table :data="customers" v-loading="loading" border>
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="email" label="邮箱" width="200" />
        <el-table-column prop="phone" label="电话" width="150" />
        <el-table-column prop="address" label="地址" show-overflow-tooltip />
        <el-table-column label="注册时间" width="180">
          <template #default="{row}">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{row}">
            <el-button 
              size="small" 
              type="danger" 
              @click="handleDelete(row.id)"
              icon="Delete"
            />
          </template>
        </el-table-column>
      </el-table>
      
      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCustomers, deleteCustomer } from '@/api/admin'
import { formatDate } from '@/utils/date'

const customers = ref([])
const searchQuery = ref('')
const loading = ref(false)
const pagination = ref({
  current: 1,
  size: 10,
  total: 0
})

const fetchCustomers = async () => {
  try {
    loading.value = true
    const res = await getCustomers({
      page: pagination.value.current - 1,
      size: pagination.value.size,
      query: searchQuery.value
    })
    customers.value = res.content
    pagination.value.total = res.totalElements
  } catch (error) {
    ElMessage.error('获取客户列表失败')
  } finally {
    loading.value = false
  }
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除此客户吗？', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await deleteCustomer(id)
    ElMessage.success('删除成功')
    fetchCustomers()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleSizeChange = (val) => {
  pagination.value.size = val
  fetchCustomers()
}

const handleCurrentChange = (val) => {
  pagination.value.current = val
  fetchCustomers()
}

onMounted(() => {
  fetchCustomers()
})
</script>

<style scoped>
.customer-list {
  padding: 20px;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-bar {
  display: flex;
  gap: 10px;
}

.el-pagination {
  margin-top: 20px;
  justify-content: flex-end;
}
</style>