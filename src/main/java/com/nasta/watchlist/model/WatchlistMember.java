package com.nasta.watchlist.model;

import com.nasta.watchlist.model.enums.MemberRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "watchlist_members")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WatchlistMember {
    @EmbeddedId
    private WatchlistMemberId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("watchlistId")
    @JoinColumn(name = "watchlist_id")
    private Watchlist watchlist;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MemberRole role;

    @Column(name = "joined_at", nullable = false)
    private Instant joinedAt = Instant.now();

    public WatchlistMember(Watchlist watchlist, User user, MemberRole role) {
        this.id = new WatchlistMemberId(watchlist.getId(), user.getId());
        this.watchlist = watchlist;
        this.user = user;
        this.role = role;
    }
}
