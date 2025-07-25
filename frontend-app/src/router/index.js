import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from '../store/auth';

/* --- 布局组件 --- */
// 这些是包含侧边栏/导航栏的骨架组件，内部的 <router-view> 将被子路由填充。
import CustomerLayout from '../layouts/CustomerLayout.vue';
import MerchantLayout from '../layouts/MerchantLayout.vue';
import PlatformLayout from '../layouts/PlatformLayout.vue';

/* --- 公共页面 (登录/注册) --- */
import CustomerLogin from '../views/customer/Login.vue';
import CustomerRegister from '../views/customer/Register.vue';
import MerchantLogin from '../views/merchant/Login.vue';
import MerchantRegister from '../views/merchant/Register.vue';
import PlatformLogin from '../views/platform/Login.vue';

/* --- 顾客端页面 --- */
import CustomerHome from '../views/customer/Home.vue';
import CustomerProductDetail from '../views/customer/ProductDetail.vue';
import CustomerCart from '../views/customer/Cart.vue';
import CustomerCheckout from '../views/customer/Checkout.vue';
import CustomerProfile from '../views/customer/Profile.vue';
import CustomerOrders from '../views/customer/Orders.vue';

/* --- 商家端页面 --- */
import MerchantDashboard from '../views/merchant/Dashboard.vue';
import MerchantProducts from '../views/merchant/Products.vue';
import MerchantOrders from '../views/merchant/Orders.vue';

/* --- 平台端页面 --- */
import PlatformDashboard from '../views/platform/Dashboard.vue';
import PlatformMerchants from '../views/platform/Merchants.vue';
import PlatformProducts from '../views/platform/Products.vue';
import PlatformOrders from '../views/platform/Orders.vue';

const routes = [
    // 默认根路径重定向到顾客首页
    { path: '/', redirect: '/customer/home' },

    // ===============================================
    //               公共路由 (无需登录)
    // ===============================================
    { path: '/customer/login', component: CustomerLogin, name: 'CustomerLogin' },
    { path: '/customer/register', component: CustomerRegister, name: 'CustomerRegister' },
    { path: '/merchant/login', component: MerchantLogin, name: 'MerchantLogin' },
    { path: '/merchant/register', component: MerchantRegister, name: 'MerchantRegister' },
    { path: '/platform/login', component: PlatformLogin, name: 'PlatformLogin' },


    // ===============================================
    //               顾客端 (需要顾客角色)
    // ===============================================
    {
        path: '/customer',
        component: CustomerLayout,
        meta: { requiresAuth: true, role: 'ROLE_CUSTOMER' },
        children: [
            { path: 'home', component: CustomerHome, name: 'CustomerHome' },
            { path: 'product/:id', component: CustomerProductDetail, name: 'CustomerProductDetail', props: true },
            { path: 'cart', component: CustomerCart, name: 'CustomerCart' },
            { path: 'checkout', component: CustomerCheckout, name: 'CustomerCheckout' },
            { path: 'profile', component: CustomerProfile, name: 'CustomerProfile' },
            { path: 'orders', component: CustomerOrders, name: 'CustomerOrders' },
        ]
    },

    // ===============================================
    //               商家端 (需要商家角色)
    // ===============================================
    {
        path: '/merchant',
        component: MerchantLayout,
        meta: { requiresAuth: true, role: 'ROLE_MERCHANT' },
        children: [
            { path: 'dashboard', component: MerchantDashboard, name: 'MerchantDashboard' },
            { path: 'products', component: MerchantProducts, name: 'MerchantProducts' },
            { path: 'orders', component: MerchantOrders, name: 'MerchantOrders' },
        ]
    },

    // ===============================================
    //               平台端 (需要平台角色)
    // ===============================================
    {
        path: '/platform',
        component: PlatformLayout,
        meta: { requiresAuth: true, role: 'ROLE_PLATFORM' },
        children: [
            { path: 'dashboard', component: PlatformDashboard, name: 'PlatformDashboard' },
            { path: 'merchants', component: PlatformMerchants, name: 'PlatformMerchants' },
            { path: 'products', component: PlatformProducts, name: 'PlatformProducts' },
            { path: 'orders', component: PlatformOrders, name: 'PlatformOrders' },
        ]
    },

    // 404 页面 (可选)
    // { path: '/:pathMatch(.*)*', name: 'NotFound', component: NotFoundComponent },
];

const router = createRouter({
    history: createWebHistory(),
    routes,
});

/**
 * 全局前置导航守卫
 * 这是实现路由权限控制的核心。
 * 在每次路由跳转之前，都会执行此函数。
 */
router.beforeEach((to, from, next) => {
    const authStore = useAuthStore();
    const { requiresAuth, role: requiredRole } = to.meta;

    // 检查目标路由是否需要认证
    if (requiresAuth) {
        // 如果需要认证，但用户未登录
        if (!authStore.isLoggedIn) {
            // 根据目标路由所需的角色，重定向到对应的登录页
            switch (requiredRole) {
                case 'ROLE_MERCHANT':
                    next({ name: 'MerchantLogin' });
                    break;
                case 'ROLE_PLATFORM':
                    next({ name: 'PlatformLogin' });
                    break;
                default: // 默认为顾客
                    next({ name: 'CustomerLogin' });
            }
        } else {
            // 用户已登录，但角色不匹配
            if (requiredRole && authStore.userRole !== requiredRole) {
                // 这是一个重要的安全措施：如果角色不符，强制用户登出，
                // 防止例如商家用户通过URL访问平台页面。
                // authStore.logout() 会自动重定向到正确的登录页。
                authStore.logout();
            } else {
                // 用户已登录且角色匹配，允许访问
                next();
            }
        }
    } else {
        // 如果目标路由不需要认证，直接放行
        next();
    }
});

export default router;

