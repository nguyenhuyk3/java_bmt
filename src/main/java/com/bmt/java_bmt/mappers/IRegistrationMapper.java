package com.bmt.java_bmt.mappers;

import com.bmt.java_bmt.dto.requests.authentication.registration.CompleteRegistrationRequest;
import com.bmt.java_bmt.dto.responses.authentication.registration.RegistrationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IRegistrationMapper {
    RegistrationResponse toRegistrationResponse(CompleteRegistrationRequest request);
}
