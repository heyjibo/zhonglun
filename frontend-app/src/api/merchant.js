import apiClient from './index';

/**
 * 获取商家自己的商品列表
 * @param {object} params - { page, size, status }
 */
export const getMyProducts = (params) => {
    return apiClient.get('/merchant/products', { params });
};

/**
 * 上架一个新商品
 * @param {object} productData
 */
export const createProduct = (productData) => {
    return apiClient.post('/merchant/products', productData);
};

/**
 * 更新商品信息
 * @param {number} productId
 * @param {object} productData
 */
export const updateProduct = (productId, productData) => {
    return apiClient.put(`/merchant/products/${productId}`, productData);
};

/**
 * 上架/下架商品
 * @param {number} productId
 */
export const toggleProductStatus = (productId) => {
    return apiClient.patch(`/merchant/products/${productId}/toggle-status`);
};

/**
 * 获取商家的订单列表
 * @param {object} params - { page, size, status }
 */
export const getMyOrders = (params) => {
    return apiClient.get('/merchant/orders', { params });
};

/**
 * 处理订单（如：发货）
 * @param {number} orderId
 * @param {object} action - { action: 'SHIP' }
 */
export const processOrder = (orderId, action) => {
    return apiClient.patch(`/merchant/orders/${orderId}/process`, action);
};
