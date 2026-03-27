package com.J2EE.BlogNMCVC.repository;

import com.J2EE.BlogNMCVC.model.Bookmark;
import com.J2EE.BlogNMCVC.model.Topic;
import com.J2EE.BlogNMCVC.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BookmarkRepository extends JpaRepository<Bookmark, UUID> {
    Page<Bookmark> findAllByTopic(Topic topic, Pageable pageable);

    Page<Bookmark> findAllByUser(User user, Pageable pageable);
}
