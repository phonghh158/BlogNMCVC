package com.J2EE.BlogNMCVC.dto.request;

import com.J2EE.BlogNMCVC.constant.TopicStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class TopicRequest {
    @NotNull(message = "Collection id is required")
    private UUID collectionId;

    @NotBlank(message = "Title is required")
    @Size(max = 256, message = "Title must be less than or equal to 256 characters")
    private String title;

    @NotBlank(message = "Content is required")
    private String content;

    @Size(max = 256, message = "Thumbnail must be less than or equal to 256 characters")
    private String thumbnail;

    @NotNull(message = "Status is required")
    private TopicStatus status;
}