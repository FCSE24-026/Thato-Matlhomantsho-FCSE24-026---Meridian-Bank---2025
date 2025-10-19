package com.banking.controller;

import com.banking.model.*;
import com.banking.service.*;
import java.util.*;

// ============================================================================
// 1. LOGIN CONTROLLER - Handles authentication
// ============================================================================
/**
 * LoginController: Manages user authentication
 * 
 * Responsibility:
 * - Validate username/customer ID
 * - Authenticate users
 * - Manage login state
 * 
 * DOES NOT contain:
 * - GUI code (no JFrame, buttons, text fields)
 * - Database code (no JDBC)
 * - Business logic storage
 */
public class LoginController {
    private Bank bank;
    private Customer currentLoggedInCustomer;
    
    public LoginController(Bank bank) {
        this.bank = bank;
        this.currentLoggedInCustomer = null;
    }
    
    /**
     * Authenticate a customer by ID
     * @param customerId The customer ID
     * @return true if authenticated, false otherwise
     */
    public boolean authenticateCustomer(String customerId) {
        if (customerId == null || customerId.isEmpty()) {
            System.out.println("✗ Customer ID cannot be empty");
            return false;
        }
        
        Customer customer = bank.getCustomerById(customerId);
        
        if (customer != null) {
            this.currentLoggedInCustomer = customer;
            System.out.println("✓ Authentication successful for: " + 
                             customer.getFirstName() + " " + customer.getSurname());
            return true;
        }
        
        System.out.println("✗ Customer ID not found");
        return false;
    }
    
    /**
     * Get currently logged-in customer
     */
    public Customer getCurrentCustomer() {
        return currentLoggedInCustomer;
    }
    
    /**
     * Logout current customer
     */
    public void logout() {
        if (currentLoggedInCustomer != null) {
            System.out.println("✓ " + currentLoggedInCustomer.getFirstName() + " logged out");
            currentLoggedInCustomer = null;
        }
    }
    
    /**
     * Check if user is logged in
     */
    public boolean isLoggedIn() {
        return currentLoggedInCustomer != null;
    }
}