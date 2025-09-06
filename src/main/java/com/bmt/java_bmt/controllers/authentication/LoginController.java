package com.bmt.java_bmt.controllers.authentication;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bmt.java_bmt.dto.APIResponse;
import com.bmt.java_bmt.dto.others.TokenPair;
import com.bmt.java_bmt.dto.requests.authentication.login.LoginRequest;
import com.bmt.java_bmt.dto.requests.authentication.login.RefreshAccessTokenRequest;
import com.bmt.java_bmt.services.authentication.ILoginService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/auth/login")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LoginController {
    ILoginService loginService;

    @PostMapping
    APIResponse<TokenPair> login(@RequestBody @Valid LoginRequest request) {
        var result = loginService.login(request);

        return APIResponse.<TokenPair>builder().result(result).build();
    }

    @PostMapping("/refresh-access-token")
    APIResponse<String> refreshAccessToken(@RequestBody @Valid RefreshAccessTokenRequest request) {
        var result = loginService.refreshAccessToken(request);

        return APIResponse.<String>builder().result(result).build();
    }
}
