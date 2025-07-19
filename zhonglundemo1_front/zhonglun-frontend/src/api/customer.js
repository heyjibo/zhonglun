import request from '@/utils/request'
import { defineStore } from 'pinia'

export const getActiveProducts = (params) => {
  return request({
    url: '/api/customer/products',
    method: 'get',
    params
  })
}

export const getProductById = (id) => {
  return request({
    url: `/api/customer/products/${id}`,
    method: 'get'
  })
}

export const getCustomerOrders = (params) => {
  return request({
    url: '/api/customer/orders',
    method: 'get',
    params
  })
}

export const createOrder = (data) => {
  return request({
    url: '/api/customer/orders',
    method: 'post',
    data
  })
}

export const getCustomerProfile = () => {
  return request({
    url: '/api/customer/profile',
    method: 'get'
  })
}