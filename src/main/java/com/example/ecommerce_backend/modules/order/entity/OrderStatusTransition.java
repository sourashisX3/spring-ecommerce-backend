package com.example.ecommerce_backend.modules.order.entity;

import com.example.ecommerce_backend.modules.role.entity.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_status_transitions")
public class OrderStatusTransition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_status_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private OrderStatus fromStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_status_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private OrderStatus toStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "allowed_by_role_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Role allowedBy;

    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        this.uuid = UUID.randomUUID().toString();
        this.createdAt = Instant.now();
    }
}
