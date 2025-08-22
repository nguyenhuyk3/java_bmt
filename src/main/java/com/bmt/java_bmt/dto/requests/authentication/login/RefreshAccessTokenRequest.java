package com.bmt.java_bmt.dto.requests.authentication.login;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RefreshAccessTokenRequest {
    @NotBlank(message = "Token không được để trống")
    String token;
}
