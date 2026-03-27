package com.J2EE.BlogNMCVC.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.J2EE.BlogNMCVC.constant.AuthorReaction;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponse {
    private UUID id;
    private String content;
    private AuthorReaction authorReaction;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    // Thông tin liên quan
    private UUID commenterId;
    private String commenterUsername;
    private String commenterName;

    private UUID topicId;
    private String topicTitle;
}