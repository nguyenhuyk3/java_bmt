package com.bmt.java_bmt.services.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import com.bmt.java_bmt.dto.others.Id;
import com.bmt.java_bmt.dto.responses.showtime.GetShowtimeSeatResponse;
import com.bmt.java_bmt.exceptions.ErrorCode;
import com.bmt.java_bmt.helpers.constants.RedisKey;
import com.bmt.java_bmt.repositories.IShowtimeSeatRepository;
import com.bmt.java_bmt.services.IRedisService;
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
public class ShowtimeEventHandler {
    IShowtimeSeatRepository showtimeSeatRepository;
    IRedisService redisService;
    ObjectMapper objectMapper;

    int NUMBER_OF_SEATS = 80;
    long TWO_DAY = 60 * 24 * 2;

    public void handleShowtimeReleased(JsonNode afterNode) {
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
}
