package com.J2EE.BlogNMCVC.controller;

import com.J2EE.BlogNMCVC.dto.response.UserResponse;
import com.J2EE.BlogNMCVC.service.UserService;
import com.J2EE.BlogNMCVC.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttribute {
    @Autowired
    private UserService userService;

    @ModelAttribute("isAuthenticated")
    public boolean isAuthenticated() {
        return UserUtils.isAuthenticated();
    }

    @ModelAttribute("currentUser")
    public UserResponse getCurrentUser() {
        return userService.getMe();
    }

    @ModelAttribute("isAdmin")
    public boolean isAdmin() {
        return UserUtils.isAuthenticated()
                && userService.getMe().getRole().name().equals("ADMIN");
    }
}