package com.J2EE.BlogNMCVC.repository;

import com.J2EE.BlogNMCVC.model.Collection;
import com.J2EE.BlogNMCVC.model.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TopicRepository extends JpaRepository<Topic, UUID> {
    // USER
    Page<Topic> findAllByDeletedAtIsNull(Pageable pageable);

    Page<Topic> findAllByCollectionAndDeletedAtIsNull(Collection collection, Pageable pageable);

    Optional<Topic> findByTitleAndDeletedAtIsNull(String title);

    Optional<Topic> findBySlugAndDeletedAtIsNull(String slug);

    // ADMIN
    Page<Topic> findAllByCollection(Collection collection, Pageable pageable);

    Optional<Topic> findByTitle(String title);

    Optional<Topic> findBySlug(String slug);

    // SYSTEM
    List<Topic> findAllByCollectionAndDeletedAtIsNull(Collection collection);
    List<Topic> findAllByCollectionAndDeletedAtIsNotNull(Collection collection);

    // EXIST
    boolean existsBySlug(String slug);
}
