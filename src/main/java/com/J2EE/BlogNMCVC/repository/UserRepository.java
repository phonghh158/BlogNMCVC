package com.J2EE.BlogNMCVC.repository;

import com.J2EE.BlogNMCVC.constant.UserStatus;
import com.J2EE.BlogNMCVC.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    // Find All Users By Username bla bla
    Page<User> findAllByUsernameContainingIgnoreCaseOrNameContainingIgnoreCase(String username, String name, Pageable pageable);

    // Find All Users By Username bla bla
    Page<User> findAllByDeletedAtIsNullAndUsernameContainingIgnoreCaseOrDeletedAtIsNullAndNameContainingIgnoreCase(
            String username,
            String name,
            Pageable pageable
    );
    // Find All Users By Status
    Page<User> findAllByStatus(UserStatus status, Pageable pageable);

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
