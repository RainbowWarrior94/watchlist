package com.nasta.watchlist.repository;

import com.nasta.watchlist.model.Title;
import com.nasta.watchlist.model.enums.MediaType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TitleRepository extends JpaRepository<Title, Long> {
    Optional<Title> findByTmdbIdAndMediaType(Integer tmdbId, MediaType mediaType);
}
