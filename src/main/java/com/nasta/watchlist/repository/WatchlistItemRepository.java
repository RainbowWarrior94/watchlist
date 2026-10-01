package com.nasta.watchlist.repository;

import com.nasta.watchlist.model.WatchlistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WatchlistItemRepository extends JpaRepository<WatchlistItem, Long> {

    @Query("select i from WatchlistItem i join fetch i.title where i.watchlist.id = :watchlistId order by i.addedAt desc")
    List<WatchlistItem> findAllWithTitleByWatchlistId(@Param("watchlistId") Long watchlistId);

    boolean existsByWatchlist_IdAndTitle_Id(Long watchlistId, Long titleId);

    // всегда ищите элемент вместе со списком, иначе можно изменить элемент чужого списка
    Optional<WatchlistItem> findByIdAndWatchlist_Id(Long id, Long watchlistId);
}
