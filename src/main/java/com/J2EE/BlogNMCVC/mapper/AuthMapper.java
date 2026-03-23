package com.J2EE.BlogNMCVC.mapper;

import com.J2EE.BlogNMCVC.constant.UserRole;
import com.J2EE.BlogNMCVC.constant.UserStatus;
import com.J2EE.BlogNMCVC.dto.auth.request.RegisterRequest;
import com.J2EE.BlogNMCVC.dto.auth.response.AuthResponse;
import com.J2EE.BlogNMCVC.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper {
    @Autowired
    private UserMapper userMapper;

    public User toUser(RegisterRequest request, String encodedPassword) {
        return User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(encodedPassword)
                .name(request.getName())
                .role(UserRole.USER)
                .status(UserStatus.INACTIVE)
                .build();
    }

    public AuthResponse toAuthResponse(User user, String msg) {
        return AuthResponse.builder()
                .message(msg)
                .userResponse(userMapper.toUserResponse(user))
                .build();
    }
}
