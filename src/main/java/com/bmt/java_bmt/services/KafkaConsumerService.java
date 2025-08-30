package com.bmt.java_bmt.services;

import java.util.*;
import java.util.concurrent.TimeUnit;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.bmt.java_bmt.dto.others.FilmDocument;
import com.bmt.java_bmt.dto.others.IFilmElasticsearchProjection;
import com.bmt.java_bmt.dto.others.Id;
import com.bmt.java_bmt.dto.others.SimplePersonInformation;
import com.bmt.java_bmt.dto.responses.showtime.GetShowtimeSeatResponse;
import com.bmt.java_bmt.exceptions.ErrorCode;
import com.bmt.java_bmt.helpers.constants.Others;
import com.bmt.java_bmt.helpers.constants.RedisKey;
import com.bmt.java_bmt.repositories.IFilmRepository;
import com.bmt.java_bmt.repositories.IShowtimeSeatRepository;
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
    ObjectMapper objectMapper;
    IFilmRepository filmRepository;
    ISearchService searchService;
    IShowtimeSeatRepository showtimeSeatRepository;
    IRedisService redisService;

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
        //        LocalDate releaseDate = (projection.getReleaseDate() != null
        //                && !projection.getReleaseDate().isBlank())
        //                ? LocalDate.parse(projection.getReleaseDate())
        //                : null;
        //        LocalTime duration =
        //                (projection.getDuration() != null && !projection.getDuration().isBlank())
        //                        ? LocalTime.parse(projection.getDuration())
        //                        : null;

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
                log.warn("⚠️ Không tìm thấy thông tin phim với ID {} trong CSDL. Có thể đã bị xóa.", filmId);
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
                ErrorCode errorCode = ErrorCode.NOT_ENOUGH_SHOWTIME_SEATS;

                log.error("❌ Lỗi khi tạo showtime seats: {}", errorCode.getMessage());
            }

            List<GetShowtimeSeatResponse> showtimeSeats =
                    showtimeSeatRepository.getShowtimeSeatsByShowtimeId(showtimeUuid);
            List<Id> showtimeSeatIds = new ArrayList<>();
            String showtimeSeatsKey = RedisKey.SHOWTIME_SEATS + showtimeId;

            for (GetShowtimeSeatResponse showtimeSeat : showtimeSeats) {
                Id seatId = Id.builder().id(showtimeSeat.getSeatId().toString()).build();

                showtimeSeatIds.add(seatId);
            }

            redisService.save(showtimeSeatsKey, showtimeSeatIds, TWO_DAY, TimeUnit.MINUTES);
        } catch (JsonProcessingException e) {
            log.error("❌ Lỗi khi parse 'os_payload' thành showtimeId: {}", e.getMessage());
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
