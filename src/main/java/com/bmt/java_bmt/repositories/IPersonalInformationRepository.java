package com.bmt.java_bmt.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bmt.java_bmt.entities.PersonalInformation;

@Repository
public interface IPersonalInformationRepository extends JpaRepository<PersonalInformation, UUID> {}
