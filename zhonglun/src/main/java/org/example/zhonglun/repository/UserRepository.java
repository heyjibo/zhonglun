package org.example.zhonglun.repository;

import org.example.zhonglun.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    // 用于注册时校验用户名是否存在，性能更高
    Boolean existsByUsername(String username);

    // 用于注册时校验邮箱是否存在
    Boolean existsByEmail(String email);
}
