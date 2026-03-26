package com.J2EE.BlogNMCVC.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class BookmarkRequest {
    @NotNull(message = "Topic id must not be null")
    private UUID topicId;
}