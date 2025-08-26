package com.bmt.java_bmt.dto.others;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class FilmDocument {
    @JsonProperty("id")
    private String id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("releaseDate")
    //    @JsonFormat(pattern = "yyyy-MM-dd")
    private String releaseDate; // Lưu dạng String "YYYY-MM-DD"

    @JsonProperty("duration")
    //    @JsonFormat(pattern = "HH:mm:ss")
    private String duration; // Lưu dạng String "HH:mm:ss"

    @JsonProperty("posterUrl")
    private String posterUrl;

    @JsonProperty("trailerUrl")
    private String trailerUrl;

    @JsonProperty("genres")
    private List<String> genres;

    @JsonProperty("actors")
    private List<SimplePersonInformation> actors;

    @JsonProperty("directors")
    private List<SimplePersonInformation> directors;
}
