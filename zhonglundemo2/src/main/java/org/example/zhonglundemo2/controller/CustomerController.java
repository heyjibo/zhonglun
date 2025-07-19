package org.example.zhonglundemo2.controller;

import lombok.RequiredArgsConstructor;
import org.example.zhonglundemo2.dto.OrderRequest;
import org.example.zhonglundemo2.dto.ProductDTO;
import org.example.zhonglundemo2.entity.Order;
import org.example.zhonglundemo2.service.OrderQueueService;
import org.example.zhonglundemo2.service.OrderService;
import org.example.zhonglundemo2.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class CustomerController {
    private final ProductService productService;
    private final OrderQueueService orderQueueService;
    private final OrderService orderService;

    @GetMapping("/products")
    public ResponseEntity<Page<ProductDTO>> getActiveProducts(Pageable pageable) {
        return ResponseEntity.ok(productService.getAllActiveProducts(pageable));
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<ProductDTO> getProductDetails(@PathVariable Long id) {
        productService.incrementViewCount(id);
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PostMapping("/orders")
    public ResponseEntity<String> placeOrder(@RequestBody OrderRequest orderRequest) {
        orderQueueService.enqueueOrder(orderRequest);
        return ResponseEntity.accepted().body("Order received and is being processed");
    }

    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getCustomerOrders(@RequestParam Long customerId) {
        return ResponseEntity.ok(orderService.findByCustomerId(customerId));
    }
}