package com.J2EE.BlogNMCVC.dto.request;

import com.J2EE.BlogNMCVC.constant.TopicStatus;
import jakarta.persistence.Column;
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
    @Size(max = 512, message = "Title must be less than or equal to 512 characters")
    private String title;

    @NotBlank(message = "Content is required")
    private String content;

    @Size(max = 512, message = "Footnote must be less than or equal to 512 characters")
    private String footnote;

    @Size(max = 1024, message = "FacebookLink must be less than or equal to 1024 characters")
    private String facebookLink;

    @NotNull(message = "Status is required")
    private TopicStatus status;
}