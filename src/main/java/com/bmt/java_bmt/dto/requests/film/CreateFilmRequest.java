package com.bmt.java_bmt.dto.requests.film;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;

import jakarta.validation.constraints.*;

import com.bmt.java_bmt.dto.others.OtherFilmInformation;
import com.bmt.java_bmt.entities.enums.Genre;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateFilmRequest {
    @NotBlank(message = "Tiêu đề phim không được để trống")
    @Size(min = 6, message = "Tiêu đề phải tối thiểu {min} kí tự")
    String title;

    @NotBlank(message = "Mô tả phim không được để trống")
    @Size(min = 50, message = "Mô tả phim phải tối thiểu quá {min} kí tự")
    String description;

    @NotNull(message = "Ngày phát hành không được để trống")
    @PastOrPresent(message = "Ngày phát hành không thể ở tương lai")
    LocalDate releaseDate;

    @NotNull(message = "Thời lượng phim không được để trống")
    LocalTime duration;

    @NotEmpty(message = "Phim phải có ít nhất 1 thể loại")
    Set<Genre> genres;

    @NotNull(message = "Thông tin khác của phim không được để trống")
    OtherFilmInformation otherFilmInformation;

    @NotEmpty(message = "Phim phải có ít nhất 1 người tham gia sản xuất")
    Set<UUID> filmProfessionalIds;
}
