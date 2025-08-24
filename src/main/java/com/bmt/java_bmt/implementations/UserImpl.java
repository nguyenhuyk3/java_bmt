package com.bmt.java_bmt.implementations;

import java.util.UUID;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.bmt.java_bmt.dto.responses.user.GetPersonalInformationResponse;
import com.bmt.java_bmt.exceptions.AppException;
import com.bmt.java_bmt.exceptions.ErrorCode;
import com.bmt.java_bmt.repositories.IUserRepository;
import com.bmt.java_bmt.services.IUserService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class UserImpl implements IUserService {
    IUserRepository userRepository;

    @Override
    public GetPersonalInformationResponse getUserInformation() {
        var userIdString =
                SecurityContextHolder.getContext().getAuthentication().getName();
        UUID userId = UUID.fromString(userIdString);
        var user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_ID_DOESNT_EXIST));
        var personalInformation = user.getPersonalInformation();

        return GetPersonalInformationResponse.builder()
                .fullName(personalInformation.getFullName())
                .dateOfBirth(personalInformation.getDateOfBirth())
                .sex(personalInformation.getSex())
                .avatarUrl(personalInformation.getAvatarUrl())
                .build();
    }
}
