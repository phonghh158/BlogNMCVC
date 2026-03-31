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
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping()
public class AuthController {

    @Autowired
    private AuthService authService;

    @GetMapping("/test-role")
    @ResponseBody
    public Object testRole(Authentication authentication) {
        return authentication.getAuthorities();
    }

    @GetMapping("/authentication")
    public String authentication(Model model) {
        if (!model.containsAttribute("registerRequest")) {
            model.addAttribute("registerRequest", new RegisterRequest());
        }
        return "auth/authentication";
    }

    @PostMapping("/register")
    public String register(
            @Valid @ModelAttribute("registerRequest") RegisterRequest req,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            return "auth/authentication";
        }

        try {
            AuthResponse response = authService.register(req);
            model.addAttribute("registerSuccess", response.getMessage());
            model.addAttribute("registerRequest", new RegisterRequest());
        } catch (IllegalArgumentException e) {
            model.addAttribute("registerError", e.getMessage());
            model.addAttribute("registerRequest", req);
        }

        return "auth/authentication";
    }

    @GetMapping("/auth/verify")
    public ResponseEntity<AuthResponse> verifyEmail(@RequestParam String token) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.verifyUser(token));
    }

    @PostMapping("/auth/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestParam
            @NotBlank(message = "Email is required.")
            @Email(message = "Email is invalid.")
            String email
    ) {
        authService.resetPassword(email);
        return ResponseEntity.status(HttpStatus.OK).body("Password reset link has been sent.");
    }

    @PostMapping("/auth/update-password")
    public ResponseEntity<String> updatePassword(@Valid @ModelAttribute ResetPasswordRequest req) {
        authService.updatePassword(req);
        return ResponseEntity.status(HttpStatus.OK).body("Password changed successfully!");
    }
}