package com.bmt.java_bmt.dto.requests.authentication.login;

import jakarta.validation.constraints.NotBlank;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RefreshAccessTokenRequest {
    @NotBlank(message = "Token không được để trống")
    String token;
}
