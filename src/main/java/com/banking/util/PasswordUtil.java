package com.banking.util;

/**
 * Utility class for password hashing using simple BCrypt alternative.
 * NOTE: For production, use org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
 */
public class PasswordUtil {

    /**
     * Hash a plain-text password (simplified for offline use)
     * @param plainPassword The plain-text password
     * @return Hashed password
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        // TODO: Replace with real BCrypt in production
        // For now, use a simple hash for development
        return plainPassword.hashCode() + ":" + plainPassword.length();
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
        // For development: simple verification
        return hashPassword(plainPassword).equals(hashedPassword);
    }
}
