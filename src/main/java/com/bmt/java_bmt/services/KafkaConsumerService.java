package com.bmt.java_bmt.services;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.mail.MessagingException;

import jakarta.transaction.Transactional;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.bmt.java_bmt.dto.others.*;
import com.bmt.java_bmt.dto.responses.showtime.GetShowtimeSeatResponse;
import com.bmt.java_bmt.entities.Film;
import com.bmt.java_bmt.entities.Order;
import com.bmt.java_bmt.entities.Outbox;
import com.bmt.java_bmt.entities.Showtime;
import com.bmt.java_bmt.entities.enums.*;
import com.bmt.java_bmt.exceptions.AppException;
import com.bmt.java_bmt.exceptions.ErrorCode;
import com.bmt.java_bmt.helpers.constants.Others;
import com.bmt.java_bmt.helpers.constants.RedisKey;
import com.bmt.java_bmt.repositories.*;
import com.bmt.java_bmt.utils.Formatter;
import com.bmt.java_bmt.utils.senders.CompletedPaymentEmailSender;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
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
    IFilmRepository filmRepository;
    ISearchService searchService;
    IShowtimeSeatRepository showtimeSeatRepository;
    IOrderRepository orderRepository;
    IRedisService redisService;
    IOutboxRepository outboxRepository;
    IShowtimeRepository showtimeRepository;

    ObjectMapper objectMapper;
    CompletedPaymentEmailSender completedPaymentEmailSender;

    int NUMBER_OF_SEATS = 80;
    long TWO_DAY = 60 * 24 * 2;

    private FilmDocument toFilmDocument(IFilmElasticsearchProjection projection) throws JsonProcessingException {
        List<String> genres =
                (projection.getGenres() != null && !projection.getGenres().isBlank())
                        ? objectMapper.readValue(projection.getGenres(), new TypeReference<List<String>>() {})
                        : Collections.emptyList();
        List<SimplePersonInformation> actors = (projection.getActors() != null
                        && !projection.getActors().isBlank())
                ? objectMapper.readValue(projection.getActors(), new TypeReference<List<SimplePersonInformation>>() {})
                : Collections.emptyList();
        List<SimplePersonInformation> directors =
                (projection.getDirectors() != null && !projection.getDirectors().isBlank())
                        ? objectMapper.readValue(
                                projection.getDirectors(), new TypeReference<List<SimplePersonInformation>>() {})
                        : Collections.emptyList();

        return FilmDocument.builder()
                .id(projection.getId())
                .title(projection.getTitle())
                .description(projection.getDescription())
                .releaseDate(projection.getReleaseDate())
                .duration(projection.getDuration())
                .posterUrl(projection.getPosterUrl())
                .trailerUrl(projection.getTrailerUrl())
                .genres(genres)
                .actors(actors)
                .directors(directors)
                .build();
    }

    private void handleFilmCreatedAndUpdated(JsonNode afterNode, boolean isUpdated) {
        JsonNode aggregatePayloadNode = afterNode.get("os_payload");

        if (aggregatePayloadNode == null || aggregatePayloadNode.isNull()) {
            log.error("❌ Trong trường 'after' không chứa 'os_payload', bỏ qua");
            return;
        }

        try {
            String aggregatePayloadString = aggregatePayloadNode.asText();
            JsonNode finalPayload = objectMapper.readTree(aggregatePayloadString);
            Id filmId = objectMapper.treeToValue(finalPayload, Id.class);
            UUID filmUuid = UUID.fromString(filmId.getId());
            // Bước 1: Query CSDL để lấy dữ liệu tổng hợp
            Optional<IFilmElasticsearchProjection> projectionOpt =
                    filmRepository.findFilmDetailsForElasticsearch(filmUuid);

            if (projectionOpt.isEmpty()) {
                log.error("⚠️ Không tìm thấy thông tin phim với ID {} trong CSDL. Có thể đã bị xóa.", filmId);
                // Có thể gửi một lệnh xóa tới Elasticsearch ở đây nếu cần
                return;
            }

            // Bước 2: Chuyển đổi projection thành FilmDocument
            FilmDocument filmDocument = toFilmDocument(projectionOpt.get());
            // Bước 3: Đẩy dữ liệu vào Elasticsearch

            if (isUpdated) {
                searchService.deleteFilm(filmUuid.toString());
            }

            searchService.indexFilm(filmDocument);
        } catch (JsonProcessingException e) {
            log.error("❌ Lỗi khi parse 'os_payload' thành filmId: {}", e.getMessage());
        }
    }

    private void handleShowtimeReleased(JsonNode afterNode) {
        JsonNode aggregatePayloadNode = afterNode.get("os_payload");

        if (aggregatePayloadNode == null || aggregatePayloadNode.isNull()) {
            log.error("❌ Trong trường 'after' không chứa 'os_payload', bỏ qua");
            return;
        }

        try {
            String aggregatePayloadString = aggregatePayloadNode.asText();
            JsonNode finalPayload = objectMapper.readTree(aggregatePayloadString);
            Id showtimeId = objectMapper.treeToValue(finalPayload, Id.class);
            UUID showtimeUuid = UUID.fromString(showtimeId.getId());

            if (showtimeSeatRepository.createShowtimeSeats(showtimeUuid) != NUMBER_OF_SEATS) {
                log.error("❌ Lỗi khi tạo showtime seats: {}", ErrorCode.NOT_ENOUGH_SHOWTIME_SEATS.getMessage());
            }

            List<GetShowtimeSeatResponse> showtimeSeats =
                    showtimeSeatRepository.getShowtimeSeatsByShowtimeId(showtimeUuid);
            List<Id> showtimeSeatIds = new ArrayList<>();
            String showtimeSeatsKey = RedisKey.SHOWTIME_SEATS + showtimeId.getId();

            for (GetShowtimeSeatResponse showtimeSeat : showtimeSeats) {
                Id seatId = Id.builder().id(showtimeSeat.getSeatId().toString()).build();

                showtimeSeatIds.add(seatId);
            }

            redisService.save(showtimeSeatsKey, showtimeSeatIds, TWO_DAY, TimeUnit.MINUTES);
        } catch (JsonProcessingException e) {
            log.error("❌ Lỗi khi parse 'os_payload' thành showtimeId: {}", e.getMessage());
        }
    }

    @Transactional
    private void handlePaymentSuccess(JsonNode afterNode) {
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

    private void sendMailWhenPaymentSuccess(JsonNode afterNode) {
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
                    .cinemaName(
                            showtime.getAuditorium().getCinema().getName())
                    .city(City.toVietnamese(
                            showtime.getAuditorium().getCinema().getCity()))
                    .address(showtime.getAuditorium().getCinema().getLocation())
                    .auditorium(showtime.getAuditorium().getName())
                    .showDate(Formatter.formatLocalDate(showtime.getShowDate()))
                    .showTime(
                            Formatter.formatReadableTime(showtime.getStartTime()))
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
                        "src/main/resources/templates/html/order/completed_payment.html");
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
                    handleFilmCreatedAndUpdated(afterNode, false);

                    return;
                case Others.FILM_UPDATED:
                    handleFilmCreatedAndUpdated(afterNode, true);

                    return;
                case Others.SHOWTIME_RELEASED:
                    handleShowtimeReleased(afterNode);

                    return;
                case Others.PAYMENT_SUCCESS:
                    handlePaymentSuccess(afterNode);

                    return;
                case Others.PAYMENT_FAILED:
                    return;
                case Others.SEND_MAIL_WHEN_PAYMENT_SUCCESS:
                    sendMailWhenPaymentSuccess(afterNode);

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
