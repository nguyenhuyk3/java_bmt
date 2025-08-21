package com.bmt.java_bmt.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "other_film_informations")
public class OtherFilmInformation {
    @Id
    @Column(name = "f_id", length = 36)
    private UUID id;

    @Column(name = "ofi_poster_url", columnDefinition = "TEXT", nullable = false)
    private String posterUrl;

    @Column(name = "ofi_trailer_url", columnDefinition = "TEXT", nullable = false)
    private String trailerUrl;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "f_id")
    private Film film;
}