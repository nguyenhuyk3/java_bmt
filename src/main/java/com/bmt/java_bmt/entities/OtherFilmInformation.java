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
     /*
        @MapsId làm gì?
        - @MapsId nói với JPA rằng:
            + Khóa chính của entity con (ContactInformation)
            sẽ được ánh xạ (map) từ khóa chính của entity cha (User).
            + Bạn không cần sinh ra một ID riêng cho ContactInformation, mà ID chính là ID của User.
    */
    @MapsId
    @JoinColumn(name = "f_id")
    private Film film;
}