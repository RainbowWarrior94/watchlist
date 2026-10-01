package com.nasta.watchlist.repository;

import com.nasta.watchlist.model.WatchlistMember;
import com.nasta.watchlist.model.WatchlistMemberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

// Проверка роли: memberRepository.findById(new WatchlistMemberId(watchlistId, userId))
public interface WatchlistMemberRepository extends JpaRepository<WatchlistMember, WatchlistMemberId> {

    // участники списка сразу с данными пользователей (без N+1)
    @Query("select m from WatchlistMember m join fetch m.user where m.watchlist.id = :watchlistId")
    List<WatchlistMember> findAllWithUserByWatchlistId(@Param("watchlistId") Long watchlistId);
}