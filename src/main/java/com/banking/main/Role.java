package com.banking.main;

/**
 * Role enum defines user roles in the banking system
 */
public enum Role {
    ADMIN("Administrator"),
    TELLER("Teller"),
    CUSTOMER("Customer");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
