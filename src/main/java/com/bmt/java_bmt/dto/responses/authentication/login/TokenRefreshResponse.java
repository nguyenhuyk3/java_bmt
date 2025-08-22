package com.bmt.java_bmt.dto.responses.authentication.login;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenRefreshResponse {
    private String accessToken;
    private String tokenType;
    private Long expiresIn;
    private Date accessTokenExpiresAt;
}