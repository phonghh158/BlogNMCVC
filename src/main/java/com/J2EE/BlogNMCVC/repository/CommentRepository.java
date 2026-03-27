package com.J2EE.BlogNMCVC.repository;

import com.J2EE.BlogNMCVC.model.Comment;
import com.J2EE.BlogNMCVC.model.Topic;
import com.J2EE.BlogNMCVC.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {
    Page<Comment> findAllByUser(User user, Pageable pageable);

    Page<Comment> findAllByTopic(Topic topic, Pageable pageable);
}
