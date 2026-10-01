package com.nasta.watchlist.model;

import com.nasta.watchlist.model.enums.MemberRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "invitations")
@Getter
@NoArgsConstructor
public class Invitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "watchlist_id", nullable = false)
    private Watchlist watchlist;

    @Column(nullable = false, unique = true, length = 64)
    private String token;

    // только EDITOR или VIEWER, это же проверяется CHECK-ограничением в БД
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MemberRole role;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public Invitation(Watchlist watchlist, String token, MemberRole role, User createdBy, Instant expiresAt) {
        if (role == MemberRole.OWNER) {
            throw new IllegalArgumentException("Приглашение не может выдавать роль OWNER");
        }
        this.watchlist = watchlist;
        this.token = token;
        this.role = role;
        this.createdBy = createdBy;
        this.expiresAt = expiresAt;
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }
}
