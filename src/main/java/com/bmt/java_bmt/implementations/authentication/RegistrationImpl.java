package com.bmt.java_bmt.implementations.authentication;

import com.bmt.java_bmt.dto.APIResponse;
import com.bmt.java_bmt.dto.requests.authentication.registration.RegistrationRequest;
import com.bmt.java_bmt.dto.requests.authentication.registration.SendOTPRequest;
import com.bmt.java_bmt.dto.requests.authentication.registration.VerifyOTPRequest;
import com.bmt.java_bmt.dto.responses.authentication.registration.RegistrationResponse;
import com.bmt.java_bmt.services.authentication.IRegistrationService;

public class RegistrationImpl implements IRegistrationService {
    @Override
    public APIResponse sendOTP(SendOTPRequest request) {
        return null;
    }

    @Override
    public APIResponse verifyOTP(VerifyOTPRequest request) {
        return null;
    }

    @Override
    public APIResponse<RegistrationResponse> registration(RegistrationRequest request) {
        return null;
    }
}

