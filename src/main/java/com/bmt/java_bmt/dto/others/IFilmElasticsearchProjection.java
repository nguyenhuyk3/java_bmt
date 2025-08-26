package com.bmt.java_bmt.dto.others;

public interface IFilmElasticsearchProjection {
    String getId();

    String getTitle();

    String getDescription();

    String getReleaseDate();

    String getDuration();

    String getPosterUrl();

    String getTrailerUrl();

    String getGenres(); // Sẽ là một chuỗi JSON, ví dụ: '["action", "comedy"]'

    String getActors(); // Sẽ là một chuỗi JSON

    String getDirectors(); // Sẽ là một chuỗi JSON
}
