package com.J2EE.BlogNMCVC.service;

import com.J2EE.BlogNMCVC.constant.TokenType;
import com.J2EE.BlogNMCVC.constant.UserStatus;
import com.J2EE.BlogNMCVC.dto.request.ChangeUsernameRequest;
import com.J2EE.BlogNMCVC.dto.response.UploadResponse;
import com.J2EE.BlogNMCVC.dto.response.UserResponse;
import com.J2EE.BlogNMCVC.mapper.UserMapper;
import com.J2EE.BlogNMCVC.model.Token;
import com.J2EE.BlogNMCVC.model.User;
import com.J2EE.BlogNMCVC.repository.UserRepository;
import com.J2EE.BlogNMCVC.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UploadService uploadService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private MailService mailService;

    private Optional<User> getCurrentUser() {
        if (!UserUtils.isAuthenticated()) {
            return Optional.empty();
        }

        UUID userId = UserUtils.getCurrentUserId();

        if (userId == null) {
            throw new UsernameNotFoundException("User Not Found");
        }

        return userRepository.findByIdAndDeletedAtIsNull(userId);
    }

    public UserResponse getMe() {
        if (!UserUtils.isAuthenticated()) {
            return null;
        }

        UUID userId = UserUtils.getCurrentUserId();

        if (userId == null) {
            throw new UsernameNotFoundException("User Not Found");
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("Cannot find user with id: " + userId));

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

    public UserResponse getPublicUserByUsername(String username) {
        User user = userRepository.findByUsernameAndDeletedAtIsNull(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return userMapper.toUserResponse(user);
    }

    public void updateName(String name) {
        User user = getCurrentUser()
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found"));

        String newName = name.isEmpty() ? user.getName() : name.trim();

        user.setName(newName);
        userRepository.save(user);
    }

    public void updateAvatar(MultipartFile avatar) {
        User user = getCurrentUser()
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found"));

        String avatarUrl = user.getAvatar();

        if (avatar != null && !avatar.isEmpty()) {
            UploadResponse uploadResponse = uploadService.uploadImage(avatar, "avatars");

            if (uploadResponse == null) {
                throw new RuntimeException("Upload Avatar Failed!");
            }

            avatarUrl = uploadResponse.getSecureUrl();
        }

        user.setAvatar(avatarUrl);
        userRepository.save(user);

    }

    public void updateBio(String bio) {
        User user = getCurrentUser()
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found"));

        user.setBio(bio == null ? null : bio.trim());
        userRepository.save(user);
    }

    public void changeUsername(ChangeUsernameRequest req) {
        User user = getCurrentUser()
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found"));

        String newUsername = req.getUsername().trim();

        if (user.getUsername().equals(newUsername)) {
            throw new IllegalArgumentException("New username is old username");
        }

        if (userRepository.existsByUsername(newUsername)) {
            throw new IllegalArgumentException("Username is already taken!");
        }

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Passwords do not match!");
        }

        user.setUsername(newUsername);
        userRepository.save(user);

    }

    public void lockAccount() {
        User user = getCurrentUser()
                .orElseThrow(() -> new UsernameNotFoundException("Account is already locked!"));

        // CONFIRM EMAIL
        String token = tokenService.generateToken(TokenType.LOCK_ACCOUNT, 15, user);

        String verifyLink = "http://localhost:8080/auth/lock-account?token=" + token;
        mailService.sendVerifyEmail(user.getEmail(), user.getName(), verifyLink);
    }

    public void confirmLockAccount(String plainToken) {
        Token token = tokenService.getToken(plainToken, TokenType.LOCK_ACCOUNT);

        User user = token.getUser();

        user.setDeletedAt(LocalDateTime.now());
        userRepository.save(user);

        tokenService.deleteToken(token);
    }

    public String restoreAccount() {
        User user = getCurrentUser()
                .orElseThrow(() -> new UsernameNotFoundException("Account is already locken!"));

        user.setDeletedAt(null);
        userRepository.save(user);

        return "Restore Account successfully!";
    }
}
