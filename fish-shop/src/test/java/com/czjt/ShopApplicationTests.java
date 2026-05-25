package com.czjt;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
class ShopApplicationTests {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    void testRedis() {
        redisTemplate.opsForValue().set("testKey", "Hello Redis");
        String value = (String) redisTemplate.opsForValue().get("testKey");
        System.out.println("Redis value: " + value);
    }
}

