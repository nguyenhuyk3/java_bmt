package com.bmt.java_bmt.dto.requests.search;

import java.util.List;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SearchRequest {
    String query;
    List<String> genres;
    String actorName;
    String directorName;
}
