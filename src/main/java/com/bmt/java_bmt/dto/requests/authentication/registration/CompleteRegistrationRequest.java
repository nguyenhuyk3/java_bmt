package com.bmt.java_bmt.dto.requests.authentication.registration;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.bmt.java_bmt.dto.others.PersonalInformation;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompleteRegistrationRequest {
    @Email(message = "Email không hợp lệ")
    @NotBlank(message = "Email không được để trống")
    String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, max = 128, message = "Mật khẩu phải có từ {min} đến {max} ký tự")
    String password;

    @NotNull(message = "Thông tin cá nhân bắt buộc phải có")
    PersonalInformation personalInformation;
}
