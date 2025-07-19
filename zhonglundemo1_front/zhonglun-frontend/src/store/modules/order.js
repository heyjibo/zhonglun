import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getCustomerOrders } from '@/api/customer'
import { defineStore } from 'pinia'

export const useOrderStore = defineStore('order', () => {
  const orders = ref([])
  const loading = ref(false)
  const pagination = ref({
    current: 1,
    size: 10,
    total: 0
  })

  const fetchOrders = async (params = {}) => {
    try {
      loading.value = true
      const res = await getCustomerOrders({
        page: pagination.value.current - 1,
        size: pagination.value.size,
        ...params
      })
      orders.value = res.content
      pagination.value.total = res.totalElements
    } catch (error) {
      console.error('Failed to fetch orders:', error)
    } finally {
      loading.value = false
    }
  }

  return {
    orders,
    loading,
    pagination,
    fetchOrders
  }
})