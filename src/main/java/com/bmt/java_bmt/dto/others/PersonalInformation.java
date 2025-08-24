package com.bmt.java_bmt.dto.others;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import com.bmt.java_bmt.entities.enums.Sex;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PersonalInformation {
    @NotBlank(message = "Tên không được để trống")
    String fullName;

    @Past(message = "Ngày sinh phải nhỏ hơn ngày hiện tại")
    LocalDate dateOfBirth;

    @NotNull(message = "Giới tính là bắt buộc")
    Sex sex;

    String avatarUrl;
}
