package com.sdaprojvideostreamingservice.sdaproj.service;

import com.sdaprojvideostreamingservice.sdaproj.dto.AuthResponse;
import com.sdaprojvideostreamingservice.sdaproj.dto.LoginRequest;
import com.sdaprojvideostreamingservice.sdaproj.dto.RegisterRequest;
import com.sdaprojvideostreamingservice.sdaproj.dto.UserDTO;
import com.sdaprojvideostreamingservice.sdaproj.exception.ApiException;
import com.sdaprojvideostreamingservice.sdaproj.model.User;
import com.sdaprojvideostreamingservice.sdaproj.model.UserRole;
import com.sdaprojvideostreamingservice.sdaproj.repository.UserRepository;
import com.sdaprojvideostreamingservice.sdaproj.security.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final Map<String, Long> resetTokens = new ConcurrentHashMap<>();

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
        return buildAuthResponse(user);
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ApiException(HttpStatus.CONFLICT, "Email already registered");
        }
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.USER)
                .build();
        return buildAuthResponse(userRepository.save(user));
    }

    public String forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "No account with that email"));
        String token = jwtTokenProvider.generateResetToken(user.getUserId());
        resetTokens.put(token, user.getUserId());
        return token;
    }

    public void resetPassword(String token, String newPassword) {
        Long userId = resetTokens.get(token);
        if (userId == null) {
            try {
                userId = jwtTokenProvider.getUserId(token);
            } catch (Exception e) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid or expired reset token");
            }
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);
        resetTokens.remove(token);
    }

    public UserDTO getUser(Long userId) {
        return toDto(findById(userId));
    }

    public UserDTO updateProfile(Long userId, String name, String profileImageUrl) {
        User user = findById(userId);
        if (name != null && !name.isBlank()) user.setName(name);
        if (profileImageUrl != null) user.setProfileImageUrl(profileImageUrl);
        user.setUpdatedAt(Instant.now());
        return toDto(userRepository.save(user));
    }

    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private AuthResponse buildAuthResponse(User user) {
        String token = jwtTokenProvider.generateToken(user.getUserId(), user.getEmail(), user.getRole().name());
        return AuthResponse.builder().token(token).user(toDto(user)).build();
    }

    public static UserDTO toDto(User user) {
        return UserDTO.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .profileImageUrl(user.getProfileImageUrl())
                .role(user.getRole())
                .build();
    }
}
