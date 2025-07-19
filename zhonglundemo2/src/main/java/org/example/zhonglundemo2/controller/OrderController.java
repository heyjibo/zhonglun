package org.example.zhonglundemo2.controller;

import org.example.zhonglundemo2.dto.OrderRequest;
import org.example.zhonglundemo2.entity.Order;
import org.example.zhonglundemo2.service.OrderQueueService;
import org.example.zhonglundemo2.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderQueueService orderQueueService;
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody OrderRequest orderRequest) {
        Order order = orderService.createOrder(orderRequest);
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    @PostMapping("/async")
    public ResponseEntity<String> createOrderAsync(@RequestBody OrderRequest orderRequest) {
        orderQueueService.enqueueOrder(orderRequest);
        return ResponseEntity.accepted().body("Order received and is being processed");
    }
}
