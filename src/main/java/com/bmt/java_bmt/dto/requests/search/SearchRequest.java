package com.bmt.java_bmt.dto.requests.search;

import java.util.List;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SearchRequest {
    String query;
    List<String> genres;
    String actorName;
    String directorName;
}
