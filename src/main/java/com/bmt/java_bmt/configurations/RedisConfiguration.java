package com.bmt.java_bmt.configurations;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis Configuration Class - Updated version without deprecated methods
 */
@Configuration
public class RedisConfiguration {

    /**
     * Cấu hình RedisTemplate với JSON serialization (không sử dụng deprecated methods)
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();

        template.setConnectionFactory(connectionFactory);

        // Tạo ObjectMapper với cấu hình mới
        ObjectMapper objectMapper = new ObjectMapper();

        /*
            ObjectMapper là class của Jackson dùng để chuyển đổi object <-> JSON.
            Bình thường Jackson chỉ serialize/deserialize những field public hoặc những field có getter/setter.
            Nếu object có field private mà không có getter/setter, mặc định Jackson sẽ bỏ qua.
            PropertyAccessor.ALL: Áp dụng cho tất cả loại property: field, getter, setter, creator...
            JsonAutoDetect.Visibility.ANY: Cho phép Jackson truy cập tất cả (bao gồm cả private, protected, package, public).
         */
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        // Sử dụng BasicPolymorphicTypeValidator thay vì LaissezFaireSubTypeValidator (deprecated)
        BasicPolymorphicTypeValidator typeValidator = BasicPolymorphicTypeValidator.builder()
                .allowIfSubType(Object.class)
                .build();

        objectMapper.activateDefaultTyping(typeValidator, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);

        // Sử dụng GenericJackson2JsonRedisSerializer với ObjectMapper tùy chỉnh
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        // String serializer
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

        // Set key serializer
        template.setKeySerializer(stringRedisSerializer);
        template.setHashKeySerializer(stringRedisSerializer);

        // Set value serializer
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        // Enable default serialization
        template.setDefaultSerializer(jsonSerializer);
        template.setEnableDefaultSerializer(true);

        template.afterPropertiesSet();

        return template;
    }

    /**
     * Alternative configuration - Sử dụng GenericJackson2JsonRedisSerializer đơn giản
     */
    @Bean("simpleRedisTemplate")
    public RedisTemplate<String, Object> simpleRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();

        template.setConnectionFactory(connectionFactory);

        // Sử dụng GenericJackson2JsonRedisSerializer mặc định (không deprecated)
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

        // Set serializers
        template.setKeySerializer(stringRedisSerializer);
        template.setHashKeySerializer(stringRedisSerializer);
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        template.afterPropertiesSet();
        return template;
    }
}