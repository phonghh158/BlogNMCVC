package com.J2EE.BlogNMCVC.model;

import com.J2EE.BlogNMCVC.constant.TokenType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(length = 36, nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    // SHA-256 = 64 ký tự hex | SHA-512 = 128 ký tự hex
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(nullable = false, unique = true, length = 64)
    private String hashedToken;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private TokenType type;

    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}