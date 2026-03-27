package com.J2EE.BlogNMCVC.dto.response;

import com.J2EE.BlogNMCVC.constant.ReactionType;
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
public class ReactionResponse {
    private UUID id;
    private ReactionType reactionType;
    private LocalDateTime createdAt;

    private UUID userId;
    private String username;
    private String name;

    private UUID topicId;
    private String topicTitle;
}