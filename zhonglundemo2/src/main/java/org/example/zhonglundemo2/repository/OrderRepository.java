package org.example.zhonglundemo2.repository;

import org.example.zhonglundemo2.dto.ProductSalesDTO;
import org.example.zhonglundemo2.dto.ProductSalesProjection;
import org.example.zhonglundemo2.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o WHERE o.customer.id = :customerId")
    List<Order> findByCustomerId(@Param("customerId") Long customerId);

    @Query("SELECT DISTINCT o FROM Order o " +
            "JOIN o.items oi " +
            "JOIN oi.product p " +
            "WHERE p.merchant.id = :merchantId AND o.status = 'DELIVERED'")
    List<Order> findCompletedOrdersByMerchant(@Param("merchantId") Long merchantId);

    @Query("SELECT p.id AS productId, p.name AS productName, " +
            "SUM(oi.quantity) AS quantitySold, " +
            "SUM(oi.price * oi.quantity) AS totalRevenue " +
            "FROM OrderItem oi " +
            "JOIN oi.product p " +
            "JOIN oi.order o " +
            "WHERE p.merchant.id = :merchantId AND o.status = 'DELIVERED' " +
            "GROUP BY p.id, p.name " +
            "ORDER BY SUM(oi.quantity) DESC")
    List<ProductSalesProjection> findTopSellingProductsByMerchant(@Param("merchantId") Long merchantId);

    @Query("SELECT SUM(oi.price * oi.quantity) FROM OrderItem oi " +
            "JOIN oi.order o " +
            "WHERE o.status = 'DELIVERED' AND oi.product.merchant.id = :merchantId")
    BigDecimal getTotalSalesByMerchant(@Param("merchantId") Long merchantId);

    @Query("SELECT COUNT(o) FROM Order o " +
            "JOIN o.items oi " +
            "WHERE o.status = 'DELIVERED' AND oi.product.merchant.id = :merchantId")
    Integer countDeliveredOrdersByMerchant(@Param("merchantId") Long merchantId);

    @Query("SELECT SUM(oi.quantity) FROM OrderItem oi " +
            "JOIN oi.order o " +
            "WHERE o.status = 'DELIVERED' AND oi.product.merchant.id = :merchantId")
    Integer sumDeliveredProductsByMerchant(@Param("merchantId") Long merchantId);
}
