package com.bmt.java_bmt.dto.others;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OtherFilmInformation {
    String posterUrl;
    String trailerUrl;
}
