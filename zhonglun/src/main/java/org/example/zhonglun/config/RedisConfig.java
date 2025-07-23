package org.example.zhonglun.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 配置类
 * 核心功能：
 * 1. 自定义 RedisTemplate 的序列化机制。
 * 2. 将默认的 JDK 序列化改为可读性更高、跨语言性更好的 Jackson JSON 序列化。
 * 3. Key 使用 String 序列化，Value 使用 JSON 序列化。
 * 4. 解决 Java 8 时间类型 (如 LocalDateTime) 的序列化问题。
 */
@Configuration
public class RedisConfig {

    /**
     * 自定义 RedisTemplate Bean
     *
     * @param redisConnectionFactory Spring Boot 自动配置的 Redis 连接工厂
     * @return 配置好的 RedisTemplate 实例
     */
    @Bean
    @SuppressWarnings("all") // 压制所有警告，因为这里的配置是类型安全的
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        // 创建 RedisTemplate 对象
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        // 设置连接工厂
        template.setConnectionFactory(redisConnectionFactory);

        // --- 创建序列化器 ---

        // 1. 创建 String 序列化器，用于 Key
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

        // 2. 创建 Jackson JSON 序列化器，用于 Value
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper om = new ObjectMapper();

        // 设置 ObjectMapper 的可见性，允许序列化所有字段（包括 private）
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);

        // 关键步骤：启用默认类型信息，这样在反序列化时就能知道原始类型。
        // 这会在 JSON 中添加一个 "@class" 属性，例如: "@class":"com.zhonglun.entity.Product"
        // LaissezFaireSubTypeValidator.instance 是 Spring Boot 3.x 推荐的安全验证器
        om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);

        // 注册 Java 8 时间模块，以正确处理 LocalDateTime 等类型
        om.registerModule(new JavaTimeModule());

        jackson2JsonRedisSerializer.setObjectMapper(om);


        // --- 配置 RedisTemplate 的序列化规则 ---

        // Key 和 HashKey 都使用 String 序列化
        template.setKeySerializer(stringRedisSerializer);
        template.setHashKeySerializer(stringRedisSerializer);

        // Value 和 HashValue 都使用 Jackson JSON 序列化
        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.setHashValueSerializer(jackson2JsonRedisSerializer);

        // 初始化 RedisTemplate
        template.afterPropertiesSet();

        return template;
    }
}
