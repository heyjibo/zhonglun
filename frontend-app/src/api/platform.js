import apiClient from './index';

// --- 数据总览 ---
/**
 * 获取平台仪表盘数据
 */
export const getDashboardStats = () => {
    return apiClient.get('/platform/dashboard/stats');
};

// --- 商家管理 ---
/**
 * 获取所有商家列表
 * @param {object} params - { page, size, search }
 */
export const getAllMerchants = (params) => {
    return apiClient.get('/platform/merchants', { params });
};

/**
 * 更新商家状态（如：审核通过/禁用）
 * @param {number} merchantId
 * @param {object} statusUpdate - { status: 'APPROVED' }
 */
export const updateMerchantStatus = (merchantId, statusUpdate) => {
    return apiClient.patch(`/platform/merchants/${merchantId}/status`, statusUpdate);
};

// --- 商品管理 ---
/**
 * 获取平台所有商品列表
 * @param {object} params - { page, size, search }
 */
export const getAllProducts = (params) => {
    return apiClient.get('/platform/products', { params });
};

// --- 订单管理 ---
/**
 * 获取平台所有订单列表
 * @param {object} params - { page, size, search }
 */
export const getAllOrders = (params) => {
    return apiClient.get('/platform/orders', { params });
};
