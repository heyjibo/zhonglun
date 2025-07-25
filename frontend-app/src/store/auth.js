import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { jwtDecode } from 'jwt-decode';
import router from '../router/index.js';
import { login } from '../api/auth';
import { useCartStore } from './cart.js'; // 引入cart store以便登出时清空

/**
 * 认证状态管理 (Pinia Store)
 * 负责处理用户登录、登出、Token管理和角色权限。
 */
export const useAuthStore = defineStore('auth', () => {
    // --- State ---
    const token = ref(localStorage.getItem('token'));
    const user = ref(JSON.parse(localStorage.getItem('user')) || null);

    // --- Getters (Computed Properties) ---
    const isLoggedIn = computed(() => !!token.value && !!user.value);
    const userRole = computed(() => user.value?.role || null);

    // 便捷的角色判断
    const isCustomer = computed(() => userRole.value === 'ROLE_CUSTOMER');
    const isMerchant = computed(() => userRole.value === 'ROLE_MERCHANT');
    const isPlatform = computed(() => userRole.value === 'ROLE_PLATFORM');

    // --- Actions ---

    /**
     * 内部函数，用于设置或清除用户和Token信息，并同步到localStorage
     * @param {string | null} newToken
     */
    function setUserAndToken(newToken) {
        if (newToken) {
            token.value = newToken;
            // 解码JWT以获取用户信息
            // !!! 重要: 确保您的后端JWT Payload中包含了 'id', 'sub'(username), 'role' 这些声明(claims)
            const decoded = jwtDecode(newToken);
            user.value = {
                id: decoded.id,
                username: decoded.sub,
                role: decoded.role,
            };
            localStorage.setItem('token', newToken);
            localStorage.setItem('user', JSON.stringify(user.value));
        } else {
            token.value = null;
            user.value = null;
            localStorage.removeItem('token');
            localStorage.removeItem('user');
        }
    }

    /**
     * 处理用户登录
     * @param {object} credentials - { username, password }
     */
    async function handleLogin(credentials) {
        // 调用登录API
        const response = await login(credentials);
        // 后端应返回类似 { "code": 200, "data": { "token": "ey..." } } 的结构
        // 我们的apiClient拦截器会处理外层包装，这里直接拿到data
        const newToken = response.data.token;
        if (newToken) {
            setUserAndToken(newToken);
        }
    }

    /**
     * 处理用户登出
     */
    function logout() {
        // 登出前记录当前角色，以便正确重定向
        const roleBeforeLogout = userRole.value;

        // 清空认证信息
        setUserAndToken(null);

        // 如果是顾客登出，同时清空购物车
        if (roleBeforeLogout === 'ROLE_CUSTOMER') {
            const cartStore = useCartStore();
            cartStore.clearCart();
        }

        // 根据角色重定向到对应的登录页
        switch (roleBeforeLogout) {
            case 'ROLE_MERCHANT':
                router.push('/merchant/login');
                break;
            case 'ROLE_PLATFORM':
                router.push('/platform/login');
                break;
            default:
                router.push('/customer/login');
        }
    }

    // 应用初始化时，尝试从localStorage恢复状态
    if (token.value) {
        setUserAndToken(token.value);
    }

    return {
        token,
        user,
        isLoggedIn,
        userRole,
        isCustomer,
        isMerchant,
        isPlatform,
        handleLogin,
        logout,
    };
});
