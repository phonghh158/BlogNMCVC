package com.J2EE.BlogNMCVC.repository;

import com.J2EE.BlogNMCVC.constant.ReactionType;
import com.J2EE.BlogNMCVC.constant.TopicStatus;
import com.J2EE.BlogNMCVC.model.Collection;
import com.J2EE.BlogNMCVC.model.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TopicRepository extends JpaRepository<Topic, UUID> {
    // USER
    Page<Topic> findAllByStatusAndDeletedAtIsNull(TopicStatus status, Pageable pageable);

    Page<Topic> findAllByCollectionAndDeletedAtIsNull(Collection collection, Pageable pageable);

    Optional<Topic> findByTitleAndDeletedAtIsNull(String title);

    Optional<Topic> findBySlugAndDeletedAtIsNull(String slug);

    Page<Topic> findAllByCollectionInAndStatusAndDeletedAtIsNull(List<Collection> collections, TopicStatus status, Pageable pageable);

    // ADMIN
    Page<Topic> findAllByCollection(Collection collection, Pageable pageable);

    Page<Topic> findAllByStatus(TopicStatus status, Pageable pageable);

    Page<Topic> findAllByCollectionAndStatus(Collection collection, TopicStatus status, Pageable pageable);

    Optional<Topic> findByTitle(String title);

    Optional<Topic> findBySlug(String slug);

    Page<Topic> findAllByCollectionIn(List<Collection> collections, Pageable pageable);

    Page<Topic> findAllByCollectionInAndStatus(List<Collection> collections, TopicStatus status, Pageable pageable);

    // SYSTEM
    List<Topic> findAllByCollectionAndDeletedAtIsNull(Collection collection);
    List<Topic> findAllByCollectionAndDeletedAtIsNotNull(Collection collection);

    // EXIST
    boolean existsBySlug(String slug);

    // Find Top Topic By Total Reaction with Reaction Type
    @Query("""
        select t
        from Topic t
        left join Reaction r
            on r.topic = t
            and r.reactionType = :reactionType
        where t.deletedAt is null
            and t.status = :publishedStatus
            and t.publishedAt is not null
        group by t
        order by count(r.id) desc, t.publishedAt desc
        """)
    List<Topic> findTopTopicsByReactionType(
            TopicStatus publishedStatus,
            ReactionType reactionType,
            Pageable pageable
    );
}
