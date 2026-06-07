package com.sdaprojvideostreamingservice.sdaproj.service;

import com.sdaprojvideostreamingservice.sdaproj.dto.HistoryUpdateRequest;
import com.sdaprojvideostreamingservice.sdaproj.dto.VideoDto;
import com.sdaprojvideostreamingservice.sdaproj.model.WatchHistory;
import com.sdaprojvideostreamingservice.sdaproj.repository.VideoRepository;
import com.sdaprojvideostreamingservice.sdaproj.repository.WatchHistoryRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class HistoryService {

    private final WatchHistoryRepository historyRepository;
    private final VideoRepository videoRepository;

    public HistoryService(WatchHistoryRepository historyRepository, VideoRepository videoRepository) {
        this.historyRepository = historyRepository;
        this.videoRepository = videoRepository;
    }

    public List<VideoDto> getContinueWatching(Long userId) {
        List<VideoDto> result = new ArrayList<>();
        for (WatchHistory h : historyRepository.findByUserIdOrderByWatchDateDesc(userId)) {
            if (!Boolean.TRUE.equals(h.getCompleted())) {
                videoRepository.findById(h.getVideoId())
                        .ifPresent(v -> result.add(VideoService.toDto(v, false)));
            }
        }
        return result;
    }

    public void updateProgress(Long userId, Long videoId, HistoryUpdateRequest request) {
        WatchHistory history = historyRepository.findByUserIdAndVideoId(userId, videoId)
                .orElse(WatchHistory.builder().userId(userId).videoId(videoId).build());
        if (request.getProgressSeconds() != null) history.setProgressSeconds(request.getProgressSeconds());
        if (request.getCompleted() != null) history.setCompleted(request.getCompleted());
        history.setWatchDate(Instant.now());
        historyRepository.save(history);
    }
}
