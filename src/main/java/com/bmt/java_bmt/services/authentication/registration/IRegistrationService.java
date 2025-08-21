package com.bmt.java_bmt.services.authentication.registration;

import com.bmt.java_bmt.dto.APIResponse;
import com.bmt.java_bmt.dto.requests.authentication.registration.RegistrationRequest;
import com.bmt.java_bmt.dto.requests.authentication.registration.VerifyOTPRequest;
import com.bmt.java_bmt.dto.responses.authentication.registration.RegistrationResponse;

public interface IRegistrationService {
    APIResponse<RegistrationResponse> registration(RegistrationRequest request);
    APIResponse verifyOTP(VerifyOTPRequest request);
}
