package com.J2EE.BlogNMCVC.service;

import com.J2EE.BlogNMCVC.constant.TokenType;
import com.J2EE.BlogNMCVC.constant.UserStatus;
import com.J2EE.BlogNMCVC.dto.auth.request.RegisterRequest;
import com.J2EE.BlogNMCVC.dto.auth.request.ResetPasswordRequest;
import com.J2EE.BlogNMCVC.dto.auth.response.AuthResponse;
import com.J2EE.BlogNMCVC.mapper.AuthMapper;
import com.J2EE.BlogNMCVC.model.Token;
import com.J2EE.BlogNMCVC.model.User;
import com.J2EE.BlogNMCVC.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuthService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthMapper authMapper;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private MailService mailService;

    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        if (userRepository.existsByUsername(req.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        if (!req.getPassword().equals(req.getConfirmPassword())) {
            throw new IllegalArgumentException("Password confirmation does not match");
        }

        String encodedPassword = passwordEncoder.encode(req.getPassword());

        User user = authMapper.toUser(req, encodedPassword);

        user = userRepository.save(user);

        // VERIFY EMAIL
        String token = tokenService.generateToken(TokenType.VERIFY_EMAIL, 60, user);

        String verifyLink = "http://localhost:8080/auth/verify?token=" + token;
        mailService.sendVerifyEmail(user.getEmail(), user.getName(), verifyLink);

        return authMapper.toAuthResponse(user, "Register new account successfully!");
    }

    public AuthResponse verifyUser(String plainToken) {
        Token token = tokenService.getToken(plainToken, TokenType.VERIFY_EMAIL);

        User user = token.getUser();
        user.setStatus(UserStatus.ACTIVE);

        userRepository.save(user);
        tokenService.deleteToken(token);

        return authMapper.toAuthResponse(user, "Email verified successfully!");
    }

    // Chưa test
    public void resetPassword(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String token = tokenService.generateToken(TokenType.RESET_PASSWORD, 15, user);

        String verifyLink = "http://localhost:8080/auth/reset-password?token=" + token;

        mailService.sendResetPasswordEmail(user.getEmail(), user.getName(), verifyLink);
    }

    public void updatePassword(ResetPasswordRequest req) {
        Token token = tokenService.getToken(req.getToken(), TokenType.RESET_PASSWORD);

        if (!req.getNewPassword().equals(req.getConfirmNewPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        String encodedPassword = passwordEncoder.encode(req.getNewPassword());

        User user = token.getUser();
        user.setPassword(encodedPassword);

        userRepository.save(user);
        tokenService.deleteToken(token);
    }

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        User user = userRepository.findByEmailOrUsername(identifier, identifier)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (user.getStatus() == UserStatus.INACTIVE) {
            throw new DisabledException("User is inactive.");
        } else if (user.getStatus() == UserStatus.BANNED) {
            throw new LockedException("User is banned.");
        }

        if (user.getDeletedAt() != null) {
            throw new DisabledException("Account was locked.");
        }

        return new org.springframework.security.core.userdetails.User(
                user.getId().toString(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }
}