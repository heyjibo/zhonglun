package org.example.zhonglun.controller;

import jakarta.validation.Valid;
import org.example.zhonglun.dto.request.CreateOrderRequest;
import org.example.zhonglun.dto.response.OrderResponse;
import org.example.zhonglun.security.CustomUserDetails;
import org.example.zhonglun.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 订单处理控制器
 * 包含顾客下单、查询订单等功能。
 * 使用 Redis + Lua 脚本处理下单过程中的库存扣减，以应对高并发场景。
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * 顾客创建订单
     * 此接口是高并发的核心入口。
     * - 安全：仅限角色为 'CUSTOMER' 的用户访问。
     * - 健壮：Service层会使用原子操作处理库存，防止超卖。
     *
     * @param userDetails      通过 @AuthenticationPrincipal 自动注入的当前登录用户信息
     * @param createOrderRequest 包含订单项和收货地址的请求体
     * @return 创建成功的订单详情
     */
    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<OrderResponse> createOrder(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                     @Valid @RequestBody CreateOrderRequest createOrderRequest) {
        OrderResponse createdOrder = orderService.createOrder(createOrderRequest, userDetails.getId());
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }

    /**
     * 获取当前登录顾客的订单列表 (分页)
     *
     * @param userDetails 当前登录用户信息
     * @param pageable    分页参数 (e.g., ?page=0&size=10&sort=createTime,desc)
     * @return 订单分页数据
     */
    @GetMapping("/my-orders")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Page<OrderResponse>> getMyOrders(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                           @PageableDefault(sort = "createTime", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<OrderResponse> orders = orderService.getOrdersForCustomer(userDetails.getId(), pageable);
        return ResponseEntity.ok(orders);
    }

    /**
     * 获取单个订单的详情
     * - 安全：Service层会校验，确保只有订单所有者、相关商家或平台管理员能查看。
     *
     * @param userDetails 当前登录用户信息
     * @param orderId     订单ID
     * @return 订单详情
     */
    @GetMapping("/{orderId}")
    @PreAuthorize("isAuthenticated()") // 允许任何已登录角色访问，具体权限由Service层判断
    public ResponseEntity<OrderResponse> getOrderDetails(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                         @PathVariable Long orderId) {
        OrderResponse order = orderService.getOrderById(orderId, userDetails.getId(), userDetails.getUser().getUserType());
        return ResponseEntity.ok(order);
    }

    /*
     * 注意：
     * 1. 商家查询/处理订单的 API 应放在 MerchantController 中，并使用 @PreAuthorize("hasRole('MERCHANT')") 保护。
     *    例如: GET /api/merchant/orders, PUT /api/merchant/orders/{orderId}/ship
     *
     * 2. 平台管理员查询所有订单的 API 应放在 PlatformController 中，并使用 @PreAuthorize("hasRole('PLATFORM_ADMIN')") 保护。
     *    例如: GET /api/platform/orders
     */
}
