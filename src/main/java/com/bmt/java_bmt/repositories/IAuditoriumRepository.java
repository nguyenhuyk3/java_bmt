package com.bmt.java_bmt.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bmt.java_bmt.entities.Auditorium;

public interface IAuditoriumRepository extends JpaRepository<Auditorium, UUID> {
    List<Auditorium> findByCinemaId(UUID cinemaId);
}
