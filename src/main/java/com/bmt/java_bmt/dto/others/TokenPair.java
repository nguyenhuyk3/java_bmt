package com.bmt.java_bmt.dto.others;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenPair {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private String role;
    //    private Long expiresIn;
    private Date accessTokenExpiresAt;
    private Date refreshTokenExpiresAt;
}
