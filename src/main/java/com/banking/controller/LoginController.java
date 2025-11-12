package com.banking.controller;

import com.banking.model.*;
import com.banking.service.*;
import com.banking.main.User;

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
     * Authenticate by username/email and password.
     *
     * NOTE: The current model does not store passwords. This method will
     * attempt to find a Customer by email (username) and mark them as logged in.
     * Replace or extend this with real password verification when a password
     * field / secure storage is available.
     *
     * @param username email/username to locate the customer
     * @param password password (currently unused)
     * @return true if a matching customer was found and logged in
     */
    public boolean authenticateUser(String username, String password) {
        if (username == null || username.isEmpty()) {
            System.out.println("✗ Username cannot be empty");
            return false;
        }

        Customer customer = bank.getCustomerByEmail(username);

        if (customer != null) {
            this.currentLoggedInCustomer = customer;
            System.out.println("✓ Authentication successful for: " + 
                             customer.getFirstName() + " " + customer.getSurname());
            if (password == null || password.isEmpty()) {
                // Informational only — password handling not implemented
                System.out.println("⚠ Warning: password verification not implemented; please add secure handling.");
            }
            return true;
        }

        System.out.println("✗ User not found");
        return false;
    }
    
    /**
     * Get currently logged-in customer
     */
    public Customer getCurrentCustomer() {
        return currentLoggedInCustomer;
    }
    
    /**
     * Get user by username - returns User object for ModernBankingApp
     */
    public User getUserByUsername(String username) {
        if (username == null || username.isEmpty()) {
            return null;
        }
        Customer customer = bank.getCustomerByEmail(username);
        if (customer != null) {
            return new User(customer.getCustomerId().hashCode(), 
                           username, 
                           "", // password not used 
                           customer.getEmail(),
                           customer.getPhoneNumber());
        }
        return null;
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
    
    /**
     * Register a new user - used by ModernBankingApp
     */
    public boolean registerUser(String firstName, String lastName, String email, 
                                String phone, String address) {
        if (firstName == null || firstName.isEmpty() || 
            lastName == null || lastName.isEmpty() ||
            email == null || email.isEmpty()) {
            System.out.println("✗ First name, last name, and email are required");
            return false;
        }
        
        // Check if email already exists
        if (bank.getCustomerByEmail(email) != null) {
            System.out.println("✗ Email already registered");
            return false;
        }
        
        // Create new customer
        String customerId = "CUST_" + System.nanoTime();
        Customer newCustomer = new Customer(customerId, firstName, lastName, 
                                           address != null ? address : "", 
                                           phone != null ? phone : "", 
                                           email);
        bank.addCustomer(newCustomer);
        System.out.println("✓ User registered successfully with ID: " + customerId);
        return true;
    }
}