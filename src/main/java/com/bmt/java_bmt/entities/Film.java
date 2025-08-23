package com.bmt.java_bmt.entities;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.bmt.java_bmt.entities.enums.Genre;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "films")
public class Film {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "f_id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "f_title", columnDefinition = "TEXT", nullable = false)
    private String title;

    @Column(name = "f_description", columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "f_release_date", nullable = false)
    private LocalDate releaseDate;

    @Column(name = "f_duration", nullable = false)
    private LocalTime duration;

    @CreationTimestamp
    @Column(name = "f_created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "f_updated_at", nullable = false)
    private Instant updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "u_changed_by", nullable = false)
    private User changedBy;

    // Mapping cho bảng film_genres
    @ElementCollection(targetClass = Genre.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "film_genres", joinColumns = @JoinColumn(name = "f_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "fg_genre", nullable = false)
    private Set<Genre> genres = new HashSet<>();

    // Quan hệ 1-1 với OtherFilmInformation
    @OneToOne(mappedBy = "film", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private OtherFilmInformation otherFilmInformation;

    @OneToMany(mappedBy = "film")
    private Set<FilmPerson> filmPeople;

    @OneToMany(mappedBy = "film")
    private Set<Showtime> showtimes;
}
