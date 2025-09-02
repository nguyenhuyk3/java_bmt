package com.bmt.java_bmt.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.stereotype.Component;

import com.bmt.java_bmt.services.RedisKeyExpiredListener;

@Component
public class RedisKeyspaceConfiguration {
    @Bean
    RedisMessageListenerContainer redisContainer(
            RedisConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        // Lắng nghe keyevent cho expired
        container.addMessageListener(listenerAdapter, new PatternTopic("__keyevent@0__:expired"));
        // Bạn có thể lắng nghe thêm set/del nếu muốn
        // container.addMessageListener(listenerAdapter, new PatternTopic("__keyevent@0__:set"));
        // container.addMessageListener(listenerAdapter, new PatternTopic("__keyevent@0__:del"));

        return container;
    }

    @Bean
    MessageListenerAdapter listenerAdapter(RedisKeyExpiredListener listener) {
        return new MessageListenerAdapter(listener);
    }
}
