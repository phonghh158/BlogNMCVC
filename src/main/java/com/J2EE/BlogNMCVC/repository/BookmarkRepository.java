package com.J2EE.BlogNMCVC.repository;

import com.J2EE.BlogNMCVC.model.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BookmarkRepository extends JpaRepository<Bookmark, UUID> {
}
