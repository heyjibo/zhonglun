package org.example.zhonglun.service;

import org.example.zhonglun.dto.request.CreateOrderRequest;
import org.example.zhonglun.dto.response.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    /**
     * 创建订单 (核心业务)
     * @param request 下单请求
     * @param customerId 顾客ID
     * @return 创建成功的订单详情
     */
    OrderResponse createOrder(CreateOrderRequest request, Long customerId);

    /**
     * 根据ID获取订单详情
     * @param orderId 订单ID
     * @param userId  当前用户ID
     * @param userType 当前用户类型
     * @return 订单详情
     */
    OrderResponse getOrderById(Long orderId, Long userId, Integer userType);

    /**
     * 获取指定顾客的订单列表 (分页)
     * @param customerId 顾客ID
     * @param pageable 分页参数
     * @return 订单分页数据
     */
    Page<OrderResponse> getOrdersForCustomer(Long customerId, Pageable pageable);

    // ... 其他接口，如取消订单、商家获取订单、平台获取订单等
}
