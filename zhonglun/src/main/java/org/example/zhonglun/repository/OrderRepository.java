package org.example.zhonglun.repository;

import org.example.zhonglun.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // [修正] 方法名从 findByCustomerId... 修改为 findByUserId... 以匹配实体字段
    Page<Order> findByUserIdOrderByCreateTimeDesc(Long userId, Pageable pageable);
}
