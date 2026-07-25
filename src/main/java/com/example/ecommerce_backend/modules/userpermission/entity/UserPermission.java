package com.example.ecommerce_backend.modules.userpermission.entity;

import com.example.ecommerce_backend.modules.permission.entity.Permission;
import com.example.ecommerce_backend.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_permissions", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "permission_id"}))
public class UserPermission {

    public enum Effect {GRANT, DENY}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Permission permission;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Effect effect;
}
