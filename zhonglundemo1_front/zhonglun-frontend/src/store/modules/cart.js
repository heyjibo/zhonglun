import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useCartStore = defineStore('cart', () => {
  const items = ref([])
  
  const totalItems = computed(() => 
    items.value.reduce((total, item) => total + item.quantity, 0)
  )
  
  const totalAmount = computed(() =>
    items.value.reduce((total, item) => 
      total + (item.price * item.quantity), 0)
  )
  
  const addItem = (newItem) => {
    const existingItem = items.value.find(
      item => item.productId === newItem.productId
    )
    
    if (existingItem) {
      existingItem.quantity += newItem.quantity
    } else {
      items.value.push({ ...newItem })
    }
  }
  
  const updateQuantity = (productId, quantity) => {
    const item = items.value.find(item => item.productId === productId)
    if (item) item.quantity = quantity
  }
  
  const removeItem = (productId) => {
    items.value = items.value.filter(item => item.productId !== productId)
  }
  
  const clearCart = () => {
    items.value = []
  }
  
  return {
    items,
    totalItems,
    totalAmount,
    addItem,
    updateQuantity,
    removeItem,
    clearCart
  }
})