package com.sdaprojvideostreamingservice.sdaproj.service;

import com.sdaprojvideostreamingservice.sdaproj.Patterns.command.Addtowatchlistcommand;
import com.sdaprojvideostreamingservice.sdaproj.Patterns.command.Removefromwatchlistcommand;
import com.sdaprojvideostreamingservice.sdaproj.Patterns.command.Useractioninvoker;
import com.sdaprojvideostreamingservice.sdaproj.dto.VideoDto;
import com.sdaprojvideostreamingservice.sdaproj.repository.VideoRepository;
import com.sdaprojvideostreamingservice.sdaproj.repository.WatchlistRespository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WatchlistService {

    private final WatchlistRespository watchlistRepository;
    private final VideoRepository videoRepository;
    private final Useractioninvoker invoker;
    private final Addtowatchlistcommand addCommand;
    private final Removefromwatchlistcommand removeCommand;

    public WatchlistService(WatchlistRespository watchlistRepository, VideoRepository videoRepository,
                            Useractioninvoker invoker, Addtowatchlistcommand addCommand,
                            Removefromwatchlistcommand removeCommand) {
        this.watchlistRepository = watchlistRepository;
        this.videoRepository = videoRepository;
        this.invoker = invoker;
        this.addCommand = addCommand;
        this.removeCommand = removeCommand;
    }

    public List<VideoDto> list(Long userId) {
        return watchlistRepository.findByUserId(userId).stream()
                .map(w -> videoRepository.findById(w.getVideoId()).orElse(null))
                .filter(v -> v != null)
                .map(v -> VideoService.toDto(v, true))
                .toList();
    }

    public void add(Long userId, Long videoId) {
        invoker.invoke(addCommand.withContext(userId, videoId));
    }

    public void remove(Long userId, Long videoId) {
        invoker.invoke(removeCommand.withContext(userId, videoId));
    }
}
