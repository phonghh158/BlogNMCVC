package com.J2EE.BlogNMCVC.dto.auth.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    @NotBlank(message = "Login identifier must not be blank")
    private String identifier;

    @NotBlank(message = "Password is required")
    private String password;
}