package com.J2EE.BlogNMCVC.dto.auth.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequest {
    @NotBlank(message = "Token invalid")
    private String token;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 72)
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).+$",
            message = "Password must contain at least 1 uppercase letter, 1 lowercase letter, 1 number, and 1 special character"
    )
    private String newPassword;

    @NotBlank(message = "Confirm password is required")
    @Size(min = 8, max = 72)
    private String confirmNewPassword;
}
