package com.bmt.java_bmt.services.authentication;

import com.bmt.java_bmt.dto.others.TokenPair;
import com.bmt.java_bmt.dto.requests.authentication.login.LoginRequest;
import com.bmt.java_bmt.dto.requests.authentication.login.RefreshAccessTokenRequest;

public interface ILoginService {
    TokenPair login(LoginRequest request);

    String refreshAccessToken(RefreshAccessTokenRequest request);
}
