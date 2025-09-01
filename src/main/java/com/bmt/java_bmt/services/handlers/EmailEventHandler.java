package com.bmt.java_bmt.services.handlers;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.mail.MessagingException;

import org.springframework.stereotype.Component;

import com.bmt.java_bmt.dto.others.FABItem;
import com.bmt.java_bmt.dto.others.Id;
import com.bmt.java_bmt.dto.others.TicketInformation;
import com.bmt.java_bmt.entities.Film;
import com.bmt.java_bmt.entities.Order;
import com.bmt.java_bmt.entities.Showtime;
import com.bmt.java_bmt.entities.enums.City;
import com.bmt.java_bmt.entities.enums.FABType;
import com.bmt.java_bmt.entities.enums.Genre;
import com.bmt.java_bmt.repositories.IOrderRepository;
import com.bmt.java_bmt.utils.Formatter;
import com.bmt.java_bmt.utils.senders.CompletedPaymentEmailSender;
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
public class EmailEventHandler {
    IOrderRepository orderRepository;
    CompletedPaymentEmailSender completedPaymentEmailSender;
    ObjectMapper objectMapper;

    String HTML_FILE_PATH = "src/main/resources/templates/html/order/completed_payment.html";

    public void sendMailWhenPaymentSuccess(JsonNode afterNode) {
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
            Optional<Order> optionalOrder = orderRepository.findOrderDetailsById(orderIdUuid);
            Showtime showtime = optionalOrder.get().getShowtime();
            Film film = showtime.getFilm();
            TicketInformation ticketInformation = TicketInformation.builder()
                    .filmTitle(film.getTitle())
                    .genres(film.getGenres().stream()
                            .map(Genre::getVietnameseName)
                            .collect(Collectors.joining(", ")))
                    .duration(Formatter.formatDuration(film.getDuration()))
                    .posterUrl(film.getOtherFilmInformation().getPosterUrl())
                    .cinemaName(showtime.getAuditorium().getCinema().getName())
                    .city(City.toVietnamese(showtime.getAuditorium().getCinema().getCity()))
                    .address(showtime.getAuditorium().getCinema().getLocation())
                    .auditorium(showtime.getAuditorium().getName())
                    .showDate(Formatter.formatLocalDate(showtime.getShowDate()))
                    .showTime(Formatter.formatReadableTime(showtime.getStartTime()))
                    .seats(optionalOrder.get().getOrderSeats().stream()
                            .map(orderSeat -> orderSeat.getSeat().getSeatNumber())
                            .sorted()
                            .collect(Collectors.joining(" • ")))
                    .FABItems(optionalOrder.get().getOrderFabs().stream()
                            .map(orderFab -> FABItem.builder()
                                    .name(orderFab.getFoodAndBeverage().getName())
                                    .emoji(FABType.toEmoji(
                                            orderFab.getFoodAndBeverage().getType()))
                                    .quantity(orderFab.getQuantity())
                                    .build())
                            .collect(Collectors.toList()))
                    .build();

            try {
                completedPaymentEmailSender.sendTicketConfirmation(
                        optionalOrder.get().getOrderedBy().getEmail(),
                        "Hoàn thành quá trình đặt vé",
                        ticketInformation,
                        HTML_FILE_PATH);
            } catch (IOException e) {
                log.error("❌ Lỗi khi đọc template email");
            } catch (MessagingException e) {
                log.error("❌ Lỗi khi gửi email");
            }

            log.info("✅ Đã gửi mail để thông báo quá trình đặt vé đã thành công");
        } catch (JsonProcessingException e) {
            log.error("❌ Lỗi khi parse 'os_payload' thành orderId: {}", e.getMessage());
        }
    }
}
