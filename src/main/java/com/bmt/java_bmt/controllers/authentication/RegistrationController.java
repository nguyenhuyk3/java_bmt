package com.bmt.java_bmt.controllers.authentication;

import com.bmt.java_bmt.dto.APIResponse;
import com.bmt.java_bmt.dto.requests.authentication.registration.CompleteRegistrationRequest;
import com.bmt.java_bmt.dto.requests.authentication.registration.SendOTPRequest;
import com.bmt.java_bmt.dto.requests.authentication.registration.VerifyOTPRequest;
import com.bmt.java_bmt.dto.responses.authentication.registration.RegistrationResponse;
import com.bmt.java_bmt.services.authentication.IRegistrationService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/register")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RegistrationController {
    IRegistrationService registrationService;

    @PostMapping("/send-otp")
    APIResponse<String> sendOTP(@RequestBody @Valid SendOTPRequest request) {
        String message = registrationService.sendOTP(request);

        return APIResponse
                .<String>builder()
                .result(message)
                .build();
    }

    @PostMapping("/verify-registration-otp")
    APIResponse<String> verifyOTP(@RequestBody @Valid VerifyOTPRequest request) {
        String message = registrationService.verifyOTP(request);

        return APIResponse
                .<String>builder()
                .result(message)
                .build();
    }

    @PostMapping("/complete-registration")
    APIResponse<RegistrationResponse> verifyOTP(@RequestBody @Valid CompleteRegistrationRequest request) {
        RegistrationResponse result = registrationService.completeRegistration(request);

        return APIResponse
                .<RegistrationResponse>builder()
                .result(result)
                .build();
    }
}
