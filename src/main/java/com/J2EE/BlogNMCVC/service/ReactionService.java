package com.J2EE.BlogNMCVC.service;

import com.J2EE.BlogNMCVC.constant.ReactionType;
import com.J2EE.BlogNMCVC.dto.response.ReactionResponse;
import com.J2EE.BlogNMCVC.mapper.ReactionMapper;
import com.J2EE.BlogNMCVC.model.Reaction;
import com.J2EE.BlogNMCVC.model.Topic;
import com.J2EE.BlogNMCVC.model.User;
import com.J2EE.BlogNMCVC.repository.ReactionRepository;
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

import java.util.UUID;

@Service
public class ReactionService {
    @Autowired
    private ReactionRepository reactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private ReactionMapper reactionMapper;

    // CREATE
    public ReactionResponse addReaction(UUID topicId, ReactionType reactionType) {
        UUID userId = UserUtils.getCurrentUserId();
        User user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("Cannot find user with id: " + userId));

        Topic topic = topicRepository.findById(topicId).orElseThrow(() -> new IllegalArgumentException("Cannot find topic with id: " + topicId));

        Reaction reaction = Reaction.builder()
                .user(user)
                .topic(topic)
                .reactionType(reactionType)
                .build();

        reaction = reactionRepository.save(reaction);

        return reactionMapper.toResponse(reaction);
    }

    // READ
    // READ ALL
    // READ By Topic
    public Page<ReactionResponse> getAllReactionByTopic(UUID id, int size, int page) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find topic with id: " + id));

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("updatedAt").descending()
        );

        return reactionRepository.findAllByTopic(topic, pageable).map(reactionMapper::toResponse);
    }

    public Page<ReactionResponse> getAllReactionByTopicAndReactionType(UUID id, ReactionType reactionType, int size, int page) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find topic with id: " + id));

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("updatedAt").descending()
        );

        return reactionRepository.findAllByTopicAndReactionType(topic, reactionType, pageable).map(reactionMapper::toResponse);
    }

    public long countAllReactionByTopic(UUID id) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find topic with id: " + id));

        return reactionRepository.countAllByTopic(topic);
    }

    public long countAllReactionByTopicAndReactionType(UUID id, ReactionType reactionType) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find topic with id: " + id));

        return reactionRepository.countAllByTopicAndReactionType(topic, reactionType);
    }

    // UPDATE
    public ReactionResponse updateReaction(UUID id, ReactionType reactionType) {
        Reaction reaction = reactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find reaction with id: " + id));

        reaction.setReactionType(reactionType);
        reaction = reactionRepository.save(reaction);

        return reactionMapper.toResponse(reaction);
    }

    // DELETE
    public String deleteReaction(UUID id) {
        reactionRepository.deleteById(id);
        return "Reaction with id: " + id + " has been deleted";
    }
}
