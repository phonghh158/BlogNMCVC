package com.J2EE.BlogNMCVC.mapper;

import com.J2EE.BlogNMCVC.dto.response.UserResponse;
import com.J2EE.BlogNMCVC.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .name(user.getName())
                .avatar(user.getAvatar())
                .bio(user.getBio())
                .role(user.getRole())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .deletedAt(user.getDeletedAt())
                .build();
    }
}