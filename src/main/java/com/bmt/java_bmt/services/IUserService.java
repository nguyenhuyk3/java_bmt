package com.bmt.java_bmt.services;

import com.bmt.java_bmt.dto.responses.user.GetPersonalInformationResponse;

public interface IUserService {
    GetPersonalInformationResponse getUserInformation();
}
