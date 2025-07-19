package org.example.zhonglundemo2.service.impl;

import org.example.zhonglundemo2.dto.ProductSalesDTO;
import org.example.zhonglundemo2.dto.ProductSalesProjection;
import org.example.zhonglundemo2.dto.SalesDataDTO;
import org.example.zhonglundemo2.repository.OrderRepository;
import org.example.zhonglundemo2.service.SalesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalesServiceImpl implements SalesService {
    private final OrderRepository orderRepository;

    @Override
    public SalesDataDTO getSalesDataByMerchant(Long merchantId) {
        // 获取总销售额（处理可能的null值）
        BigDecimal totalSales = orderRepository.getTotalSalesByMerchant(merchantId);
        if (totalSales == null) {
            totalSales = BigDecimal.ZERO;
        }

        // 获取总订单数（处理可能的null值）
        Integer totalOrders = orderRepository.countDeliveredOrdersByMerchant(merchantId);
        if (totalOrders == null) {
            totalOrders = 0;
        }

        // 获取总销售商品数（处理可能的null值）
        Integer totalProductsSold = orderRepository.sumDeliveredProductsByMerchant(merchantId);
        if (totalProductsSold == null) {
            totalProductsSold = 0;
        }

        // 转换Projection到DTO
        List<ProductSalesDTO> topSellingProducts = orderRepository
                .findTopSellingProductsByMerchant(merchantId)
                .stream()
                .map(projection -> new ProductSalesDTO(
                        projection.getProductId(),
                        projection.getProductName(),
                        projection.getQuantitySold(),
                        projection.getTotalRevenue()
                ))
                .toList();

        return new SalesDataDTO(totalSales, totalOrders, totalProductsSold);
    }
}