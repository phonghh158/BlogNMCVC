package com.J2EE.BlogNMCVC.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookmarkResponse {
    private UUID id;
    private LocalDateTime createdAt;

    private UUID userId;
    private String username;
    private String name;

    private UUID topicId;
    private String topicTitle;
}