package com.sdaprojvideostreamingservice.sdaproj.controller;

import com.sdaprojvideostreamingservice.sdaproj.dto.AdminAnalyticsDto;
import com.sdaprojvideostreamingservice.sdaproj.dto.UserDTO;
import com.sdaprojvideostreamingservice.sdaproj.dto.VideoDto;
import com.sdaprojvideostreamingservice.sdaproj.repository.UserRepository;
import com.sdaprojvideostreamingservice.sdaproj.repository.VideoRepository;
import com.sdaprojvideostreamingservice.sdaproj.repository.WatchlistRespository;
import com.sdaprojvideostreamingservice.sdaproj.service.PaymentService;
import com.sdaprojvideostreamingservice.sdaproj.service.SubscriptionService;
import com.sdaprojvideostreamingservice.sdaproj.service.UserService;
import com.sdaprojvideostreamingservice.sdaproj.service.VideoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final VideoRepository videoRepository;
    private final WatchlistRespository watchlistRepository;
    private final SubscriptionService subscriptionService;
    private final PaymentService paymentService;
    private final VideoService videoService;

    public AdminController(UserRepository userRepository, VideoRepository videoRepository,
                           WatchlistRespository watchlistRepository, SubscriptionService subscriptionService,
                           PaymentService paymentService, VideoService videoService) {
        this.userRepository = userRepository;
        this.videoRepository = videoRepository;
        this.watchlistRepository = watchlistRepository;
        this.subscriptionService = subscriptionService;
        this.paymentService = paymentService;
        this.videoService = videoService;
    }

    @GetMapping("/analytics")
    public AdminAnalyticsDto analytics() {
        return AdminAnalyticsDto.builder()
                .totalUsers(userRepository.count())
                .totalVideos(videoRepository.count())
                .activeSubscribers(subscriptionService.countActive())
                .totalWatchlistItems(watchlistRepository.count())
                .totalRevenue(paymentService.totalRevenue())
                .build();
    }

    @GetMapping("/users")
    public List<UserDTO> users() {
        return userRepository.findAll().stream().map(UserService::toDto).toList();
    }

    @PostMapping("/videos")
    @ResponseStatus(HttpStatus.CREATED)
    public VideoDto createVideo(@RequestBody VideoDto dto) {
        return videoService.create(dto);
    }

    @PutMapping("/videos/{id}")
    public VideoDto updateVideo(@PathVariable Long id, @RequestBody VideoDto dto) {
        return videoService.update(id, dto);
    }

    @DeleteMapping("/videos/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteVideo(@PathVariable Long id) {
        videoService.delete(id);
    }
}
