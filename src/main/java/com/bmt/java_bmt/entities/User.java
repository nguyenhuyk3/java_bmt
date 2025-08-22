package com.bmt.java_bmt.entities;

import com.bmt.java_bmt.entities.enums.Role;
import com.bmt.java_bmt.entities.enums.Source;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "u_id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "u_email", length = 64, nullable = false)
    private String email;

    @Column(name = "u_password", length = 128, nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "u_role", nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "u_source", nullable = false)
    private Source source;

    @CreationTimestamp
    @Column(name = "u_created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "u_updated_at", nullable = false)
    private Instant updatedAt;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "pi_id", referencedColumnName = "pi_id", nullable = false)
    private PersonalInformation personalInformation;

//    @OneToMany(mappedBy = "orderedBy")
//    private Set<Order> orders;
}
