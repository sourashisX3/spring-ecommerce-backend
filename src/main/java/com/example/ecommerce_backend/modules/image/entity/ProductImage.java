package com.example.ecommerce_backend.modules.image.entity;

import com.example.ecommerce_backend.modules.product.entity.Product;
import com.example.ecommerce_backend.modules.variant.entity.ProductVariant;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product_images")
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String uuid;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    @Builder.Default
    private boolean isPrimary = false;

    @Column(nullable = false)
    @Builder.Default
    private int sortOrder = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ProductVariant variant;

    @PrePersist
    public void prePersist() {
        this.uuid = UUID.randomUUID().toString();
    }
}
