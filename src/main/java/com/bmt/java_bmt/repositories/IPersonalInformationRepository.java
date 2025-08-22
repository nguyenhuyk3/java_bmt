package com.bmt.java_bmt.repositories;

import com.bmt.java_bmt.entities.PersonalInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IPersonalInformationRepository extends JpaRepository<PersonalInformation, UUID> {
}
