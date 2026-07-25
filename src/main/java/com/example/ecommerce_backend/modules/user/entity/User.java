package com.example.ecommerce_backend.modules.user.entity;

import com.example.ecommerce_backend.modules.role.entity.Role;
import com.example.ecommerce_backend.modules.userpermission.entity.UserPermission;
import jakarta.persistence.*;
import lombok.*;
import lombok.Builder.Default;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String uuid;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String dialCode;

    @Column(unique = true, nullable = false)
    private String phoneNumber;
    private String profilePictureUrl;

    @Embedded
    private UserAddress address;

    @Column(nullable = false)
    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Role role;
    private boolean isActive;
    private boolean isEmailVerified;
    private boolean isPhoneVerified;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<UserPermission> userPermissions = new ArrayList<>();

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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Stream<String> rolePermissions = role != null && role.getPermissions() != null
                ? role.getPermissions().stream().map(p -> p.getPermissionName())
                : Stream.empty();
        Stream<String> userPermissions = this.userPermissions != null
                ? this.userPermissions.stream()
                    .filter(up -> up.getPermission() != null)
                    .map(up -> up.getPermission().getPermissionName())
                : Stream.empty();
        return Stream.concat(rolePermissions, userPermissions)
                .distinct()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isActive;
    }
}
