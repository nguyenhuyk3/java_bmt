package com.bmt.java_bmt.dto.responses.film;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import com.bmt.java_bmt.dto.others.OtherFilmInformation;
import com.bmt.java_bmt.entities.enums.Genre;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateFilmResponse {
    String title;
    String description;
    LocalDate releaseDate;
    UUID changedBy;
    Set<Genre> genres;
    OtherFilmInformation otherFilmInformation;
    Set<UUID> filmProfessionals;
}
