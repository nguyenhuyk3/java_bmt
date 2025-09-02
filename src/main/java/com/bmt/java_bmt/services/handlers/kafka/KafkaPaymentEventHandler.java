package com.bmt.java_bmt.services.handlers.kafka;

import java.util.Optional;
import java.util.UUID;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Component;

import com.bmt.java_bmt.dto.others.Id;
import com.bmt.java_bmt.entities.Order;
import com.bmt.java_bmt.entities.Outbox;
import com.bmt.java_bmt.entities.enums.OrderStatus;
import com.bmt.java_bmt.entities.enums.SeatStatus;
import com.bmt.java_bmt.exceptions.AppException;
import com.bmt.java_bmt.exceptions.ErrorCode;
import com.bmt.java_bmt.helpers.constants.Others;
import com.bmt.java_bmt.repositories.IOrderRepository;
import com.bmt.java_bmt.repositories.IOutboxRepository;
import com.bmt.java_bmt.repositories.IShowtimeSeatRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class KafkaPaymentEventHandler {
    IOrderRepository orderRepository;
    IShowtimeSeatRepository showtimeSeatRepository;
    IOutboxRepository outboxRepository;
    ObjectMapper objectMapper;

    @Transactional
    public void handlePaymentSuccess(JsonNode afterNode) {
        JsonNode aggregatePayloadNode = afterNode.get("os_payload");

        if (aggregatePayloadNode == null || aggregatePayloadNode.isNull()) {
            log.error("❌ Trong trường 'after' không chứa 'os_payload', bỏ qua");
            return;
        }

        try {
            String aggregatePayloadString = aggregatePayloadNode.asText();
            JsonNode finalPayload = objectMapper.readTree(aggregatePayloadString);
            Id orderId = objectMapper.treeToValue(finalPayload, Id.class);
            UUID orderIdUuid = UUID.fromString(orderId.getId());
            Optional<Order> optionalOrder = orderRepository.findById(orderIdUuid);

            if (optionalOrder.isEmpty()) {
                log.error("❌ Không tìm thấy Order với id = {}", orderIdUuid.toString());

                // TODO: có thể đẩy vào DLQ / notification service ở đây
                return;
            }

            Order order = optionalOrder.get();

            order.setStatus(OrderStatus.SUCCESS);

            orderRepository.save(order);

            int affectedRows = showtimeSeatRepository.updateStatusOfSeatsByUserIdAndShowtimeId(
                    SeatStatus.BOOKED.name(),
                    order.getOrderedBy().getId(),
                    order.getShowtime().getId());

            if (affectedRows == 0) {
                log.info("❌ Cập nhập trạng thái của ghế thất bại");

                // TODO: có thể đẩy vào DLQ / notification service ở đây
                return;
            }

            try {
                outboxRepository.save(Outbox.builder()
                        .eventType(Others.SEND_MAIL_WHEN_PAYMENT_SUCCESS)
                        .payload(objectMapper.writeValueAsString(orderId))
                        .build());
            } catch (JsonProcessingException e) {
                throw new AppException(ErrorCode.JSON_PARSE_ERROR);
            }

            log.info("✅ Order {} đã cập nhật trạng thái SUCCESS", orderIdUuid);
        } catch (JsonProcessingException e) {
            log.error("❌ Lỗi khi parse 'os_payload' thành orderId: {}", e.getMessage());
        }
    }
}
