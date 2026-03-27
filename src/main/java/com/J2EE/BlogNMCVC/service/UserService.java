package com.J2EE.BlogNMCVC.service;

import com.J2EE.BlogNMCVC.dto.request.ChangeUsernameRequest;
import com.J2EE.BlogNMCVC.dto.response.UserResponse;
import com.J2EE.BlogNMCVC.mapper.UserMapper;
import com.J2EE.BlogNMCVC.model.User;
import com.J2EE.BlogNMCVC.repository.UserRepository;
import com.J2EE.BlogNMCVC.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserResponse getMe() {
        UUID id = UserUtils.getCurrentUserId();
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));

        return userMapper.toUserResponse(user);
    }

    public UserResponse getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + id));

        return userMapper.toUserResponse(user);
    }

    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return userMapper.toUserResponse(user);
    }

    public String updateName(UUID id, String name) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));

        user.setName(name);
        userRepository.save(user);
        return "Update Name successfully!";
    }

    public String updateAvatar(UUID id, String avatar) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));

        user.setAvatar(avatar);
        userRepository.save(user);
        return "Update Avatar successfully!";
    }

    public String updateBio(UUID id, String bio) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));

        user.setBio(bio);
        userRepository.save(user);
        return "Update Bio successfully!";
    }

    public String changeUsername(UUID id, ChangeUsernameRequest req) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));

        if (user.getUsername().equals(req.getUsername())) {
            throw new IllegalArgumentException("New username is old username");
        }

        if (userRepository.existsByUsername(req.getUsername())) {
            throw new IllegalArgumentException("Username is already taken!");
        }

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Passwords do not match!");
        }

        user.setUsername(req.getUsername());
        userRepository.save(user);

        return "Change Username successfully! New Username: " + req.getUsername();
    }

    public String lockAccount(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));

        if (!userRepository.existsByIdAndDeletedAtIsNull(id)) {
            throw new UsernameNotFoundException("Account is already locken!");
        }

        user.setDeletedAt(LocalDateTime.now());
        userRepository.save(user);

        return "Lock Account successfully!";
    }

    public String restoreAccount(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));

        if (userRepository.existsByIdAndDeletedAtIsNull(id)) {
            throw new UsernameNotFoundException("Account is already opened!");
        }

        user.setDeletedAt(null);
        userRepository.save(user);

        return "Restore Account successfully!";
    }
}
