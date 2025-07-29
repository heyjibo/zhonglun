package org.example.zhonglun.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.zhonglun.dto.request.CreateAddressRequest;
import org.example.zhonglun.dto.response.CustomerProfileResponse;
import org.example.zhonglun.entity.Address;
import org.example.zhonglun.entity.Order;
import org.example.zhonglun.entity.User;
import org.example.zhonglun.exception.ResourceNotFoundException;
import org.example.zhonglun.repository.OrderRepository;
import org.example.zhonglun.repository.UserRepository;
import org.example.zhonglun.security.CustomUserDetails;
import org.example.zhonglun.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    private final AddressService addressService;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    @Autowired
    public CustomerController(AddressService addressService, UserRepository userRepository, OrderRepository orderRepository) {
        this.addressService = addressService;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    /**
     * 获取当前登录用户的个人信息
     */
    @GetMapping("/me")
    public ResponseEntity<CustomerProfileResponse> getMyProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        CustomerProfileResponse response = CustomerProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * 获取当前登录用户的所有收货地址
     */
    @GetMapping("/addresses")
    public ResponseEntity<List<Address>> getMyAddresses(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<Address> addresses = addressService.getAddressesByCustomerId(userDetails.getId());
        return ResponseEntity.ok(addresses);
    }

    /**
     * 为当前登录用户新增一个收货地址
     */
    @PostMapping("/addresses")
    public ResponseEntity<Address> addAddress(@Valid @RequestBody CreateAddressRequest request,
                                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        Address createdAddress = addressService.createAddress(request, userDetails.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAddress);
    }

    /**
     * 更新当前登录用户的指定收货地址
     */
    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<Address> updateAddress(@PathVariable Long addressId,
                                                 @Valid @RequestBody CreateAddressRequest request,
                                                 @AuthenticationPrincipal CustomUserDetails userDetails) {
        Address updatedAddress = addressService.updateAddress(addressId, request, userDetails.getId());
        return ResponseEntity.ok(updatedAddress);
    }

    /**
     * 删除当前登录用户的指定收货地址
     */
    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long addressId,
                                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        addressService.deleteAddress(addressId, userDetails.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/orders/{orderId}/logistics")
    public ResponseEntity<String> getOrderLogistics(@PathVariable Long orderId /*, @AuthenticationPrincipal YourUserPrincipal currentUser */) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        // 安全性检查: 确保是订单所有者在查询
        // if (!order.getUserId().equals(currentUser.getId())) {
        //     throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access Denied");
        // }

        String logisticsTrace = order.getLogisticsTrace();

        if (logisticsTrace == null || logisticsTrace.isEmpty()) {
            String noTraceInfo = "{\"Success\":false, \"Reason\":\"暂无物流信息，包裹可能正在等待揽收。\", \"Traces\":[]}";
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
                    .body(noTraceInfo);
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
                .body(logisticsTrace);
    }
}
