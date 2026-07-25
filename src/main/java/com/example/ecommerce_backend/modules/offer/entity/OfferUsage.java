package com.example.ecommerce_backend.modules.offer.entity;

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
@Table(name = "offer_usages")
public class OfferUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offer_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Offer offer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;

    private Long orderId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal discountAmount;

    private Instant usedAt;

    @PrePersist
    public void prePersist() {
        this.uuid = UUID.randomUUID().toString();
        this.usedAt = Instant.now();
    }
}
