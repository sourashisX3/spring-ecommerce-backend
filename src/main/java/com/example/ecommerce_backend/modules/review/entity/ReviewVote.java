package com.example.ecommerce_backend.modules.review.entity;

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
@Table(name = "review_votes", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "review_id"})
})
public class ReviewVote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Review review;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;

    @Column(nullable = false)
    private String voteType;

    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        this.uuid = UUID.randomUUID().toString();
        this.createdAt = Instant.now();
    }
}
