package com.bmt.java_bmt.dto.others;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenPair {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Long expiresIn;
    private Date accessTokenExpiresAt;
    private Date refreshTokenExpiresAt;
}
