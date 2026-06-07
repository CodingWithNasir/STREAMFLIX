package com.sdaprojvideostreamingservice.sdaproj.Patterns.command;

import com.sdaprojvideostreamingservice.sdaproj.repository.WatchlistRespository;
import org.springframework.stereotype.Component;

@Component
public class Removefromwatchlistcommand implements command {

    private final WatchlistRespository watchlistRepository;
    private Long userId;
    private Long videoId;

    public Removefromwatchlistcommand(WatchlistRespository watchlistRepository) {
        this.watchlistRepository = watchlistRepository;
    }

    public Removefromwatchlistcommand withContext(Long userId, Long videoId) {
        this.userId = userId;
        this.videoId = videoId;
        return this;
    }

    @Override
    public void execute() {
        watchlistRepository.deleteByUserIdAndVideoId(userId, videoId);
    }

    @Override
    public String getName() { return "remove-from-watchlist"; }
}
