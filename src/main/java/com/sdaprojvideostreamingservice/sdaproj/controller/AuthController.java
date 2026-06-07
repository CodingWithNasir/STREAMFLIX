package com.sdaprojvideostreamingservice.sdaproj.controller;

import com.sdaprojvideostreamingservice.sdaproj.dto.AuthResponse;
import com.sdaprojvideostreamingservice.sdaproj.dto.ForgotPasswordRequest;
import com.sdaprojvideostreamingservice.sdaproj.dto.LoginRequest;
import com.sdaprojvideostreamingservice.sdaproj.dto.MessageResponse;
import com.sdaprojvideostreamingservice.sdaproj.dto.RegisterRequest;
import com.sdaprojvideostreamingservice.sdaproj.dto.ResetPasswordRequest;
import com.sdaprojvideostreamingservice.sdaproj.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.register(request));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        String token = userService.forgotPassword(request.getEmail());
        return ResponseEntity.ok(new MessageResponse("Reset token generated. Use it within 1 hour."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        userService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok(new MessageResponse("Password updated successfully"));
    }
}
