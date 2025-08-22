package com.bmt.java_bmt.implementations;

import com.bmt.java_bmt.dto.others.TokenPair;
import com.bmt.java_bmt.dto.responses.authentication.jwt.TokenRefreshResponse;
import com.bmt.java_bmt.entities.User;
import com.bmt.java_bmt.exceptions.AppException;
import com.bmt.java_bmt.exceptions.ErrorCode;
import com.bmt.java_bmt.repositories.IUserRepository;
import com.bmt.java_bmt.services.authentication.IJwtTokenService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JwtTokemImpl implements IJwtTokenService {
    @Value("${jwt.access.secret}")
    String accessTokenSecret;

    @Value("${jwt.refresh.secret}")
    String refreshTokenSecret;

    @Value("${jwt.access.expiration}") // 15 minutes = 900 seconds
    long accessTokenExpirationSeconds;

    @Value("${jwt.refresh.expiration:604800}") // 7 days = 604800 seconds
    long refreshTokenExpirationSeconds;

    final String ACCESS_TOKEN_TYPE = "accesss";
    final String REFRESH_TOKEN_TYPE = "refresh";

    @Override
    public String generateAccessToken(UUID userId, String role) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(accessTokenSecret.getBytes());
            Instant now = Instant.now();
            Instant expiration = now.plus(accessTokenExpirationSeconds, ChronoUnit.SECONDS);
            Map<String, Object> claims = new HashMap<>();

            claims.put("userId", userId);
            claims.put("role", role);
            claims.put("type", ACCESS_TOKEN_TYPE);

            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(userId.toString())
                    .setIssuedAt(Date.from(now))
                    .setExpiration(Date.from(expiration))
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();
        } catch (Exception e) {
            throw new AppException(ErrorCode.FAILED_TO_CREATE_ACCESS_TOKEN);
        }
    }

    @Override
    public String generateRefreshToken(UUID userId) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(refreshTokenSecret.getBytes());
            Instant now = Instant.now();
            Instant expiration = now.plus(refreshTokenExpirationSeconds, ChronoUnit.SECONDS);
            Map<String, Object> claims = new HashMap<>();

            claims.put("userId", userId);
            claims.put("type", REFRESH_TOKEN_TYPE);
//            claims.put("random", UUID.randomUUID().toString());

            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(userId.toString())
                    .setIssuedAt(Date.from(now))
                    .setExpiration(Date.from(expiration))
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();
        } catch (Exception e) {
            throw new AppException(ErrorCode.FAILED_TO_CREATE_REFRESH_TOKEN);
        }
    }

    @Override
    public TokenPair generateTokenPair(User user) {
        try {
            String accessToken = generateAccessToken(user.getId(), user.getRole().name());
            String refreshToken = generateRefreshToken(user.getId());
            Instant now = Instant.now();
            Instant accessTokenExpires = now.plus(accessTokenExpirationSeconds, ChronoUnit.SECONDS);
            Instant refreshTokenExpires = now.plus(refreshTokenExpirationSeconds, ChronoUnit.SECONDS);

            return TokenPair.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .expiresIn(accessTokenExpirationSeconds)
                    .accessTokenExpiresAt(Date.from(accessTokenExpires))
                    .refreshTokenExpiresAt(Date.from(refreshTokenExpires))
                    .build();
        } catch (Exception e) {
            throw new AppException(ErrorCode.FAILED_TO_CREATE_TOKEN_PAIR);
        }
    }

    @Override
    public Claims verifyAccessToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(accessTokenSecret.getBytes());
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            if (!ACCESS_TOKEN_TYPE.equals(claims.get("type"))) {
                throw new AppException(ErrorCode.TOKEN_TYPE_DONT_MATCH);
            }

            return claims;
        } catch (ExpiredJwtException e) {
            throw new AppException(ErrorCode.ACCESS_TOKEN_EXPIRED);
        } catch (MalformedJwtException e) {
            throw new AppException(ErrorCode.ACCESS_TOKEN_INVALID);
        } catch (UnsupportedJwtException e) {
            throw new AppException(ErrorCode.ACCESS_TOKEN_UNSUPPORTED);
        } catch (IllegalArgumentException e) {
            throw new AppException(ErrorCode.ACCESS_TOKEN_EMPTY);
        }
    }

    @Override
    public Claims verifyRefreshToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(refreshTokenSecret.getBytes());
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            if (!REFRESH_TOKEN_TYPE.equals(claims.get("type"))) {
                throw new AppException(ErrorCode.TOKEN_TYPE_DONT_MATCH);
            }

            return claims;
        } catch (ExpiredJwtException e) {
            throw new AppException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        } catch (MalformedJwtException e) {
            throw new AppException(ErrorCode.REFRESH_TOKEN_INVALID);
        } catch (UnsupportedJwtException e) {
            throw new AppException(ErrorCode.REFRESH_TOKEN_UNSUPPORTED);
        } catch (IllegalArgumentException e) {
            throw new AppException(ErrorCode.REFRESH_TOKEN_EMPTY);
        }
    }

    @Override
    public TokenRefreshResponse refreshAccessToken(String refreshToken, IUserRepository userRepository) {
        try {
            // Xác nhận refresh token
            Claims claims = verifyRefreshToken(refreshToken);
            UUID userId = UUID.fromString(claims.getSubject());
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_ID_DOESNT_EXIST));
            // Tạo access token mới
            String newAccessToken = generateAccessToken(user.getId(), user.getRole().name());
            Instant now = Instant.now();
            Instant accessTokenExpires = now.plus(accessTokenExpirationSeconds, ChronoUnit.SECONDS);

            return TokenRefreshResponse.builder()
                    .accessToken(newAccessToken)
                    .tokenType("Bearer")
                    .expiresIn(accessTokenExpirationSeconds)
                    .accessTokenExpiresAt(Date.from(accessTokenExpires))
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi refresh token: " + e.getMessage(), e);
        }
    }

    @Override
    public UUID extractUserId(String token) {
        try {
            Claims claims = verifyAccessToken(token);

            return UUID.fromString(claims.getSubject());
        } catch (Exception e) {
            throw new AppException(ErrorCode.CANNOT_EXTRACT_USER_ID);
        }
    }

    @Override
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = verifyAccessToken(token);

            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
}
