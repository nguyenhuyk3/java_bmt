package com.bmt.java_bmt.dto.responses.authentication.jwt;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
