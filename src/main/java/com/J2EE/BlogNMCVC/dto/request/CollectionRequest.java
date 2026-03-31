package com.J2EE.BlogNMCVC.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CollectionRequest {
    @NotBlank(message = "Name must not be blank")
    @Size(max = 96, message = "Name must be at most 96 characters")
    private String name;

    @Size(max = 1080, message = "Description must be at most 1080 characters")
    private String description;
}