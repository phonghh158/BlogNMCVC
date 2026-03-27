package com.J2EE.BlogNMCVC.repository;

import com.J2EE.BlogNMCVC.constant.ReactionType;
import com.J2EE.BlogNMCVC.model.Reaction;
import com.J2EE.BlogNMCVC.model.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReactionRepository extends JpaRepository<Reaction, UUID> {
    Page<Reaction> findAllByTopic(Topic topic, Pageable pageable);

    Page<Reaction> findAllByTopicAndReactionType(Topic topic, ReactionType reactionType, Pageable pageable);

    long countAllByTopic(Topic topic);

    long countAllByTopicAndReactionType(Topic topic, ReactionType reactionType);
}
