package com.bmt.java_bmt.dto.responses.authentication.registration;

import com.bmt.java_bmt.dto.requests.authentication.registration.PersonalInformation;
import com.bmt.java_bmt.entities.enums.Role;
import com.bmt.java_bmt.entities.enums.Source;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegistrationResponse {
    String email;
    String password;
    Role role;
    Source source;
    PersonalInformation personalInformation;
}
