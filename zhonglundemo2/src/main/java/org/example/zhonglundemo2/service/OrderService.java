package org.example.zhonglundemo2.service;

import org.example.zhonglundemo2.dto.OrderRequest;
import org.example.zhonglundemo2.entity.Order;

import java.util.List;

public interface OrderService {
    Order createOrder(OrderRequest orderRequest);
    void processOrder(OrderRequest orderRequest);
    List<Order> findByCustomerId(Long customerId);
}