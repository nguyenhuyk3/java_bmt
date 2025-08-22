package com.bmt.java_bmt.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "film_people")
public class FilmPerson {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "fp_id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "f_id", nullable = false)
    private Film film;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mp_id", nullable = false)
    private MoviePerson moviePerson;
}