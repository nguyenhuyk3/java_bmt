package com.bmt.java_bmt.services.authentication;

import com.bmt.java_bmt.dto.requests.authentication.registration.CompleteRegistrationRequest;
import com.bmt.java_bmt.dto.requests.authentication.registration.SendOTPRequest;
import com.bmt.java_bmt.dto.requests.authentication.registration.VerifyOTPRequest;
import com.bmt.java_bmt.dto.responses.authentication.registration.RegistrationResponse;

public interface IRegistrationService {
    String sendOTP(SendOTPRequest request);
    String verifyOTP(VerifyOTPRequest request);
    RegistrationResponse completeRegistration(CompleteRegistrationRequest request);
}
