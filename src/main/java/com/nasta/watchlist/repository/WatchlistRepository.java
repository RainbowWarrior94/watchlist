package com.nasta.watchlist.repository;

import com.nasta.watchlist.model.Watchlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WatchlistRepository extends JpaRepository<Watchlist, Long> {

    // все списки, в которых состоит пользователь (в любой роли)
    @Query("select m.watchlist from WatchlistMember m where m.user.id = :userId order by m.watchlist.createdAt desc")
    List<Watchlist> findAllByMemberUserId(@Param("userId") Long userId);
}