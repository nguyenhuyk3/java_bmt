package com.bmt.java_bmt.dto.requests.film;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.bmt.java_bmt.entities.enums.Genre;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateFilmRequest {
    UUID id;
    String title;
    String description;
    LocalDate releaseDate;
    LocalTime duration;
    Set<Genre> genres;
    MultipartFile image;
    MultipartFile video;
    Set<UUID> filmProfessionalIds;
}
