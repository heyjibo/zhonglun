package org.example.zhonglundemo2.repository;

import org.example.zhonglundemo2.entity.Merchant;
import org.example.zhonglundemo2.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findAllByActiveTrue(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.active = true ORDER BY p.viewCount DESC")
    Page<Product> findTopPopularProducts(Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findByIdForUpdate(@Param("id") Long id);

    Page<Product> findByMerchant(Merchant merchant, Pageable pageable);
}
