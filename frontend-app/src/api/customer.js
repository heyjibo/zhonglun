import apiClient from './index';

// --- 商品相关 ---
/**
 * 获取商品列表（带分页和搜索）
 * @param {object} params - { page, size, search }
 */
export const getProducts = (params) => {
    return apiClient.get('/customer/products', { params });
};

/**
 * 根据ID获取商品详情
 * @param {number} productId
 */
export const getProductById = (productId) => {
    return apiClient.get(`/customer/products/${productId}`);
};


// --- 用户信息与地址 ---
/**
 * 获取当前顾客的个人信息
 */
export const getCustomerProfile = () => {
    return apiClient.get('/customer/profile');
};

/**
 * 更新顾客的个人信息
 * @param {object} profileData
 */
export const updateCustomerProfile = (profileData) => {
    return apiClient.put('/customer/profile', profileData);
};

/**
 * 获取顾客的所有收货地址
 */
export const getAddresses = () => {
    return apiClient.get('/customer/addresses');
};

/**
 * 添加新收货地址
 * @param {object} addressData
 */
export const addAddress = (addressData) => {
    return apiClient.post('/customer/addresses', addressData);
};

/**
 * 更新收货地址
 * @param {number} addressId
 * @param {object} addressData
 */
export const updateAddress = (addressId, addressData) => {
    return apiClient.put(`/customer/addresses/${addressId}`, addressData);
};

/**
 * 删除收货地址
 * @param {number} addressId
 */
export const deleteAddress = (addressId) => {
    return apiClient.delete(`/customer/addresses/${addressId}`);
};
