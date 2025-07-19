<template>
  <div class="merchant-list">
    <el-card>
      <template #header>
        <div class="header">
          <span>商家管理</span>
        </div>
      </template>
      
      <el-table :data="merchants" style="width: 100%">
        <el-table-column prop="businessName" label="商家名称" />
        <el-table-column prop="contactPerson" label="联系人" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="phone" label="电话" />
        <el-table-column label="状态">
          <template #default="{row}">
            <el-tag :type="row.approved ? 'success' : 'warning'">
              {{ row.approved ? '已批准' : '待审批' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="{row}">
            <el-button 
              size="small" 
              :type="row.approved ? 'danger' : 'success'" 
              @click="toggleApproval(row.id, !row.approved)"
            >
              {{ row.approved ? '撤销' : '批准' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        layout="prev, pager, next"
        @current-change="fetchMerchants"
      />
    </el-card>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { getAllMerchants, approveMerchant } from '@/api/admin'

export default {
  setup() {
    const merchants = ref([])
    const pagination = ref({
      current: 1,
      size: 10,
      total: 0
    })

    const fetchMerchants = async () => {
      try {
        const response = await getAllMerchants({
          page: pagination.value.current - 1,
          size: pagination.value.size
        })
        merchants.value = response.content
        pagination.value.total = response.totalElements
      } catch (error) {
        console.error('Failed to fetch merchants:', error)
      }
    }

    const toggleApproval = async (id, approved) => {
      try {
        await approveMerchant(id, approved)
        fetchMerchants()
      } catch (error) {
        console.error('Failed to update merchant approval:', error)
      }
    }

    onMounted(fetchMerchants)

    return {
      merchants,
      pagination,
      fetchMerchants,
      toggleApproval
    }
  }
}
</script>

<style scoped>
.merchant-list {
  padding: 20px;
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>