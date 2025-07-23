package org.example.zhonglun.repository;

import org.example.zhonglun.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {
    //列举特定顾客的地址
    List<Address> findByUserId(Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE Address a SET a.isDefault = 0 WHERE a.userId = :userId")
    void clearDefaultByUserId(Long userId);
}
