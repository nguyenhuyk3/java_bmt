package com.bmt.java_bmt.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bmt.java_bmt.dto.responses.filmProfessional.IFilmProfessionalView;
import com.bmt.java_bmt.entities.FilmProfessional;

public interface IFilmProfessionalRepository extends JpaRepository<FilmProfessional, UUID> {
    @Query("SELECT f.id AS id, f.job AS job FROM FilmProfessional f")
    List<IFilmProfessionalView> findAllIdAndJobs();
}
