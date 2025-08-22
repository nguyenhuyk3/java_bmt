package com.bmt.java_bmt.controllers.authentication;

import com.bmt.java_bmt.dto.APIResponse;
import com.bmt.java_bmt.dto.others.TokenPair;
import com.bmt.java_bmt.dto.requests.authentication.login.LoginRequest;
import com.bmt.java_bmt.dto.requests.authentication.registration.SendOTPRequest;
import com.bmt.java_bmt.services.authentication.ILoginService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/login")
@RequiredArgsConstructor
public class LoginController {
    ILoginService loginService;

    @PostMapping
    APIResponse<TokenPair> login(@RequestBody @Valid LoginRequest request) {
        TokenPair tokenPair = loginService.login(request);

        return APIResponse
                .<TokenPair>builder()
                .result(tokenPair)
                .build();
    }
}
