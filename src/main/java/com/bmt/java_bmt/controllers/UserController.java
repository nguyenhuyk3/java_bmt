package com.bmt.java_bmt.controllers;

import org.springframework.web.bind.annotation.*;

import com.bmt.java_bmt.dto.APIResponse;
import com.bmt.java_bmt.dto.responses.user.GetPersonalInformationResponse;
import com.bmt.java_bmt.services.IUserService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    IUserService userService;

    @GetMapping()
    APIResponse<GetPersonalInformationResponse> getPersonalInformation() {
        GetPersonalInformationResponse personalInformationResponse = userService.getUserInformation();

        return APIResponse.<GetPersonalInformationResponse>builder()
                .result(personalInformationResponse)
                .build();
    }
}
