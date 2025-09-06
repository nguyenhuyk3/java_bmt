package com.bmt.java_bmt.controllers.authentication;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bmt.java_bmt.dto.APIResponse;
import com.bmt.java_bmt.dto.requests.authentication.logout.LogoutRequest;
import com.bmt.java_bmt.services.authentication.ILogoutService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/auth/logout")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LogoutController {
    ILogoutService logoutService;

    @PostMapping
    APIResponse<String> logout(@RequestBody @Valid LogoutRequest request) {
        var result = logoutService.logout(request);

        return APIResponse.<String>builder().result(result).build();
    }
}
