package com.bmt.java_bmt.services.authentication;

import java.util.Date;
import java.util.UUID;

import com.bmt.java_bmt.dto.others.TokenPair;
import com.bmt.java_bmt.dto.responses.authentication.jwt.TokenRefreshResponse;
import com.bmt.java_bmt.entities.User;
import com.bmt.java_bmt.repositories.IUserRepository;

import io.jsonwebtoken.Claims;

public interface IJwtTokenService {
    /**
     * Tạo access token
     *
     * @param userId ID của user
     * @param role   Role của user
     * @return Access token
     */
    String generateAccessToken(UUID userId, String role);

    /**
     * Tạo refresh token
     *
     * @param userId ID của user
     * @return Refresh token
     */
    String generateRefreshToken(UUID userId);

    /**
     * Tạo cả access token và refresh token
     *
     * @param user User object
     * @return TokenPair object chứa cả hai token
     */
    TokenPair generateTokenPair(User user);

    /**
     * Xác thực access token
     *
     * @param token Access token cần verify
     * @return Claims nếu token hợp lệ
     */
    Claims verifyAccessToken(String token);

    /**
     * Xác thực refresh token
     *
     * @param token Refresh token cần verify
     * @return Claims nếu token hợp lệ
     */
    Claims verifyRefreshToken(String token);

    /**
     * Làm mới access token bằng refresh token
     *
     * @param refreshToken    Refresh token
     * @param userRepository Repository để lấy thông tin user
     * @return TokenRefreshResponse chứa access token mới
     */
    TokenRefreshResponse refreshAccessToken(String refreshToken, IUserRepository userRepository);

    /**
     * Extract user ID từ token
     *
     * @param token JWT token
     * @return User ID
     */
    UUID extractUserId(String token);

    Date extractRefreshTokenExpiration(String token);

    /**
     * Kiểm tra token có hết hạn hay không
     *
     * @param token JWT token
     * @return true nếu hết hạn
     */
    boolean isAccessTokenExpired(String token);
}
