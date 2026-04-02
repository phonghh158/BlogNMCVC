package com.J2EE.BlogNMCVC.repository;

import com.J2EE.BlogNMCVC.model.Collection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CollectionRepository extends JpaRepository<Collection, UUID> {
    // User
    Page<Collection> findAllByDeletedAtIsNull(Pageable pageable);

    List<Collection> findAllByDeletedAtIsNull(Sort sort);

    Optional<Collection> findBySlugAndDeletedAtIsNull(String slug);

    Optional<Collection> findByNameAndDeletedAtIsNull(String name);

    // Admin
    Optional<Collection> findBySlug(String slug);

    //Exist
    boolean existsBySlug(String slug);
}
