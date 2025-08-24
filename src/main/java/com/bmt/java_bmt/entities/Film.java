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
    @ToString.Exclude // Loại trừ khỏi toString()
    @EqualsAndHashCode.Exclude // Loại trừ khỏi equals() và hashCode()
    private User changedBy;

    // Mapping cho bảng film_genres
    @ElementCollection(targetClass = Genre.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "film_genres", joinColumns = @JoinColumn(name = "f_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "fg_genre", nullable = false)
    private Set<Genre> genres = new HashSet<>();

    // Quan hệ 1-1 với OtherFilmInformation
    /*
    Cascade là gì?
    	- Khi bạn thao tác với entity parent,
    cascade quyết định Hibernate có tự động thao tác entity con liên quan hay không.
    	- Ví dụ bạn có:
    		+ Film là parent, OtherFilmInformation là child.
    		+ Cascade sẽ tự động áp dụng các hành động trên Film cho OtherFilmInformation.
    mappedBy là gì?
    	- mappedBy là thuộc tính dùng trong JPA để chỉ ra “phía nào là owner” của quan hệ.
    	- Nó chỉ định tên trường trong entity kia mà nắm giữ khóa ngoại (foreign key).
    	- Entity có mappedBy không phải owner, Hibernate sẽ không tạo cột/khóa ngoại mới ở bảng đó,
    mà dựa vào bảng bên kia để quản lý.
    */
    @OneToOne(mappedBy = "film", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude // QUAN TRỌNG: Loại trừ khỏi toString()
    @EqualsAndHashCode.Exclude // QUAN TRỌNG: Loại trừ khỏi equals() và hashCode()
    private OtherFilmInformation otherFilmInformation;

    @ManyToMany(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "film_people",
            joinColumns = @JoinColumn(name = "f_id"),
            inverseJoinColumns = @JoinColumn(name = "fpf_id"))
    @ToString.Exclude // Loại trừ khỏi toString()
    @EqualsAndHashCode.Exclude // Loại trừ khỏi equals() và hashCode()
    private Set<FilmProfessional> filmProfessionals = new HashSet<>();

    @OneToMany(mappedBy = "film")
    @ToString.Exclude // Loại trừ khỏi toString()
    @EqualsAndHashCode.Exclude // Loại trừ khỏi equals() và hashCode()
    private Set<Showtime> showtimes;

    public void setOtherFilmInformation(OtherFilmInformation otherFilmInformation) {
        if (otherFilmInformation == null) {
            if (this.otherFilmInformation != null) {
                this.otherFilmInformation.setFilm(null);
            }
        } else {
            otherFilmInformation.setFilm(this);
        }

        this.otherFilmInformation = otherFilmInformation;
    }
}
