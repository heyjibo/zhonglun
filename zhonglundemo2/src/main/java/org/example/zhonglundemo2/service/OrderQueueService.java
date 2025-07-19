package org.example.zhonglundemo2.service;

import org.example.zhonglundemo2.dto.OrderRequest;

public interface OrderQueueService {
    void enqueueOrder(OrderRequest orderRequest);
}