package com.bmt.java_bmt.dto.others;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SimplePersonInformation {
    //    @JsonProperty("id")
    //    String id;

    @JsonProperty("fullName")
    String fullName;
}
