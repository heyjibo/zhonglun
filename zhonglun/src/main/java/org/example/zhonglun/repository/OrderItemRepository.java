package org.example.zhonglun.repository;

import org.example.zhonglun.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // [新增] 根据订单ID查询所有订单项，用于获取订单详情
    List<OrderItem> findByOrderId(Long orderId);
    List<OrderItem> findByOrderIdIn(Collection<Long> orderIds);
}
