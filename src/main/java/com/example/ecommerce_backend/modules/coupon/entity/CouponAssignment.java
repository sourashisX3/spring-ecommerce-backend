package com.example.ecommerce_backend.modules.coupon.entity;

import com.example.ecommerce_backend.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "coupon_assignments")
public class CouponAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Coupon coupon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;

    @Builder.Default
    private Integer usedCount = 0;

    private Instant assignedAt;

    @PrePersist
    public void prePersist() {
        this.uuid = UUID.randomUUID().toString();
        this.assignedAt = Instant.now();
    }
}
