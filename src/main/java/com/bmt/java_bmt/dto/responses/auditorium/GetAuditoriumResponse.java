package com.bmt.java_bmt.dto.responses.auditorium;

import java.util.UUID;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetAuditoriumResponse {
    UUID id;
    String name;
    int numberOfSeats;
}
