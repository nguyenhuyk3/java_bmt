package com.bmt.java_bmt.services;

import java.util.List;

import com.bmt.java_bmt.dto.responses.filmProfessional.GetFilmProfessionalResponse;

public interface IFilmProfessionalService {
    List<GetFilmProfessionalResponse> getAll();
}
