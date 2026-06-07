package com.sdaprojvideostreamingservice.sdaproj.repository;

import com.sdaprojvideostreamingservice.sdaproj.model.WatchHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WatchHistoryRepository extends JpaRepository<WatchHistory, Long> {
    List<WatchHistory> findByUserIdOrderByWatchDateDesc(Long userId);
    Optional<WatchHistory> findByUserIdAndVideoId(Long userId, Long videoId);
}
