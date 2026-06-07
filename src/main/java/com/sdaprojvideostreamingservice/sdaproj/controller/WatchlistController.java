package com.sdaprojvideostreamingservice.sdaproj.controller;

import com.sdaprojvideostreamingservice.sdaproj.dto.VideoDto;
import com.sdaprojvideostreamingservice.sdaproj.security.SecurityUtils;
import com.sdaprojvideostreamingservice.sdaproj.service.WatchlistService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/watchlist")
public class WatchlistController {

    private final WatchlistService watchlistService;

    public WatchlistController(WatchlistService watchlistService) {
        this.watchlistService = watchlistService;
    }

    @GetMapping
    public List<VideoDto> list() {
        return watchlistService.list(SecurityUtils.currentUserId());
    }

    @PostMapping("/{videoId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void add(@PathVariable Long videoId) {
        watchlistService.add(SecurityUtils.currentUserId(), videoId);
    }

    @DeleteMapping("/{videoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable Long videoId) {
        watchlistService.remove(SecurityUtils.currentUserId(), videoId);
    }
}
