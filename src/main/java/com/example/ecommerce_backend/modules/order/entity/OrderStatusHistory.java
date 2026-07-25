package com.example.ecommerce_backend.modules.order.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_status_history")
public class OrderStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_status_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private OrderStatus fromStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_status_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private OrderStatus toStatus;

    @Column(nullable = false)
    private String changedBy;

    @Column(columnDefinition = "TEXT")
    private String reason;

    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        this.uuid = UUID.randomUUID().toString();
        this.createdAt = now;
    }
}
