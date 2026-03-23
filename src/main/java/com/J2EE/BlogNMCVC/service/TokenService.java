package com.J2EE.BlogNMCVC.service;

import com.J2EE.BlogNMCVC.constant.TokenType;
import com.J2EE.BlogNMCVC.model.Token;
import com.J2EE.BlogNMCVC.model.User;
import com.J2EE.BlogNMCVC.repository.TokenRepository;
import com.J2EE.BlogNMCVC.repository.UserRepository;
import com.J2EE.BlogNMCVC.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TokenService {
    @Autowired
    private TokenRepository tokenRepository;

    public String generateToken(TokenType tokenType, int exp, User user) {
        String plainToken = TokenUtils.generatePlainToken(user.getEmail(),user.getUsername(), tokenType);
        String hashedToken = TokenUtils.hashToken(plainToken);

        Token token = Token.builder()
                .hashedToken(hashedToken)
                .type(tokenType)
                .user(user)
                .expiredAt(LocalDateTime.now().plusMinutes(exp))
                .build();

        tokenRepository.save(token);

        return plainToken;
    }

    public Token getToken(String plainToken, TokenType tokenType) {
        String hashedToken = TokenUtils.hashToken(plainToken);

        Token token = tokenRepository.findByHashedToken(hashedToken)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Token."));

        if (token.getExpiredAt().isBefore(LocalDateTime.now())) {
            tokenRepository.delete(token);
            throw new IllegalArgumentException("Token has expired");
        }

        if (token.getType() != tokenType) {
            throw new IllegalArgumentException("Invalid token type");
        }

        return token;
    }

    public void deleteToken(Token token) {
        tokenRepository.delete(token);
    }
}
