package org.example.zhonglun.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.zhonglun.dto.request.CreateOrderRequest;
import org.example.zhonglun.dto.request.OrderItemRequest;
import org.example.zhonglun.dto.response.OrderItemResponse;
import org.example.zhonglun.dto.response.OrderResponse;
import org.example.zhonglun.entity.Address;
import org.example.zhonglun.entity.Order;
import org.example.zhonglun.entity.OrderItem;
import org.example.zhonglun.entity.Product;
import org.example.zhonglun.exception.ResourceNotFoundException;
import org.example.zhonglun.exception.ValidationException;
import org.example.zhonglun.repository.*;
import org.example.zhonglun.service.OrderService;
import org.example.zhonglun.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final ProductService productService;

    public OrderServiceImpl(OrderRepository orderRepository, OrderItemRepository orderItemRepository, ProductRepository productRepository, AddressRepository addressRepository, UserRepository userRepository, ProductService productService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
        this.productService = productService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderResponse createOrder(CreateOrderRequest request, Long customerId) {
        log.info("用户ID: {} 开始创建订单...", customerId);

        // 1. 基础信息校验
        userRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", customerId));
        Address address = addressRepository.findById(request.getAddressId())
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", request.getAddressId()));

        // [修正] 安全校验：根据实体类的 userId 字段进行校验
        if (!address.getUserId().equals(customerId)) {
            throw new AccessDeniedException("无法使用不属于自己的收货地址！");
        }

        List<OrderItemRequest> deductedStockItems = new ArrayList<>();
        try {
            // 2. 预扣减Redis库存
            for (OrderItemRequest itemReq : request.getItems()) {
                boolean success = productService.deductStock(itemReq.getProductId(), itemReq.getQuantity());
                if (!success) {
                    throw new ValidationException("库存不足，商品ID: " + itemReq.getProductId());
                }
                deductedStockItems.add(itemReq);
            }

            // 3. 库存扣减成功，创建数据库记录
            Order order = new Order();
            // [修正] 设置 userId 和 addressId，而不是复杂的对象
            order.setUserId(customerId);
            order.setAddressId(address.getId());
            order.setStatus("0"); // 0-待付款
            order.setOrderSn(generateOrderSn());
            order.setCreateTime(new Timestamp(System.currentTimeMillis()));

            Map<Long, Product> productMap = getProductMap(request.getItems());
            BigDecimal totalPrice = BigDecimal.ZERO;

            for (OrderItemRequest itemReq : request.getItems()) {
                Product product = productMap.get(itemReq.getProductId());
                BigDecimal itemPrice = product.getPrice();
                totalPrice = totalPrice.add(itemPrice.multiply(BigDecimal.valueOf(itemReq.getQuantity())));
            }
            order.setTotalAmount(totalPrice);

            // [修正] 先保存Order实体，以获取其生成的ID
            Order savedOrder = orderRepository.save(order);

            // [修正] 为每个OrderItem设置刚生成的orderId，然后批量保存
            List<OrderItem> orderItemsToSave = new ArrayList<>();
            for (OrderItemRequest itemReq : request.getItems()) {
                Product product = productMap.get(itemReq.getProductId());
                OrderItem orderItem = new OrderItem();
                orderItem.setOrderId(savedOrder.getId()); // [关键] 设置外键
                orderItem.setProductId(product.getId());
                orderItem.setProductName(product.getName()); // 记录快照
                orderItem.setProductPrice(product.getPrice()); // 记录快照
                orderItem.setProductQuantity(itemReq.getQuantity());
                orderItemsToSave.add(orderItem);
            }
            List<OrderItem> savedItems = orderItemRepository.saveAll(orderItemsToSave);

            log.info("订单创建成功，订单号: {}, 包含 {} 个订单项", savedOrder.getOrderSn(), savedItems.size());

            // [修正] 构建并返回响应DTO
            return convertToResponse(savedOrder, savedItems, address);

        } catch (Exception e) {
            log.error("创建订单失败，用户ID: {}. 错误: {}", customerId, e.getMessage());
            // [关键] 库存补偿机制
            if (!deductedStockItems.isEmpty()) {
                log.warn("开始补偿回滚Redis库存...");
                for (OrderItemRequest itemToRollback : deductedStockItems) {
                    productService.increaseStock(itemToRollback.getProductId(), itemToRollback.getQuantity());
                }
                log.warn("Redis库存补偿完成。");
            }
            throw e;
        }
    }

    @Override
    public OrderResponse getOrderById(Long orderId, Long userId, Integer userType) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        // [修正] 权限校验，使用实体类中定义的 userId 字段
        if (userType == 1 && !order.getUserId().equals(userId)) { // 顾客只能看自己的
            throw new AccessDeniedException("您无权查看此订单");
        }
        // TODO: 可在此处扩展商家和平台的权限校验逻辑

        // [修正] 手动查询关联的订单项和地址信息
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        Address address = addressRepository.findById(order.getAddressId())
                .orElse(null); // 地址可能已被删除，做个空值安全处理

        return convertToResponse(order, items, address);
    }

    @Override
    public Page<OrderResponse> getOrdersForCustomer(Long customerId, Pageable pageable) {
        Page<Order> orderPage = orderRepository.findByUserIdOrderByCreateTimeDesc(customerId, pageable);
        List<Order> ordersOnPage = orderPage.getContent();

        if (ordersOnPage.isEmpty()) {
            return Page.empty(pageable);
        }

        List<Long> orderIds = ordersOnPage.stream()
                .map(Order::getId)
                .collect(Collectors.toList());

        List<Long> addressIds = ordersOnPage.stream()
                .map(Order::getAddressId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());


        List<OrderItem> allItemsForPage = orderItemRepository.findByOrderIdIn(orderIds);

        List<Address> allAddressesForPage = addressRepository.findAllById(addressIds);


        Map<Long, List<OrderItem>> itemsByOrderIdMap = allItemsForPage.stream()
                .collect(Collectors.groupingBy(OrderItem::getOrderId));

        Map<Long, Address> addressByIdMap = allAddressesForPage.stream()
                .collect(Collectors.toMap(Address::getId, address -> address));

        List<OrderResponse> orderResponses = ordersOnPage.stream().map(order -> {
            List<OrderItem> items = itemsByOrderIdMap.getOrDefault(order.getId(), Collections.emptyList());
            Address address = addressByIdMap.get(order.getAddressId());

            return convertToResponse(order, items, address);
        }).collect(Collectors.toList());

        return new PageImpl<>(orderResponses, pageable, orderPage.getTotalElements());
    }

    private OrderResponse convertToResponse(Order order, List<OrderItem> items, Address address) {
        List<OrderItemResponse> itemResponses = items.stream()
                .map(item -> OrderItemResponse.builder()
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .quantity(item.getProductQuantity())
                        .unitPrice(item.getProductPrice())
                        .build())
                .collect(Collectors.toList());

        // 使用实体类的正确字段构建响应DTO
        return OrderResponse.builder()
                .orderId(order.getId())
                .orderNumber(order.getOrderSn())
                .customerId(order.getUserId())
                .totalPrice(order.getTotalAmount())
                .status(Integer.parseInt(order.getStatus())) // 假设DTO中status是Integer
                .remark(null) // 实体类中无此字段，可设为null或从请求中获取
                .createTime(order.getCreateTime().toLocalDateTime())
                .shippingAddress(address) // 直接使用查询到的Address对象
                .items(itemResponses)
                .build();
    }

    private Map<Long, Product> getProductMap(List<OrderItemRequest> items) {
        List<Long> productIds = items.stream()
                .map(OrderItemRequest::getProductId)
                .collect(Collectors.toList());
        List<Product> products = productRepository.findAllById(productIds);
        if (products.size() != productIds.size()) {
            throw new ResourceNotFoundException("存在一个或多个无效的商品ID");
        }
        return products.stream().collect(Collectors.toMap(Product::getId, p -> p));
    }

    private String generateOrderSn() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        int randomNum = ThreadLocalRandom.current().nextInt(100, 1000);
        return timestamp + randomNum;
    }
}
