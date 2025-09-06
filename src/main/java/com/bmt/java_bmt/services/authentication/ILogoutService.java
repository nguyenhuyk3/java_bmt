package com.bmt.java_bmt.services.authentication;

import com.bmt.java_bmt.dto.requests.authentication.logout.LogoutRequest;

public interface ILogoutService {
    String logout(LogoutRequest request);
}
