package com.J2EE.BlogNMCVC.controller;

import com.J2EE.BlogNMCVC.dto.auth.request.RegisterRequest;
import com.J2EE.BlogNMCVC.dto.auth.request.ResetPasswordRequest;
import com.J2EE.BlogNMCVC.dto.auth.response.AuthResponse;
import com.J2EE.BlogNMCVC.service.AuthService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(req));
    }

    @GetMapping("/verify")
    public ResponseEntity<AuthResponse> verifyEmail(@RequestParam String token) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.verifyUser(token));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @RequestParam
            @NotBlank(message = "Email is required.")
            @Email(message = "Email is invalid.")
            String email)
    {
        authService.resetPassword(email);
        return ResponseEntity.status(HttpStatus.OK).body("Password reset link has been sent.");
    }

    @PostMapping("/update-password")
    public ResponseEntity<?> updatePassword(@Valid @RequestBody ResetPasswordRequest req) {
        authService.updatePassword(req);
        return ResponseEntity.status(HttpStatus.OK).body("Password changed successfully!");
    }
}