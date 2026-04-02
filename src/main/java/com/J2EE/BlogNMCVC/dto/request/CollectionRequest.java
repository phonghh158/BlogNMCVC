package com.J2EE.BlogNMCVC.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CollectionRequest {
    @NotBlank(message = "Name must not be blank")
    @Size(max = 96, message = "Name must be at most 96 characters")
    private String name;

    @Size(max = 1080, message = "Description must be at most 1080 characters")
    private String description;
}