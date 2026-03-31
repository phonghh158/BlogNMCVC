package com.J2EE.BlogNMCVC.dto.response;

import com.J2EE.BlogNMCVC.constant.TopicStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class TopicResponse {
    private UUID id;

    private String title;

    private String slug;

    private String content;

    private String footnote;

    private String facebookLink;

    private String thumbnail;

    private TopicStatus status;

    private LocalDateTime publishedAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // Thông tin liên quan
    private UUID authorId;
    private String authorUsername;
    private String authorName;

    private UUID collectionId;
    private String collectionName;
}