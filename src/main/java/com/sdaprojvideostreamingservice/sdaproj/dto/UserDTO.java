package com.sdaprojvideostreamingservice.sdaproj.dto;

import com.sdaprojvideostreamingservice.sdaproj.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long userId;
    private String name;
    private String email;
    private String profileImageUrl;
    private UserRole role;
}
