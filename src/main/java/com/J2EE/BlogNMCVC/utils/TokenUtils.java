package com.J2EE.BlogNMCVC.utils;

import com.J2EE.BlogNMCVC.constant.TokenType;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class TokenUtils {

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final String SALT = "NMCVCBlog";

    // SHA-256 hash -> hex (64 chars)
    public static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                String h = Integer.toHexString(0xff & b);
                if (h.length() == 1) hex.append('0');
                hex.append(h);
            }
            return hex.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error hashing token", e);
        }
    }

    // Random base64 string
    public static String randomBase64(int byteLength) {
        byte[] bytes = new byte[byteLength];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    // Tạo plain token theo rule: email + SALT + username
    public static String generatePlainToken(String email, String username, TokenType tokenType) {
        String base = email + SALT + username + tokenType;
        String randomPart = randomBase64(32);
        return sha256(base + randomPart);
    }

    // Hash Token
    public static String hashToken(String plainToken) {
        return sha256(plainToken);
    }
}