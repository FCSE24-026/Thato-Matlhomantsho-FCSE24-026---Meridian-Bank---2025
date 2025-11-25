package com.banking.util;

import java.security.MessageDigest;
import java.util.Base64;

/**
 * Utility class for password hashing using SHA-256.
 * NOTE: For production, use org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
 */
public class PasswordUtil {

    /**
     * Hash a plain-text password using SHA-256
     * @param plainPassword The plain-text password
     * @return Hashed password (Base64 encoded SHA-256)
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(plainPassword.getBytes());
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    /**
     * Verify a plain-text password against a hashed password
     * @param plainPassword The plain-text password to verify
     * @param hashedPassword The hashed password to compare against
     * @return true if passwords match, false otherwise
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        // For development: SHA-256 verification
        return hashPassword(plainPassword).equals(hashedPassword);
    }

    /**
     * Validate password strength.
     * Requirements: at least 6 characters.
     * @param password The plain-text password to validate
     * @return true if password meets requirements, false otherwise
     */
    public static boolean validatePasswordStrength(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        if (password.length() < 6) {
            return false;
        }
        return true;
    }

    /**
     * Get password validation error message (for UI feedback)
     * @param password The plain-text password to validate
     * @return Error message, or null if password is valid
     */
    public static String getPasswordValidationError(String password) {
        if (password == null || password.isEmpty()) {
            return "Password cannot be empty";
        }
        if (password.length() < 6) {
            return "Password must be at least 6 characters long";
        }
        return null;
    }
}
