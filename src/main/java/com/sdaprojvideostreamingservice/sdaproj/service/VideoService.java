package com.sdaprojvideostreamingservice.sdaproj.service;

import com.sdaprojvideostreamingservice.sdaproj.Patterns.bridge.streamingservice;
import com.sdaprojvideostreamingservice.sdaproj.Patterns.chain.StreamingAccessChain;
import com.sdaprojvideostreamingservice.sdaproj.Patterns.composite.categorycomposite;
import com.sdaprojvideostreamingservice.sdaproj.Patterns.composite.videoleaf;
import com.sdaprojvideostreamingservice.sdaproj.dto.CategoryNodeDto;
import com.sdaprojvideostreamingservice.sdaproj.dto.StreamResponse;
import com.sdaprojvideostreamingservice.sdaproj.dto.VideoDto;
import com.sdaprojvideostreamingservice.sdaproj.exception.ApiException;
import com.sdaprojvideostreamingservice.sdaproj.model.StreamQuality;
import com.sdaprojvideostreamingservice.sdaproj.model.User;
import com.sdaprojvideostreamingservice.sdaproj.model.VideoGenre;
import com.sdaprojvideostreamingservice.sdaproj.model.videoentity;
import com.sdaprojvideostreamingservice.sdaproj.repository.VideoRepository;
import com.sdaprojvideostreamingservice.sdaproj.repository.WatchlistRespository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class VideoService {

    private final VideoRepository videoRepository;
    private final WatchlistRespository watchlistRepository;
    private final streamingservice streamingService;
    private final StreamingAccessChain accessChain;

    public VideoService(VideoRepository videoRepository, WatchlistRespository watchlistRepository,
                        streamingservice streamingService, StreamingAccessChain accessChain) {
        this.videoRepository = videoRepository;
        this.watchlistRepository = watchlistRepository;
        this.streamingService = streamingService;
        this.accessChain = accessChain;
    }

    public List<VideoDto> listAll(Long userId) {
        Set<Long> watchlistIds = watchlistRepository.findByUserId(userId).stream()
                .map(w -> w.getVideoId()).collect(Collectors.toSet());
        return videoRepository.findAll().stream()
                .map(v -> toDto(v, watchlistIds.contains(v.getVideoId())))
                .toList();
    }

    public VideoDto getById(Long videoId, Long userId) {
        videoentity video = findEntity(videoId);
        boolean inList = watchlistRepository.existsByUserIdAndVideoId(userId, videoId);
        return toDto(video, inList);
    }

    public StreamResponse getStream(Long videoId, Long userId, StreamQuality quality, User user) {
        videoentity video = findEntity(videoId);
        accessChain.validateAccess(user, video);
        return streamingService.buildStream(videoId, video.getVideoUrl(), quality);
    }

    public List<CategoryNodeDto> getCategoryTree(Long userId) {
        Set<Long> watchlistIds = watchlistRepository.findByUserId(userId).stream()
                .map(w -> w.getVideoId()).collect(Collectors.toSet());
        Map<VideoGenre, categorycomposite> map = new LinkedHashMap<>();
        for (videoentity v : videoRepository.findAll()) {
            map.computeIfAbsent(v.getGenre(), g -> new categorycomposite(g.name()))
                    .add(new videoleaf(toDto(v, watchlistIds.contains(v.getVideoId()))));
        }
        return map.values().stream().map(c -> c.toNode()).toList();
    }

    public VideoDto create(VideoDto dto) {
        videoentity saved = videoRepository.save(fromDto(dto));
        return toDto(saved, false);
    }

    public VideoDto update(Long id, VideoDto dto) {
        videoentity existing = findEntity(id);
        existing.setTitle(dto.getTitle());
        existing.setDescription(dto.getDescription());
        existing.setGenre(dto.getGenre());
        existing.setDurationSeconds(dto.getDurationSeconds());
        existing.setReleaseYear(dto.getReleaseYear());
        existing.setRating(dto.getRating());
        existing.setAgeRating(dto.getAgeRating());
        existing.setThumbnailUrl(dto.getThumbnailUrl());
        existing.setVideoUrl(dto.getVideoUrl());
        existing.setType(dto.getType());
        existing.setIsPremium(dto.getIsPremium());
        return toDto(videoRepository.save(existing), false);
    }

    public void delete(Long id) {
        videoRepository.deleteById(id);
    }

    public videoentity findEntity(Long id) {
        return videoRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Video not found"));
    }

    public static VideoDto toDto(videoentity v, boolean inWatchlist) {
        return VideoDto.builder()
                .videoId(v.getVideoId())
                .title(v.getTitle())
                .description(v.getDescription())
                .genre(v.getGenre())
                .durationSeconds(v.getDurationSeconds())
                .releaseYear(v.getReleaseYear())
                .rating(v.getRating())
                .ageRating(v.getAgeRating())
                .thumbnailUrl(v.getThumbnailUrl())
                .videoUrl(v.getVideoUrl())
                .type(v.getType())
                .isPremium(v.getIsPremium())
                .inWatchlist(inWatchlist)
                .build();
    }

    private videoentity fromDto(VideoDto dto) {
        return videoentity.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .genre(dto.getGenre())
                .durationSeconds(dto.getDurationSeconds())
                .releaseYear(dto.getReleaseYear())
                .rating(dto.getRating())
                .ageRating(dto.getAgeRating())
                .thumbnailUrl(dto.getThumbnailUrl())
                .videoUrl(dto.getVideoUrl())
                .type(dto.getType())
                .isPremium(dto.getIsPremium() != null ? dto.getIsPremium() : false)
                .build();
    }
}
