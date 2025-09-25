package com.bmt.java_bmt.dto.responses.cinema;

import java.util.UUID;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetCinemaResponse {
    UUID id;
    String name;
    String location;
    String city;
    int numberOfRooms;
}
