package com.J2EE.BlogNMCVC.repository;

import com.J2EE.BlogNMCVC.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TokenRepository extends JpaRepository<Token, UUID> {
    Optional<Token> findByHashedToken(String hashedToken);
}
