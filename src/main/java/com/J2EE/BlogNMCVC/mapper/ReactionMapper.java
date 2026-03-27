package com.J2EE.BlogNMCVC.mapper;

import com.J2EE.BlogNMCVC.dto.response.ReactionResponse;
import com.J2EE.BlogNMCVC.model.Reaction;
import com.J2EE.BlogNMCVC.model.Topic;
import com.J2EE.BlogNMCVC.model.User;
import org.springframework.stereotype.Component;

@Component
public class ReactionMapper {
    public ReactionResponse toResponse(Reaction reaction) {
        return ReactionResponse.builder()
                .id(reaction.getId())
                .reactionType(reaction.getReactionType())
                .createdAt(reaction.getCreatedAt())

                .userId(reaction.getUser().getId())
                .username(reaction.getUser().getUsername())
                .name(reaction.getUser().getName())

                .topicId(reaction.getTopic().getId())
                .topicTitle(reaction.getTopic().getTitle())
                .build();
    }
}