import { defineStore } from 'pinia';
import { getToken, setToken, removeToken } from '@/utils/auth';
import { login as apiLogin, registerCustomer, getMyInfo } from '@/api/auth'; // 引入 getMyInfo

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: getToken(),
    user: null, // 从 localStorage 移到这里
    loading: false,
  }),
  getters: {
    isLoggedIn: (state) => !!state.token,
    isAdmin: (state) => state.user?.role === 'admin',
    isMerchant: (state) => state.user?.role === 'merchant',
    isCustomer: (state) => state.user?.role === 'customer',
  },
  actions: {
    // 登录 Action
    async login(credentials) {
      this.loading = true;
      try {
        const response = await apiLogin(credentials);
        const { token, user } = response.data; // 假设登录接口返回 token 和 user
        this.token = token;
        this.user = user; // 直接设置 user state
        setToken(token); // 只在 localStorage 中存储 token
        return Promise.resolve(response);
      } catch (error) {
        return Promise.reject(error);
      } finally {
        this.loading = false;
      }
    },

    async fetchUser() {
      if (!this.token) return; // 如果没有token，直接返回
      try {
        const response = await getMyInfo();
        this.user = response.data;
      } catch (error) {
        // 如果 token 失效，获取用户信息会失败（通常是 401）
        console.error('Failed to fetch user info, logging out.', error);
        this.logout(); // 自动登出
      }
    },

    async logout() {
      try {
        await logout()
        this.token = null;
        this.user = null;
        removeToken();
        localStorage.removeItem('user')
        return Promise.resolve()
      } catch (error) {
        return Promise.reject(error)
      }
    }
  }
})