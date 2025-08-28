package com.bmt.java_bmt.dto.responses.filmProfessional;

import java.util.UUID;

import com.bmt.java_bmt.entities.enums.Job;

public interface IFilmProfessionalView {
    UUID getId();

    Job getJob();
}
