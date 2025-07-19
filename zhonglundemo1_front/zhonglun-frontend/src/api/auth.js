import request from '@/utils/request'
import { defineStore } from 'pinia'



export const login = (data) => request({
    return request({
    url: '/api/auth/login',
    method: 'post',
    data
  })
})


export const registerCustomer = (data) => {
  return request({
    url: '/api/auth/customer/register',
    method: 'post',
    data
  })
}

export const registerMerchant = (data) => {
  return request({
    url: '/api/auth/merchant/register',
    method: 'post',
    data
  })
}

export const getCurrentUser = () => {
  return request({
    url: '/api/auth/me',
    method: 'get'
  })
}