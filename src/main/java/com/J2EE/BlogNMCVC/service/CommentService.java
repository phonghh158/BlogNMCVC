package com.J2EE.BlogNMCVC.service;

import com.J2EE.BlogNMCVC.dto.request.CommentRequest;
import com.J2EE.BlogNMCVC.dto.response.CommentResponse;
import com.J2EE.BlogNMCVC.mapper.CommentMapper;
import com.J2EE.BlogNMCVC.model.Comment;
import com.J2EE.BlogNMCVC.model.Topic;
import com.J2EE.BlogNMCVC.model.User;
import com.J2EE.BlogNMCVC.repository.CommentRepository;
import com.J2EE.BlogNMCVC.repository.TopicRepository;
import com.J2EE.BlogNMCVC.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private UserRepository userRepository;

    // CREATE
    public CommentResponse addComment(CommentRequest req) {
        if (req.getParentId() != null) {
            Comment parentComment = commentRepository.findById(req.getParentId()).orElse(null);

            if (parentComment != null && parentComment.getParentId() != null) {
                throw new RuntimeException("This comment can't make parent");
            }
        }

        Topic topic = topicRepository.findById(req.getTopicId())
                .orElseThrow(() -> new RuntimeException("Could not find topic with id: " + req.getTopicId()));

        User user = userService.getCurrentUser();

        Comment comment = commentMapper.toEntity(req, topic, user);

        comment = commentRepository.save(comment);

        return commentMapper.toResponse(comment);
    }

    // READ
    public Page<CommentResponse> getAllByParentId(UUID parentId, int page, int size) {
        Comment parentComment = commentRepository.findById(parentId).orElseThrow(() -> new RuntimeException("Could not find parent comment with id: " + parentId));

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        return commentRepository.findAllByParentId(parentId, pageable).map(commentMapper::toResponse);
    }

    public Page<CommentResponse> getAllByTopic(UUID id, int page, int size) {
        Topic topic = topicRepository.findById(id).orElseThrow(() -> new RuntimeException("Could not find topic with id: " + id));

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

    public CommentResponse getCommentByUserAndTopicAndParentId(UUID userId, UUID topicId, UUID parentId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("Could not find user with id: " + userId));

        Topic topic = topicRepository.findById(topicId).orElseThrow(() -> new RuntimeException("Could not find topic with id: " + topicId));

        Comment comment = commentRepository.findByUserAndTopicAndParentId(user, topic, parentId);

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
}
