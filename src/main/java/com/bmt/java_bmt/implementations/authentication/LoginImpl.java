package com.bmt.java_bmt.implementations.authentication;

import com.bmt.java_bmt.dto.others.TokenPair;
import com.bmt.java_bmt.dto.requests.authentication.login.LoginRequest;
import com.bmt.java_bmt.entities.User;
import com.bmt.java_bmt.exceptions.AppException;
import com.bmt.java_bmt.exceptions.ErrorCode;
import com.bmt.java_bmt.repositories.IUserRepository;
import com.bmt.java_bmt.services.authentication.IJwtTokenService;
import com.bmt.java_bmt.services.authentication.ILoginService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class LoginImpl implements ILoginService {
    IUserRepository userRepository;
    IJwtTokenService jwtTokenService;
    PasswordEncoder passwordEncoder;

    @Override
    public TokenPair login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.PASSWORD_INCORRECT);
        }

        return jwtTokenService.generateTokenPair(user);
    }
}
