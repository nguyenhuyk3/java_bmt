package com.bmt.java_bmt.dto.responses.showtime;

import java.time.Instant;
import java.util.UUID;

public interface GetShowtimeSeatResponse {
    UUID getSeatId();

    String getSeatType();

    String getSeatNumber();

    Integer getPrice();

    String getStatus();

    UUID getBookedBy();

    Instant getCreatedAt();
}
