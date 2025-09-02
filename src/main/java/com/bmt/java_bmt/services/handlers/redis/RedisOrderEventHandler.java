package com.bmt.java_bmt.services.handlers.redis;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Component;

import com.bmt.java_bmt.dto.others.Id;
import com.bmt.java_bmt.entities.Order;
import com.bmt.java_bmt.entities.enums.OrderStatus;
import com.bmt.java_bmt.entities.enums.SeatStatus;
import com.bmt.java_bmt.helpers.constants.RedisKey;
import com.bmt.java_bmt.repositories.IOrderRepository;
import com.bmt.java_bmt.repositories.IShowtimeSeatRepository;
import com.bmt.java_bmt.services.IRedisService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RedisOrderEventHandler {
    IRedisService redisService;
    IShowtimeSeatRepository showtimeSeatRepository;
    IOrderRepository orderRepository;

    @Transactional
    public void handleOrderWhenNotPaid(String expiredKey) {
        UUID orderId = UUID.fromString(expiredKey.substring(RedisKey.TOTAL_OF_ORDER.length()));
        log.info("⏰ Đơn hàng hết hạn TTL. orderId={}", orderId);

        Optional<Order> optionalOrder = orderRepository.findById(orderId);

        if (optionalOrder.isEmpty()) {
            log.warn("⚠️ Không tìm thấy đơn hàng cho key hết hạn: {}", expiredKey);
            return;
        }

        Order order = optionalOrder.get();
        UUID showtimeId = order.getShowtime().getId();
        String showtimeSeatsKey = RedisKey.SHOWTIME_SEATS + showtimeId;

        @SuppressWarnings("unchecked")
        List<Id> showtimeSeatIds = (List<Id>) redisService.get(showtimeSeatsKey);

        if (showtimeSeatIds == null) {
            log.warn("⚠️ Không tìm thấy cache ghế cho suất chiếu: {}", showtimeId);
            return;
        }

        // Cập nhật trạng thái ghế và log chi tiết
        order.getOrderSeats().forEach(orderedSeat -> {
            UUID seatId = orderedSeat.getSeat().getId();

            showtimeSeatRepository.updateStatusOfSeatsWhenNotPaidBySeatIdAndShowtimeId(
                    SeatStatus.AVAILABLE.name(), seatId, showtimeId);

            showtimeSeatIds.add(Id.builder().id(seatId.toString()).build());

            log.info("🎟 Đã giải phóng ghế. orderId={} | seatId={} | showtimeId={}", orderId, seatId, showtimeId);
        });

        // Cập nhật lại cache nếu TTL còn
        Long ttl = redisService.getTTL(showtimeSeatsKey, TimeUnit.MINUTES);

        if (ttl != null && ttl > 0) {
            redisService.save(showtimeSeatsKey, showtimeSeatIds, ttl, TimeUnit.MINUTES);

            log.info("💾 Đã cập nhật lại cache cho suất chiếu {} với TTL={} phút", showtimeId, ttl);
        } else {
            log.warn("⚠️ Không tìm thấy TTL cho cache key={} | bỏ qua cập nhật cache", showtimeSeatsKey);
        }

        // Đánh dấu order là FAILED
        order.setStatus(OrderStatus.FAILED);

        orderRepository.save(order);

        log.info("❌ Đơn hàng đã được chuyển sang trạng thái FAILED. orderId={} | showtimeId={}", orderId, showtimeId);
    }
}
