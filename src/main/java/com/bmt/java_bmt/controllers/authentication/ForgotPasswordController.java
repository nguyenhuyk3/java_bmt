package com.bmt.java_bmt.controllers.authentication;

import com.bmt.java_bmt.dto.APIResponse;
import com.bmt.java_bmt.dto.requests.authentication.forgotPassword.CompleteForgotPasswordRequest;
import com.bmt.java_bmt.dto.requests.authentication.forgotPassword.SendForgotPasswordOTPRequest;
import com.bmt.java_bmt.dto.requests.authentication.forgotPassword.VerifyForgotPasswordOTPRequest;
import com.bmt.java_bmt.dto.responses.authentication.forgotPassword.CompleteForgotPasswordResponse;
import com.bmt.java_bmt.services.authentication.IForgotPasswordService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/forgot-password")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ForgotPasswordController {
    IForgotPasswordService forgotPasswordService;

    @PostMapping("/send-otp")
    APIResponse<String> sendOTP(@RequestBody @Valid SendForgotPasswordOTPRequest request) {
        String message = forgotPasswordService.sendForgotPasswordOTP(request);

        return APIResponse.<String>builder()
                .result(message)
                .build();
    }

    @PostMapping("/verify-otp")
    APIResponse<String> verifyOTP(@RequestBody @Valid VerifyForgotPasswordOTPRequest request) {
        String message = forgotPasswordService.verifyForgotPasswordOTP(request);

        return APIResponse.<String>builder()
                .result(message)
                .build();
    }

    @PutMapping("/complete-forgot-password")
    APIResponse<CompleteForgotPasswordResponse> completeForgotPassword(@RequestBody @Valid CompleteForgotPasswordRequest request) {
        var result = forgotPasswordService.completeFortgotPassword(request);

        return APIResponse.<CompleteForgotPasswordResponse>builder()
                .result(result)
                .build();
    }
}
