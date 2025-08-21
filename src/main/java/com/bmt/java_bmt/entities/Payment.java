package com.bmt.java_bmt.entities;

import com.bmt.java_bmt.entities.enums.PaymentMethod;
import com.bmt.java_bmt.entities.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "p_id", length = 36, nullable = false)
    private UUID id;

    // Chú ý: Lưu trữ tiền dưới dạng String không phải là cách tốt nhất.
    // Nên cân nhắc dùng BigDecimal hoặc Long (lưu dưới đơn vị nhỏ nhất, vd: xu)
    @Column(name = "p_amount", length = 32, nullable = false)
    private String amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "p_status", nullable = false)
    private PaymentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "p_method", nullable = false)
    private PaymentMethod method;

    @Column(name = "p_transaction_id", length = 128)
    private String transactionId;

    @Column(name = "p_error_message", columnDefinition = "TEXT", nullable = false)
    private String errorMessage;

    @CreationTimestamp
    @Column(name = "p_created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "o_id", nullable = false, unique = true)
    private Order order;
}
