package com.banking.controller;

import com.banking.model.*;
import com.banking.service.*;

public class CustomerController {
    private Bank bank;
    
    public CustomerController(Bank bank) {
        this.bank = bank;
    }
    
    /**
     * Register a new customer
     */
    public Customer registerCustomer(String firstName, String surname, String address,
                                    String phoneNumber, String email) {
        // Validate inputs
        if (!validateCustomerInput(firstName, surname, address, phoneNumber, email)) {
            return null;
        }
        
        // Create unique customer ID
        String customerId = generateCustomerId();
        
        // Create customer
        Customer customer = new Customer(customerId, firstName, surname, address, 
                                        phoneNumber, email);
        
        // Add to bank
        bank.addCustomer(customer);
        
        System.out.println("✓ Customer registered: " + customerId);
        return customer;
    }
    
    /**
     * Validate customer input
     */
    private boolean validateCustomerInput(String firstName, String surname, String address,
                                         String phoneNumber, String email) {
        if (firstName == null || firstName.isEmpty()) {
            System.out.println("✗ First name is required");
            return false;
        }
        if (surname == null || surname.isEmpty()) {
            System.out.println("✗ Surname is required");
            return false;
        }
        if (address == null || address.isEmpty()) {
            System.out.println("✗ Address is required");
            return false;
        }
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            System.out.println("✗ Phone number is required");
            return false;
        }
        if (email == null || email.isEmpty()) {
            System.out.println("✗ Email is required");
            return false;
        }
        return true;
    }
    
    /**
     * Generate unique customer ID
     */
    private String generateCustomerId() {
        return "CUST" + System.nanoTime();
    }
    
    /**
     * Get customer information for display
     */
    public String getCustomerDetailsForDisplay(Customer customer) {
        if (customer == null) {
            return "Customer not found";
        }
        
        StringBuilder details = new StringBuilder();
        details.append("Customer ID: ").append(customer.getCustomerId()).append("\n");
        details.append("Name: ").append(customer.getFirstName()).append(" ")
               .append(customer.getSurname()).append("\n");
        details.append("Address: ").append(customer.getAddress()).append("\n");
        details.append("Phone: ").append(customer.getPhoneNumber()).append("\n");
        details.append("Email: ").append(customer.getEmail()).append("\n");
        details.append("Total Accounts: ").append(customer.getAccounts().size()).append("\n");
        
        double totalBalance = customer.getAccounts().stream()
            .mapToDouble(Account::getBalance)
            .sum();
        details.append("Total Balance: BWP ").append(String.format("%.2f", totalBalance));
        
        return details.toString();
    }
    
    /**
     * Update customer address
     */
    public boolean updateCustomerAddress(Customer customer, String newAddress) {
        if (customer == null) {
            System.out.println("✗ Customer is null");
            return false;
        }
        
        if (newAddress == null || newAddress.isEmpty()) {
            System.out.println("✗ Address cannot be empty");
            return false;
        }
        
        customer.setAddress(newAddress);
        System.out.println("✓ Address updated");
        return true;
    }
    
    /**
     * Update customer email
     */
    public boolean updateCustomerEmail(Customer customer, String newEmail) {
        if (customer == null) {
            System.out.println("✗ Customer is null");
            return false;
        }
        
        if (newEmail == null || newEmail.isEmpty()) {
            System.out.println("✗ Email cannot be empty");
            return false;
        }
        
        customer.setEmail(newEmail);
        System.out.println("✓ Email updated");
        return true;
    }
}