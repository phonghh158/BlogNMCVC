package com.J2EE.BlogNMCVC.repository;

import com.J2EE.BlogNMCVC.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    // Find by username (get user)
    Optional<User> findByUsername(String username);

    // Find by Email (Forgot/Reset Password)
    Optional<User> findByEmail(String email);

    // Find by Email or Username (Login)
    Optional<User> findByEmailOrUsername(String email, String username);

    Optional<User> findByUsernameAndDeletedAtIsNull(String username);

    Optional<User> findByIdAndDeletedAtIsNull(UUID id);

    // Check Exist
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByIdAndDeletedAtIsNull(UUID id);
}
