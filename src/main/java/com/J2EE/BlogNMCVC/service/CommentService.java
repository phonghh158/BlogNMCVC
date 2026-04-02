package com.J2EE.BlogNMCVC.service;

import com.J2EE.BlogNMCVC.constant.AuthorReaction;
import com.J2EE.BlogNMCVC.dto.request.CommentRequest;
import com.J2EE.BlogNMCVC.dto.response.CommentResponse;
import com.J2EE.BlogNMCVC.mapper.CommentMapper;
import com.J2EE.BlogNMCVC.model.Comment;
import com.J2EE.BlogNMCVC.model.Topic;
import com.J2EE.BlogNMCVC.model.User;
import com.J2EE.BlogNMCVC.repository.CommentRepository;
import com.J2EE.BlogNMCVC.repository.TopicRepository;
import com.J2EE.BlogNMCVC.repository.UserRepository;
import com.J2EE.BlogNMCVC.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private UserRepository userRepository;

    // CREATE
    public CommentResponse addComment(CommentRequest req) {
        Topic topic = topicRepository.findById(req.getTopicId())
                .orElseThrow(() -> new RuntimeException("Could not find topic with id: " + req.getTopicId()));

        UUID userId = UserUtils.getCurrentUserId();

        if (userId == null) {
            throw new UsernameNotFoundException("Cannot find user with id: " + userId);
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Comment comment = commentMapper.toEntity(req, topic, user);

        comment = commentRepository.save(comment);

        return commentMapper.toResponse(comment);
    }

    public Page<CommentResponse> getAllByTopic(UUID id, int page, int size) {
        Topic topic = topicRepository.findById(id).orElseThrow(() -> new RuntimeException("Could not find topic with id: " + id));

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        return commentRepository.findAllByTopicAndDeletedAtIsNull(topic, pageable).map(commentMapper::toResponse);
    }

    public Page<CommentResponse> getAllByTopicForAdmin(String slug, int page, int size) {
        Topic topic = topicRepository.findBySlug(slug).orElseThrow(() -> new RuntimeException("Could not find topic with slug: " + slug));

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        return commentRepository.findAllByTopic(topic, pageable).map(commentMapper::toResponse);
    }

    public Page<CommentResponse> getAllByUser(UUID id, int page, int size) {
        User user = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("Could not find user with id: " + id));

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        return commentRepository.findAllByUser(user, pageable).map(commentMapper::toResponse);
    }

    public CommentResponse getCommentById(UUID id) {
        Comment comment = commentRepository.findById(id).orElseThrow(() -> new RuntimeException("Could not find comment with id: " + id));

        return commentMapper.toResponse(comment);
    }

    // UPDATE
    public CommentResponse updateComment(UUID id, String commentContent) {
        Comment comment = commentRepository.findById(id).orElseThrow(() -> new RuntimeException("Could not find comment with id: " + id));

        if (commentContent.isEmpty() || commentContent.length() > 2048) {
            throw new RuntimeException("Invalid comment. Comment can not blank and must be less than or equal to 2048 characters");
        }

        comment.setContent(commentContent);

        commentRepository.save(comment);
        return commentMapper.toResponse(comment);
    }

    public CommentResponse authorReactComment(UUID id, AuthorReaction authorReaction) {
        Comment comment = commentRepository.findById(id).orElseThrow(() -> new RuntimeException("Could not find comment with id: " + id));

        comment.setAuthorReaction(authorReaction);

        comment = commentRepository.save(comment);

        return commentMapper.toResponse(comment);
    }

    // DELETE
    public CommentResponse deteleComment(UUID id) {
        Comment comment = commentRepository.findById(id).orElseThrow(() -> new RuntimeException("Could not find comment with id: " + id));

        comment.setDeletedAt(LocalDateTime.now());

        comment = commentRepository.save(comment);

        return commentMapper.toResponse(comment);
    }

}
