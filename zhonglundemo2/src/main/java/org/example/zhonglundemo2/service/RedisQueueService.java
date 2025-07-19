package org.example.zhonglundemo2.service;

import lombok.RequiredArgsConstructor;
import org.example.zhonglundemo2.dto.OrderRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisQueueService {
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String ORDER_QUEUE_KEY = "order:queue";

    public void enqueueOrder(OrderRequest orderRequest) {
        redisTemplate.opsForList().rightPush(ORDER_QUEUE_KEY, orderRequest);
    }

    public OrderRequest dequeueOrder() {
        return (OrderRequest) redisTemplate.opsForList().leftPop(ORDER_QUEUE_KEY);
    }
}