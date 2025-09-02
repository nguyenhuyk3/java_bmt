package com.bmt.java_bmt.services;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.bmt.java_bmt.helpers.constants.Others;
import com.bmt.java_bmt.services.handlers.kafka.KafkaEmailEventHandler;
import com.bmt.java_bmt.services.handlers.kafka.KafkaFilmEventHandler;
import com.bmt.java_bmt.services.handlers.kafka.KafkaPaymentEventHandler;
import com.bmt.java_bmt.services.handlers.kafka.KafkaShowtimeEventHandler;
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
    KafkaFilmEventHandler filmEventHandler;
    KafkaShowtimeEventHandler showtimeEventHandler;
    KafkaPaymentEventHandler paymentEventHandler;
    KafkaEmailEventHandler emailEventHandler;

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
                    filmEventHandler.handleFilmCreatedAndUpdated(afterNode, false);

                    return;
                case Others.FILM_UPDATED:
                    filmEventHandler.handleFilmCreatedAndUpdated(afterNode, true);

                    return;
                case Others.SHOWTIME_RELEASED:
                    showtimeEventHandler.handleShowtimeReleased(afterNode);

                    return;
                case Others.PAYMENT_SUCCESS:
                    paymentEventHandler.handlePaymentSuccess(afterNode);

                    return;
                case Others.PAYMENT_FAILED:
                    return;
                case Others.SEND_MAIL_WHEN_PAYMENT_SUCCESS:
                    emailEventHandler.sendMailWhenPaymentSuccess(afterNode);

                    return;
                default:
                    log.error("❌ Sự kiện không hợp lệ, bỏ qua");
            }
        } catch (JsonProcessingException e) {
            log.error("❌ Thất bại khi phân tích tin nhắn JSON: " + e.getMessage());
        } catch (Exception e) {
            log.error("❌ Đã xảy ra lỗi không mong muốn trong khi xử lý tin nhắn: " + e.getMessage());

            // Ném lại lỗi để Spring Kafka biết và xử lý (ví dụ: retry hoặc gửi tới DLQ)
            //            throw new RuntimeException("Failed to process Kafka message", e);
        }
    }
}
