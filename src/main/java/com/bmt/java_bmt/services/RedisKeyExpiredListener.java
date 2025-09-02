package com.bmt.java_bmt.services;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import com.bmt.java_bmt.helpers.constants.RedisKey;
import com.bmt.java_bmt.services.handlers.redis.RedisOrderEventHandler;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RedisKeyExpiredListener implements MessageListener {
    RedisOrderEventHandler orderEventHandler;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = message.toString();

        if (expiredKey.startsWith(RedisKey.TOTAL_OF_ORDER)) {
            orderEventHandler.handleOrderWhenNotPaid(expiredKey);
        }
    }
}
