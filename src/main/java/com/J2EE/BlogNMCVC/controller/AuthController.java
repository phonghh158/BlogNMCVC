package com.J2EE.BlogNMCVC.controller;

import com.J2EE.BlogNMCVC.dto.auth.request.RegisterRequest;
import com.J2EE.BlogNMCVC.dto.auth.request.ResetPasswordRequest;
import com.J2EE.BlogNMCVC.dto.auth.response.AuthResponse;
import com.J2EE.BlogNMCVC.dto.response.UserResponse;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
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
    public String verifyEmail(
            @RequestParam String token,
            RedirectAttributes redirectAttributes
    ) {
        try {
            authService.verifyUser(token);
            redirectAttributes.addFlashAttribute("successMessage", "Email verified successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/authentication";
    }

    @PostMapping("/auth/reset-password")
    public String resetPassword(
            @RequestParam
            @NotBlank(message = "Email is required.")
            @Email(message = "Email is invalid.")
            String email,
            RedirectAttributes redirectAttributes
    ) {
        try {
            authService.resetPassword(email);
            redirectAttributes.addFlashAttribute("successMessage", "Password reset link has been sent.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Nhà không tìm thấy Email của bạn, bạn vào nhà rồi đăng ký mới nhé.");
            return "redirect:/404";
        }

        return "redirect:/";
    }

    @GetMapping("/auth/change-password")
    public String changePasswordPage(
            @RequestParam("token") String token,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try {
            UserResponse user = authService.validateResetPasswordToken(token);

            ResetPasswordRequest req = new ResetPasswordRequest();
            req.setToken(token);

            model.addAttribute("user", user);
            model.addAttribute("resetPasswordRequest", req);

            return "auth/change-password";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/authentication";
        }
    }

    @PostMapping("/auth/update-password")
    public String updatePassword(
            @Valid @ModelAttribute("resetPasswordRequest") ResetPasswordRequest req,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            return "auth/change-password";
        }

        try {
            authService.updatePassword(req);
            redirectAttributes.addFlashAttribute("successMessage", "Password changed successfully!");
            return "redirect:/";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/authentication";
        }
    }
}