package com.J2EE.BlogNMCVC.mapper;

import com.J2EE.BlogNMCVC.dto.request.CommentRequest;
import com.J2EE.BlogNMCVC.dto.response.CommentResponse;
import com.J2EE.BlogNMCVC.model.Comment;
import com.J2EE.BlogNMCVC.model.Topic;
import com.J2EE.BlogNMCVC.model.User;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {
    public Comment toEntity(CommentRequest req, Topic topic, User user) {
        return Comment.builder()
                .topic(topic)
                .user(user)
                .content(req.getContent())
                .build();
    }

    public CommentResponse toResponse(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .authorReaction(comment.getAuthorReaction())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .deletedAt(comment.getDeletedAt())

                .commenterId(comment.getUser().getId())
                .commenterUsername(comment.getUser().getUsername())
                .commenterName(comment.getUser().getName())
                .commenterAvatar(comment.getUser().getAvatar())

                .topicId(comment.getTopic().getId())
                .topicTitle(comment.getTopic().getTitle())

                .build();
    }
}