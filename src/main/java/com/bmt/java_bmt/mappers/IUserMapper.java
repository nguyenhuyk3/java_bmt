package com.bmt.java_bmt.mappers;

import com.bmt.java_bmt.dto.others.PersonalInformation;
import com.bmt.java_bmt.dto.requests.authentication.registration.CompleteRegistrationRequest;
import com.bmt.java_bmt.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface IUserMapper {
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "source", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "personalInformation", ignore = true)
    User toUser(CompleteRegistrationRequest request);

    com.bmt.java_bmt.entities.PersonalInformation toPersonalInformation(PersonalInformation request);
}
