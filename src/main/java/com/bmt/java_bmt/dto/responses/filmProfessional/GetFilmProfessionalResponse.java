package com.bmt.java_bmt.dto.responses.filmProfessional;

import java.util.UUID;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetFilmProfessionalResponse {
    UUID id;
    String job;
    String name;
    String avatarUrl;
}
