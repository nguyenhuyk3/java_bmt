package com.bmt.java_bmt.dto.responses.authentication.registration;

import com.bmt.java_bmt.dto.requests.authentication.registration.PersonalInformation;
import com.bmt.java_bmt.entities.enums.Role;
import com.bmt.java_bmt.entities.enums.Source;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class RegistrationResponse {
    String email;
    String password;
    Role role;
    Source source;
    PersonalInformation personalInformation;
}
