package com.bmt.java_bmt.dto.requests.authentication.registration;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class SendOTPRequest {
    @Email(message = "Email không hợp lệ")
    @NotBlank(message = "Email không được để trống")
    String email;
}
