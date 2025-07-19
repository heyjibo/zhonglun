import request from '@/utils/request'
import { defineStore } from 'pinia'

export const getProductsByMerchant = (merchantId, params) => {
  return request({
    url: `/api/merchant/products?merchantId=${merchantId}`,
    method: 'get',
    params
  })
}

export const createProduct = (data) => {
  return request({
    url: '/api/merchant/products',
    method: 'post',
    data
  })
}

export const updateProduct = (id, data) => {
  return request({
    url: `/api/merchant/products/${id}`,
    method: 'put',
    data
  })
}

export const updateProductStatus = (id, active) => {
  return request({
    url: `/api/merchant/products/${id}/status`,
    method: 'patch',
    params: { active }
  })
}

export const getSalesData = (merchantId) => {
  return request({
    url: `/api/merchant/sales?merchantId=${merchantId}`,
    method: 'get'
  })
}