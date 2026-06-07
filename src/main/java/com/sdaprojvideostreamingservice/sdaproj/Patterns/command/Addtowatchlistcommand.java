package com.sdaprojvideostreamingservice.sdaproj.Patterns.command;

import com.sdaprojvideostreamingservice.sdaproj.model.watchlist;
import com.sdaprojvideostreamingservice.sdaproj.repository.WatchlistRespository;
import org.springframework.stereotype.Component;

@Component
public class Addtowatchlistcommand implements command {

    private final WatchlistRespository watchlistRepository;
    private Long userId;
    private Long videoId;

    public Addtowatchlistcommand(WatchlistRespository watchlistRepository) {
        this.watchlistRepository = watchlistRepository;
    }

    public Addtowatchlistcommand withContext(Long userId, Long videoId) {
        this.userId = userId;
        this.videoId = videoId;
        return this;
    }

    @Override
    public void execute() {
        if (!watchlistRepository.existsByUserIdAndVideoId(userId, videoId)) {
            watchlistRepository.save(watchlist.builder().userId(userId).videoId(videoId).build());
        }
    }

    @Override
    public String getName() { return "add-to-watchlist"; }
}
