import apiClient from './index';

/**
 * 统一登录接口
 * @param {object} credentials - { username, password }
 */
export const login = (credentials) => {
    return apiClient.post('/auth/login', credentials);
};

/**
 * 顾客注册接口
 * @param {object} customerData - { username, password, email }
 */
export const registerCustomer = (customerData) => {
    return apiClient.post('/auth/register/customer', customerData);
};

/**
 * 商家注册接口
 * @param {object} merchantData - { username, password, email, storeName }
 */
export const registerMerchant = (merchantData) => {
    return apiClient.post('/auth/register/merchant', merchantData);
};
