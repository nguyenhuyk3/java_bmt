package com.bmt.java_bmt.dto.responses.authentication.registration;

import com.bmt.java_bmt.dto.others.PersonalInformation;
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
    PersonalInformation personalInformation;
}
