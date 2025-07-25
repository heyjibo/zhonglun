import axios from 'axios';
import { useAuthStore } from '@/store/auth';
import { ElMessage } from 'element-plus';
import router from '@/router'; // 引入 router

// 1. 创建axios实例
const apiClient = axios.create({
    // VITE_API_URL 是在 .env.development 文件中配置的后端地址
    // 这样做的好处是，开发环境和生产环境可以有不同的API地址
    baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080/api',
    timeout: 10000, // 请求超时时间
});

// 2. 请求拦截器 (Request Interceptor)
apiClient.interceptors.request.use(
    (config) => {
        // 在 Pinia 初始化之后才能在模块顶层之外的地方调用 useStore()
        const authStore = useAuthStore();

        // 如果存在Token，则为每个请求头附加Token
        if (authStore.token) {
            config.headers.Authorization = `Bearer ${authStore.token}`;
        }
        return config;
    },
    (error) => {
        // 对请求错误做些什么
        console.error('Request Error:', error);
        return Promise.reject(error);
    }
);

// 3. 响应拦截器 (Response Interceptor)
apiClient.interceptors.response.use(
    (response) => {
        // 后端返回的数据结构通常是 { code: 200, message: 'success', data: { ... } }
        const res = response.data;

        // 如果 code 不是 200，则判定为错误
        if (res.code !== 200) {
            ElMessage({
                message: res.message || 'Error',
                type: 'error',
                duration: 5 * 1000,
            });

            // 特殊错误码处理，例如：Token失效、无权限等
            // 假设 401 是Token无效或过期的状态码
            if (res.code === 401) {
                const authStore = useAuthStore();
                authStore.logout(); // 调用登出 action，清空状态并重定向到登录页
            }

            return Promise.reject(new Error(res.message || 'Error'));
        } else {
            // 如果 code 是 200，直接返回核心数据 data
            return res.data;
        }
    },
    (error) => {
        // 处理网络错误等 HTTP 状态码非 2xx 的情况
        console.error('Response Error:', error);
        let message = error.message;

        if (error.response) {
            // 根据不同的HTTP状态码给出不同的提示
            switch (error.response.status) {
                case 401:
                    message = '认证失败，请重新登录';
                    // 这是最关键的错误处理，直接调用登出逻辑
                    const authStore = useAuthStore();
                    authStore.logout();
                    break;
                case 403:
                    message = '您没有权限访问此资源';
                    break;
                case 404:
                    message = '请求的资源未找到';
                    break;
                case 500:
                    message = '服务器内部错误';
                    break;
                default:
                    message = error.response.data.message || '请求失败';
            }
        } else if (error.request) {
            message = '请求超时，请检查您的网络连接';
        }

        ElMessage({
            message: message,
            type: 'error',
            duration: 5 * 1000,
        });

        return Promise.reject(error);
    }
);

export default apiClient;

