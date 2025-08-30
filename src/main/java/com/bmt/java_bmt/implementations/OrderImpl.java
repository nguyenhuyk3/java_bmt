package com.bmt.java_bmt.implementations;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.bmt.java_bmt.dto.others.FABOrder;
import com.bmt.java_bmt.dto.others.Id;
import com.bmt.java_bmt.dto.others.SeatOrder;
import com.bmt.java_bmt.dto.requests.order.CreateOrderRequest;
import com.bmt.java_bmt.entities.Order;
import com.bmt.java_bmt.entities.OrderFab;
import com.bmt.java_bmt.entities.OrderSeat;
import com.bmt.java_bmt.entities.enums.OrderStatus;
import com.bmt.java_bmt.exceptions.AppException;
import com.bmt.java_bmt.exceptions.ErrorCode;
import com.bmt.java_bmt.helpers.constants.RedisKey;
import com.bmt.java_bmt.repositories.*;
import com.bmt.java_bmt.services.IOrderService;
import com.bmt.java_bmt.services.IRedisService;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
@AllArgsConstructor
public class OrderImpl implements IOrderService {
    IOrderRepository orderRepository;
    IShowtimeRepository showtimeRepository;
    IFoodAndBeverageRepository foodAndBeverageRepository;
    ISeatRepository seatRepository;
    IRedisService redisService;
    IUserRepository userRepository;

    long FIFTEEN_MINUTES = 15;

    @Override
    public String createOrder(CreateOrderRequest request) {
        // 1. Lấy user
        var user = userRepository
                .findById(UUID.fromString(
                        SecurityContextHolder.getContext().getAuthentication().getName()))
                .orElseThrow(() -> new AppException(ErrorCode.USER_ID_DOESNT_EXIST));
        // 2. Lấy showtime
        var showtime = showtimeRepository
                .findById(request.getShowtimeId())
                .orElseThrow(() -> new AppException((ErrorCode.SHOWTIME_NOT_FOUND)));
        // 3. Validate FABs
        int totalOfOrder = 0;

        Set<OrderFab> orderedFABs = new HashSet<>();

        if (!request.getFABs().isEmpty()) {
            for (FABOrder fABOrder : request.getFABs()) {
                var foodAndBeverage = foodAndBeverageRepository
                        .findById(fABOrder.getFABId())
                        .orElseThrow(() -> new AppException((ErrorCode.FAB_NOT_FOUND)));
                var orderFAB = OrderFab.builder()
                        .order(null) // sẽ set sau khi có order
                        .quantity(fABOrder.getQuantity())
                        .foodAndBeverage(foodAndBeverage)
                        .build();

                orderedFABs.add(orderFAB);

                totalOfOrder += foodAndBeverage.getPrice() * fABOrder.getQuantity();
            }
        }
        // 4. Validate seats với Redis
        String showtimeSeatsKey =
                RedisKey.SHOWTIME_SEATS + request.getShowtimeId().toString();
        List<Id> showtimeSeatIds = (List<Id>) redisService.get(showtimeSeatsKey);

        if (showtimeSeatIds == null) {
            throw new AppException(ErrorCode.SHOWTIME_SEATS_NOT_FOUND_IN_CACHE);
        }

        Set<OrderSeat> orderedSeats = new HashSet<>();

        for (SeatOrder seatOrder : request.getSeats()) {
            Id seatId = Id.builder().id(seatOrder.getSeatId().toString()).build();

            if (!showtimeSeatIds.contains(seatId)) {
                throw new AppException((ErrorCode.SEAT_NOT_FOUND_IN_SHOWTIME));
            }

            // remove để tránh double-book
            showtimeSeatIds.remove(seatId);

            var seat = seatRepository
                    .findById(seatOrder.getSeatId())
                    .orElseThrow(() -> new AppException(ErrorCode.SEAT_NOT_FOUND));

            orderedSeats.add(OrderSeat.builder()
                    .order(null) // sẽ set sau
                    .seat(seat)
                    .build());

            totalOfOrder += seat.getPrice();
        }
        // 5. Update Redis với TTL cũ
        Long ttl = redisService.getTTL(showtimeSeatsKey, TimeUnit.MINUTES);

        if (ttl != null && ttl > 0) {
            redisService.save(showtimeSeatsKey, showtimeSeatIds, ttl, TimeUnit.MINUTES);
        }
        // 6. Tạo order
        var order = Order.builder()
                .status(OrderStatus.CREATED)
                .showtime(showtime)
                .orderedBy(user)
                .orderSeats(orderedSeats)
                .orderFabs(orderedFABs)
                .build();

        // gắn order ngược lại cho các child entity
        orderedSeats.forEach(seat -> seat.setOrder(order));
        orderedFABs.forEach(fab -> fab.setOrder(order));
        // 7. Save vào DB
        orderRepository.save(order);

        // 8. Lưu các thông tin cần thiết vào redis để phục vụ cho việc thanh toán
        String totalOfOrderKey = RedisKey.TOTAL_OF_ORDER + order.getId().toString();

        redisService.save(totalOfOrderKey, totalOfOrder, FIFTEEN_MINUTES, TimeUnit.MINUTES);

        return String.format("Order của bạn được tạo thành công với id %s", order.getId());
    }
}
