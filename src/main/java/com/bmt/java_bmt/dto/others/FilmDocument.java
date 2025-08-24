package com.bmt.java_bmt.dto.others;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilmDocument {
    private String id;
    private String title;
    private String description;
    private LocalDate releaseDate;
    private LocalTime duration;
    private String posterUrl;
    private String trailerUrl;
    private List<String> genres;
    private List<SimplePersonInformation> actors;
    private List<SimplePersonInformation> directors;
}
