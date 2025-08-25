package com.bmt.java_bmt.services;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.bmt.java_bmt.dto.others.FilmId;
import com.bmt.java_bmt.helpers.constants.Others;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
@Slf4j
public class KafkaConsumerService {
    ObjectMapper objectMapper;

    @KafkaListener(topics = Others.OUTBOX, groupId = "java-bmt-group")
    public void listen(String message) {
        if (message == null) {
            log.error("❌ Tin nhắn từ Kafka bị null, bỏ qua");
            return;
        }

        try {
            JsonNode rootNode = objectMapper.readTree(message);
            JsonNode payloadNode = rootNode.get("payload");

            if (payloadNode == null || payloadNode.isNull()) {
                log.error("❌ Tin nhắn từ Kafka không chứa trường 'payload', bỏ qua");
                return;
            }

            JsonNode afterNode = payloadNode.get("after");

            if (afterNode == null || afterNode.isNull()) {
                log.error("❌ Trong trường 'payload' không chứa trường 'after, bỏ qua");
                return;
            }

            JsonNode eventTypeNode = afterNode.get("os_event_type");

            if (eventTypeNode == null || eventTypeNode.isNull()) {
                log.error("❌ Trong trường 'after' không chứa trường 'os_event_type, bỏ qua");
                return;
            }

            switch (eventTypeNode.asText()) {
                case Others.FILM_CREATED:
                    JsonNode aggregatePayloadNode = afterNode.get("os_payload");

                    if (aggregatePayloadNode == null || aggregatePayloadNode.isNull()) {
                        log.error("❌ Trong trường 'after' không chứa 'os_payload', bỏ qua");
                        return;
                    }

                    try {
                        String aggregatePayloadString = aggregatePayloadNode.asText();
                        JsonNode finalPayload = objectMapper.readTree(aggregatePayloadString);
                        FilmId filmId = objectMapper.treeToValue(finalPayload, FilmId.class);

                        log.info(filmId.toString());
                    } catch (JsonProcessingException e) {
                        log.error("❌ Lỗi khi parse 'os_payload' thành FilmId: {}", e.getMessage());
                    }

                    return;
                default:
                    log.error("❌ Sự kiện không hợp lệ, bỏ qua");
                    return;
            }
        } catch (JsonProcessingException e) {
            log.error("❌ Thất bại khi phân tích tin nhắn JSON: " + e.getMessage());
        } catch (Exception e) {
            log.error("❌ Đã xảy ra lỗi không mong muốn trong khi xử lý tin nhắn: " + message);

            // Ném lại lỗi để Spring Kafka biết và xử lý (ví dụ: retry hoặc gửi tới DLQ)
            throw new RuntimeException("Failed to process Kafka message", e);
        }
    }
}
