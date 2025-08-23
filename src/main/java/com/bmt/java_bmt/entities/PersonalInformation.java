package com.bmt.java_bmt.entities;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.*;

import com.bmt.java_bmt.entities.enums.Sex;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "personal_information")
public class PersonalInformation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "pi_id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "pi_first_name", length = 64, nullable = false)
    private String firstName;

    @Column(name = "pi_last_name", length = 64, nullable = false)
    private String lastName;

    @Column(name = "pi_date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(name = "pi_sex", nullable = false)
    private Sex sex;

    @Column(name = "pi_avatar_url", columnDefinition = "TEXT", nullable = true)
    private String avatarUrl;
}
