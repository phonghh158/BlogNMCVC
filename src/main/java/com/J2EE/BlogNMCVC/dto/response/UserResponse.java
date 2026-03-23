package com.J2EE.BlogNMCVC.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.J2EE.BlogNMCVC.constant.UserRole;
import com.J2EE.BlogNMCVC.constant.UserStatus;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private UUID id;

    private String username;

    private String email;

    private String name;

    private String avatar;

    private String bio;

    private UserRole role;

    private UserStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime deletedAt;
}