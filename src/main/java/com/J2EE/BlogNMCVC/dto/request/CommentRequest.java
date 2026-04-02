package com.J2EE.BlogNMCVC.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentRequest {
    @NotNull(message = "Topic id must not be null")
    private UUID topicId;

    @NotBlank(message = "Content must not be blank")
    @Size(min = 1, max = 2048)
    private String content;
}