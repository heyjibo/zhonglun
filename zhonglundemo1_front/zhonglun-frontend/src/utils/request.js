import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/store/modules/auth'
import router from '@/router'

const service = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 10000
})

service.interceptors.request.use(
  config => {
    const authStore = useAuthStore()
    if (authStore.token) {
      config.headers['Authorization'] = `Bearer ${authStore.token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

service.interceptors.response.use(
  response => {
    const res = response.data
    
    if (res.code && res.code !== 200) {
      ElMessage.error(res.message || 'Error')
      
      // 特殊错误码处理
      if (res.code === 401) {
        const authStore = useAuthStore()
        authStore.logout()
        router.push('/login')
      }
      
      return Promise.reject(new Error(res.message || 'Error'))
    }
    
    return res
  },
  error => {
    // HTTP状态码处理
    if (error.response) {
      switch (error.response.status) {
        case 400:
          error.message = '请求参数错误'
          break
        case 401:
          error.message = '登录已过期，请重新登录'
          const authStore = useAuthStore()
          authStore.logout()
          router.push('/login')
          break
        case 403:
          error.message = '没有权限访问此资源'
          break
        case 404:
          error.message = '请求资源不存在'
          break
        case 500:
          error.message = '服务器内部错误'
          break
        default:
          error.message = `连接错误 ${error.response.status}`
      }
    } else if (error.request) {
      error.message = '服务器无响应'
    } else {
      error.message = '请求发送失败'
    }
    
    ElMessage.error(error.message)
    return Promise.reject(error)
  }
)

export default service