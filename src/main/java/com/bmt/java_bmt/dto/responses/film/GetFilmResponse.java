package com.bmt.java_bmt.dto.responses.film;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;

import com.bmt.java_bmt.dto.responses.filmProfessional.GetFilmProfessionalResponse;
import com.bmt.java_bmt.entities.enums.Genre;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetFilmResponse {
    private UUID id;
    private String title;
    private String description;
    private LocalDate releaseDate;
    private LocalTime duration;
    private Set<GetFilmProfessionalResponse> filmProfessionals;
    private Set<Genre> genres;
    private String posterUrl;
    private String trailerUrl;
}
