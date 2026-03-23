package com.J2EE.BlogNMCVC.dto.auth.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 16)
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email is invalid")
    @Size(max = 96)
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 72)
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).+$",
            message = "Password must contain at least 1 uppercase letter, 1 lowercase letter, 1 number, and 1 special character"
    )
    private String password;

    @NotBlank(message = "Confirm password is required")
    @Size(min = 8, max = 72)
    private String confirmPassword;

    @NotBlank(message = "Name is required")
    @Size(max = 96)
    private String name;
}