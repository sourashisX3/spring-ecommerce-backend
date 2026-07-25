package com.example.ecommerce_backend.modules.discount.entity;

import com.example.ecommerce_backend.modules.user.entity.User;
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
@Table(name = "discounts")
public class Discount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discount_type_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private DiscountType discountType;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal discountValue;

    @Column(precision = 10, scale = 2)
    private BigDecimal minOrderAmount;

    @Column(precision = 10, scale = 2)
    private BigDecimal maxDiscount;

    @Column(nullable = false)
    @Builder.Default
    private boolean isActive = true;

    @Column(nullable = false)
    @Builder.Default
    private boolean isGlobal = false;

    private Instant validFrom;
    private Instant validUntil;

    private String description;

    private Instant createdAt;
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        this.uuid = UUID.randomUUID().toString();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }
}
