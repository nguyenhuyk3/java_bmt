package com.bmt.java_bmt.implementations;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.bmt.java_bmt.services.IRedisService;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Implementation của IRedis interface sử dụng RedisTemplate
 */
@Service
public class RedisImpl implements IRedisService<String, Object> {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public Boolean existsKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void save(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save(String key, Object value, long timeout, TimeUnit timeUnit) {
        try {
            redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Boolean delete(String key) {
        try {
            return redisTemplate.delete(key);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Long delete(String... keys) {
        try {
            return redisTemplate.delete(java.util.Arrays.asList(keys));
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }

    @Override
    public Object get(String key) {
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Long getTTL(String key) {
        try {
            return redisTemplate.getExpire(key);
        } catch (Exception e) {
            e.printStackTrace();
            return -2L; // Key không tồn tại
        }
    }

    @Override
    public Long getTTL(String key, TimeUnit timeUnit) {
        try {
            return redisTemplate.getExpire(key, timeUnit);
        } catch (Exception e) {
            e.printStackTrace();
            return -2L; // Key không tồn tại
        }
    }

    //    @Override
    //    public <T> T get(String key, Class<T> clazz) {
    //        try {
    //            Object value = redisTemplate.opsForValue().get(key);
    //
    //            if (value == null) {
    //                return null;
    //            }
    //
    //            return objectMapper.convertValue(value, clazz);
    //        } catch (Exception e) {
    //            e.printStackTrace();
    //            return null;
    //        }
    //    }

    //    @Override
    //    public <T> T get(String key, com.fasterxml.jackson.core.type.TypeReference<T> typeReference) {
    //        try {
    //            Object value = redisTemplate.opsForValue().get(key);
    //
    //            if (value == null) {
    //                return null;
    //            }
    //            // Dùng ObjectMapper với TypeReference để giữ lại thông tin generic
    //            return objectMapper.convertValue(value, typeReference);
    //        } catch (Exception e) {
    //            e.printStackTrace();
    //            return null;
    //        }
    //    }
}
