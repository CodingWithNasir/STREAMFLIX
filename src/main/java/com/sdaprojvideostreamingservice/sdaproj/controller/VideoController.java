package com.sdaprojvideostreamingservice.sdaproj.controller;

import com.sdaprojvideostreamingservice.sdaproj.Patterns.command.Useractioninvoker;
import com.sdaprojvideostreamingservice.sdaproj.Patterns.command.pausevideocommand;
import com.sdaprojvideostreamingservice.sdaproj.Patterns.command.playvideocommand;
import com.sdaprojvideostreamingservice.sdaproj.dto.CategoryNodeDto;
import com.sdaprojvideostreamingservice.sdaproj.dto.StreamResponse;
import com.sdaprojvideostreamingservice.sdaproj.dto.VideoDto;
import com.sdaprojvideostreamingservice.sdaproj.model.StreamQuality;
import com.sdaprojvideostreamingservice.sdaproj.security.SecurityUtils;
import com.sdaprojvideostreamingservice.sdaproj.service.UserService;
import com.sdaprojvideostreamingservice.sdaproj.service.VideoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/videos")
public class VideoController {

    private final VideoService videoService;
    private final UserService userService;
    private final Useractioninvoker invoker;
    private final playvideocommand playCommand;
    private final pausevideocommand pauseCommand;

    public VideoController(VideoService videoService, UserService userService,
                           Useractioninvoker invoker, playvideocommand playCommand,
                           pausevideocommand pauseCommand) {
        this.videoService = videoService;
        this.userService = userService;
        this.invoker = invoker;
        this.playCommand = playCommand;
        this.pauseCommand = pauseCommand;
    }

    @GetMapping
    public List<VideoDto> list() {
        return videoService.listAll(SecurityUtils.currentUserId());
    }

    @GetMapping("/categories")
    public List<CategoryNodeDto> categories() {
        return videoService.getCategoryTree(SecurityUtils.currentUserId());
    }

    @GetMapping("/{id}")
    public VideoDto get(@PathVariable Long id) {
        return videoService.getById(id, SecurityUtils.currentUserId());
    }

    @GetMapping("/{id}/stream")
    public StreamResponse stream(@PathVariable Long id,
                                 @RequestParam(defaultValue = "HD") StreamQuality quality) {
        Long userId = SecurityUtils.currentUserId();
        return videoService.getStream(id, userId, quality, userService.findById(userId));
    }

    @GetMapping("/player/play")
    public Map<String, String> play() {
        invoker.invoke(playCommand);
        return Map.of("status", "playing");
    }

    @GetMapping("/player/pause")
    public Map<String, String> pause() {
        invoker.invoke(pauseCommand);
        return Map.of("status", "paused");
    }
}
