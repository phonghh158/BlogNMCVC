package com.J2EE.BlogNMCVC.repository;

import com.J2EE.BlogNMCVC.model.Reaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReactionRepository extends JpaRepository<Reaction, UUID> {

}
