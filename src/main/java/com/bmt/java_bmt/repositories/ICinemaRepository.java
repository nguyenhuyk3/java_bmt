package com.bmt.java_bmt.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bmt.java_bmt.entities.Cinema;

public interface ICinemaRepository extends JpaRepository<Cinema, UUID> {}
