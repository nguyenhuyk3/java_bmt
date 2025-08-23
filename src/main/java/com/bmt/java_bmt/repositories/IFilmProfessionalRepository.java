package com.bmt.java_bmt.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bmt.java_bmt.entities.FilmProfessional;

public interface IFilmProfessionalRepository extends JpaRepository<FilmProfessional, UUID> {}
