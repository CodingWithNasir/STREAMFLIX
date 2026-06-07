package com.sdaprojvideostreamingservice.sdaproj.repository;

import com.sdaprojvideostreamingservice.sdaproj.model.watchlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WatchlistRespository extends JpaRepository<watchlist, Long> {
    List<watchlist> findByUserId(Long userId);
    Optional<watchlist> findByUserIdAndVideoId(Long userId, Long videoId);
    boolean existsByUserIdAndVideoId(Long userId, Long videoId);
    void deleteByUserIdAndVideoId(Long userId, Long videoId);
}
