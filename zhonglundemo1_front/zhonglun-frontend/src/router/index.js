import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/store/modules/auth';


const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/auth/Login.vue') ,
    meta: { guestOnly: true }
  },
  {
    path: '/register/customer',
    name: 'RegisterCustomer',
    component: () => import('../views/auth/RegisterCustomer.vue'),
    meta: { guestOnly: true }
  },
  {
    path: '/admin',
    component: () => import('../components/Layout/index.vue'),
    meta: { requiresAuth: true, role: 'ADMIN' },
    children: [
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('../views/admin/Dashboard.vue')
      },
      {
        path: 'customers',
        name: 'CustomerList',
        component: () => import('../views/admin/CustomerList.vue')
      },
      {
        path: 'merchants',
        name: 'MerchantList',
        component: () => import('../views/admin/MerchantList.vue')
      }
    ]
  },
  // 404 route
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('../views/404.vue')
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

router.beforeEach(async (to, from, next) => {
  const authStore = useAuthStore()
  
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next('/login')
  } else if (to.meta.role && authStore.user.role !== to.meta.role) {
    next('/')
  } else {
    next()
  }
})

export default router