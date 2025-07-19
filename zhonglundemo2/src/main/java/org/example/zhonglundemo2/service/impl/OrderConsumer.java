package org.example.zhonglundemo2.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.zhonglundemo2.dto.OrderRequest;
import org.example.zhonglundemo2.service.OrderService;
import org.example.zhonglundemo2.service.RedisQueueService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderConsumer {
    private final OrderService orderService;
    private final RedisQueueService redisQueueService;

    @Scheduled(fixedDelay = 5000)
    public void processOrders() {
        OrderRequest orderRequest;
        while ((orderRequest = redisQueueService.dequeueOrder()) != null) {
            try {
                log.info("Processing order from Redis queue for customer: {}", orderRequest.getCustomerId());
                orderService.processOrder(orderRequest);
            } catch (Exception e) {
                log.error("Failed to process order: {}", orderRequest, e);

            }
        }
    }
}