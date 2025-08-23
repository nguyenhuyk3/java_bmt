package com.bmt.java_bmt.entities;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.bmt.java_bmt.entities.enums.Job;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "film_professionals")
public class FilmProfessional {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "fpf_id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "fpf_nationality", length = 64, nullable = false)
    private String nationality;

    @Enumerated(EnumType.STRING)
    @Column(name = "fpf_job", nullable = false)
    private Job job;

    @CreationTimestamp
    @Column(name = "fpf_created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "fpf_updated_at", nullable = false)
    private Instant updatedAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pi_id", referencedColumnName = "pi_id", nullable = false)
    private PersonalInformation personalInformation;

    @ManyToMany(mappedBy = "filmProfessionals", fetch = FetchType.LAZY)
    private Set<Film> films = new HashSet<>();
}
