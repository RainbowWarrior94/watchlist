package com.nasta.watchlist.repository;

import com.nasta.watchlist.model.WatchlistMember;
import com.nasta.watchlist.model.WatchlistMemberId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WatchlistMemberRepository extends JpaRepository<WatchlistMember, WatchlistMemberId> {
    Optional<WatchlistMember> findByWatchlistIdAndUserId(Long watchlistId, Long userId);
}