package org.example.zhonglundemo2.service.impl;

import org.example.zhonglundemo2.dto.OrderItemRequest; // 添加这行导入
import org.example.zhonglundemo2.dto.OrderRequest;
import org.example.zhonglundemo2.entity.*;
import org.example.zhonglundemo2.exception.InsufficientStockException;
import org.example.zhonglundemo2.exception.ResourceNotFoundException;
import org.example.zhonglundemo2.repository.CustomerRepository;
import org.example.zhonglundemo2.repository.OrderRepository;
import org.example.zhonglundemo2.repository.ProductRepository;
import org.example.zhonglundemo2.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;

    public OrderServiceImpl(OrderRepository orderRepository,
                            ProductRepository productRepository,
                            CustomerRepository customerRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    @Transactional
    public Order createOrder(OrderRequest orderRequest) {
        Customer customer = customerRepository.findById(orderRequest.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : orderRequest.getItems()) {
            Product product = productRepository.findByIdForUpdate(itemRequest.getProductId()).orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            if (product.getStock() < itemRequest.getQuantity()) {
                throw new InsufficientStockException("Insufficient stock for product: " + product.getName());
            }

            product.setStock(product.getStock() - itemRequest.getQuantity());
            productRepository.save(product);

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setPrice(product.getPrice());
            orderItems.add(orderItem);

            totalAmount = totalAmount.add(product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity())));
        }

        Order order = new Order();
        order.setCustomer(customer);
        order.setTotalAmount(totalAmount);
        order.setItems(orderItems);

        for (OrderItem item : orderItems) {
            item.setOrder(order);
        }

        return orderRepository.save(order);
    }

    @Override
    public List<Order> findByCustomerId(Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    @Override
    public void processOrder(OrderRequest orderRequest) {
        createOrder(orderRequest);
    }
}