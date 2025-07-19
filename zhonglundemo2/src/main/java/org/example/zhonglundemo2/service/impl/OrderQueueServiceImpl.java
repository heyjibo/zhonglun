package org.example.zhonglundemo2.service.impl;

import org.example.zhonglundemo2.dto.OrderRequest;
import org.example.zhonglundemo2.service.OrderQueueService;
import org.example.zhonglundemo2.service.RedisQueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderQueueServiceImpl implements OrderQueueService {
    private final RedisQueueService redisQueueService;

    @Override
    public void enqueueOrder(OrderRequest orderRequest) {
        redisQueueService.enqueueOrder(orderRequest);
    }
}
