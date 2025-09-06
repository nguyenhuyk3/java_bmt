package com.bmt.java_bmt.implementations.authentication;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.bmt.java_bmt.dto.requests.authentication.logout.LogoutRequest;
import com.bmt.java_bmt.exceptions.AppException;
import com.bmt.java_bmt.exceptions.ErrorCode;
import com.bmt.java_bmt.services.IRedisService;
import com.bmt.java_bmt.services.authentication.IJwtTokenService;
import com.bmt.java_bmt.services.authentication.ILogoutService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class LogoutImpl implements ILogoutService {
    IRedisService redisService;
    IJwtTokenService jwtTokenService;

    @Override
    public String logout(LogoutRequest request) {
        // Kiểm tra token đã được đăng xuất trước đó hay chưa
        if (redisService.existsKey(request.getToken())) {
            throw new AppException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        // Lấy thời gian hết hạn của token
        Date tokenExpiration = jwtTokenService.extractRefreshTokenExpiration(request.getToken());
        long nowMillis = System.currentTimeMillis();
        long expirationMillis = tokenExpiration.getTime();
        // TTL = thời gian còn lại
        long ttlMinutes = (expirationMillis - nowMillis) / (60 * 1000);

        if (ttlMinutes <= 0) {
            throw new AppException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        redisService.save(request.getToken(), "BLACKLISTED", ttlMinutes, TimeUnit.MINUTES);

        return "Đăng xuất thành công";
    }
}
