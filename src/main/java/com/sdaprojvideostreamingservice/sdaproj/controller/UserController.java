package com.sdaprojvideostreamingservice.sdaproj.controller;

import com.sdaprojvideostreamingservice.sdaproj.dto.HistoryUpdateRequest;
import com.sdaprojvideostreamingservice.sdaproj.dto.UserDTO;
import com.sdaprojvideostreamingservice.sdaproj.dto.VideoDto;
import com.sdaprojvideostreamingservice.sdaproj.security.SecurityUtils;
import com.sdaprojvideostreamingservice.sdaproj.service.HistoryService;
import com.sdaprojvideostreamingservice.sdaproj.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final HistoryService historyService;

    public UserController(UserService userService, HistoryService historyService) {
        this.userService = userService;
        this.historyService = historyService;
    }

    @GetMapping("/me")
    public UserDTO me() {
        return userService.getUser(SecurityUtils.currentUserId());
    }

    @PutMapping("/me")
    public UserDTO update(@RequestBody Map<String, String> body) {
        return userService.updateProfile(
                SecurityUtils.currentUserId(),
                body.get("name"),
                body.get("profileImageUrl"));
    }

    @GetMapping("/me/history")
    public List<VideoDto> history() {
        return historyService.getContinueWatching(SecurityUtils.currentUserId());
    }

    @PutMapping("/me/history/{videoId}")
    public void updateHistory(@PathVariable Long videoId, @RequestBody HistoryUpdateRequest request) {
        historyService.updateProgress(SecurityUtils.currentUserId(), videoId, request);
    }
}
