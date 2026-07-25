package com.example.ecommerce_backend.modules.wallet.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "wallet_transactions")
public class WalletTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Wallet wallet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private WalletTransactionType type;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal balanceBefore;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal balanceAfter;

    private String referenceType;

    private Long referenceId;

    private String description;

    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        this.uuid = UUID.randomUUID().toString();
        this.createdAt = Instant.now();
    }
}
