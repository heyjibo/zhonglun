import apiClient from './index';

// --- 订单操作 ---

/**
 * 创建新订单 (顾客)
 * 用户点击“结算”后，调用此接口直接生成订单。
 * @param {object} orderData - { items, addressId } (对应 CreateOrderRequest)
 */
export const createOrder = (orderData) => {
    return apiClient.post('/orders', orderData);
};

/**
 * 获取当前用户的订单列表 (顾客)
 * @param {object} params - { page, size, status }
 */
export const getMyOrders = (params) => {
    return apiClient.get('/orders/my-orders', { params });
};

/**
 * 根据ID获取订单详情 (顾客/商家/平台)
 * @param {number} orderId
 */
export const getOrderById = (orderId) => {
    return apiClient.get(`/orders/${orderId}`);
};

/**
 * 取消订单 (顾客)
 * @param {number} orderId
 */
export const cancelOrder = (orderId) => {
    return apiClient.patch(`/orders/${orderId}/cancel`);
};
