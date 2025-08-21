package com.bmt.java_bmt.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "contact_information")
public class ContactInformation {
    @Id
    @Column(name = "u_id", length = 36)
    private UUID id;

    @Column(name = "ci_email", length = 64, nullable = false)
    private String email;

    @Column(name = "ci_phone_number", length = 10, nullable = false)
    private String phoneNumber;

    @OneToOne(fetch = FetchType.LAZY)
    /*
        @MapsId làm gì?
        - @MapsId nói với JPA rằng:
            + Khóa chính của entity con (ContactInformation)
            sẽ được ánh xạ (map) từ khóa chính của entity cha (User).
            + Bạn không cần sinh ra một ID riêng cho ContactInformation, mà ID chính là ID của User.
    */
    @MapsId
    @JoinColumn(name = "u_id")
    private User user;
}
