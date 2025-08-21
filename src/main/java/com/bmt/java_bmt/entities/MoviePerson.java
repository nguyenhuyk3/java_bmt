package com.bmt.java_bmt.entities;

import com.bmt.java_bmt.entities.enums.Job;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "movie_people")
public class MoviePerson {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "mp_id", length = 36, nullable = false)
    private UUID id;

    @Column(name = "mp_nationality", length = 64, nullable = false)
    private String nationality;

    @Enumerated(EnumType.STRING)
    @Column(name = "mp_job", nullable = false)
    private Job job;

    @CreationTimestamp
    @Column(name = "mp_created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "mp_updated_at")
    private LocalDateTime updatedAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pi_id", referencedColumnName = "pi_id", nullable = false)
    private PersonalInformation personalInformation;

    @OneToMany(mappedBy = "moviePerson")
    private Set<FilmPerson> filmPeople;
}
