package com.example.ecommerce_backend.modules.shipping.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.util.UUID;

@Data @Entity @Builder @NoArgsConstructor @AllArgsConstructor
@Table(name = "shipping_carriers")
public class ShippingCarrier {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String uuid;
    @Column(unique = true, nullable = false)
    private String code;
    @Column(nullable = false)
    private String name;
    private String trackingUrlTemplate;
    @Column(nullable = false) @Builder.Default
    private boolean isActive = true;
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
    public void preUpdate() { this.updatedAt = Instant.now(); }
}
