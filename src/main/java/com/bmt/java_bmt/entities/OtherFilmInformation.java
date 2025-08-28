package com.bmt.java_bmt.entities;

import java.util.UUID;

import jakarta.persistence.*;

import lombok.*;

@Getter
@Setter
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
    	+ Khóa chính của entity con (OtherFilmInformation)
    	sẽ được ánh xạ (map) từ khóa chính của entity cha (Film).
    	+ Bạn không cần sinh ra một ID riêng cho OtherFilmInformation, mà ID chính là ID của Film.
    */
    @MapsId
    @JoinColumn(name = "f_id")
    @ToString.Exclude // QUAN TRỌNG: Loại trừ khỏi toString()
    @EqualsAndHashCode.Exclude // QUAN TRỌNG: Loại trừ khỏi equals() và hashCode()
    private Film film;
}
