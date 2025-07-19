import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getActiveProducts } from '../api/customer'


export const useProductStore = defineStore('product', () => {
  const products = ref([])
  const loading = ref(false)
  const pagination = ref({
    current: 1,
    size: 12,
    total: 0
  })

  const fetchProducts = async (params = {}) => {
    try {
      loading.value = true
      const res = await getActiveProducts({
        page: pagination.value.current - 1,
        size: pagination.value.size,
        ...params
      })
      products.value = res.content
      pagination.value.total = res.totalElements
    } catch (error) {
      console.error('Failed to fetch products:', error)
    } finally {
      loading.value = false
    }
  }

  return {
    products,
    loading,
    pagination,
    fetchProducts
  }
})