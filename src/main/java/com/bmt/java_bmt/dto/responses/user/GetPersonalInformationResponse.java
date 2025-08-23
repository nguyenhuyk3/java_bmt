package com.bmt.java_bmt.dto.responses.user;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import com.bmt.java_bmt.entities.enums.Sex;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetPersonalInformationResponse {
    @NotBlank(message = "Họ không được để trống")
    String firstName;

    @NotBlank(message = "Tên không được để trống")
    String lastName;

    @Past(message = "Ngày sinh phải nhỏ hơn ngày hiện tại")
    LocalDate dateOfBirth;

    @NotNull(message = "Giới tính là bắt buộc")
    Sex sex;

    String avatarUrl;
}
