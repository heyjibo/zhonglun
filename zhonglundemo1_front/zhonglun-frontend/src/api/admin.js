import request from '@/utils/request'
import { defineStore } from 'pinia'

export const getAllCustomers = (params) => {
  return request({
    url: '/api/admin/customers',
    method: 'get',
    params
  })
}

export const getAllMerchants = (params) => {
  return request({
    url: '/api/admin/merchants',
    method: 'get',
    params
  })
}

export const approveMerchant = (id, approved) => {
  return request({
    url: `/api/admin/merchants/${id}/approve`,
    method: 'patch',
    params: { approved }
  })
}