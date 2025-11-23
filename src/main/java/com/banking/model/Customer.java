package com.banking.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;
import com.banking.main.Role;

public class Customer implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String customerId;
    private String firstName;
    private String surname;
    private String address;
    private String phoneNumber;
    private String email;
    private String passwordHash;
    private LocalDate dateOfBirth;
    private Role role;
    private boolean approved;
    private boolean suspended;
    private List<Account> accounts;
    
    public Customer(String customerId, String firstName, String surname, String address,
                   String phoneNumber, String email, Role role) {
        this.customerId = customerId;
        this.firstName = firstName;
        this.surname = surname;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.passwordHash = "";
        this.dateOfBirth = LocalDate.now();
        this.role = role;
        this.approved = true;
        this.suspended = false;
        this.accounts = new ArrayList<>();
    }
    
    public Customer(String customerId, String firstName, String surname, String address,
                   String phoneNumber, String email, String passwordHash, Role role) {
        this.customerId = customerId;
        this.firstName = firstName;
        this.surname = surname;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.passwordHash = passwordHash;
        this.dateOfBirth = LocalDate.now();
        this.role = role;
        this.approved = true;
        this.suspended = false;
        this.accounts = new ArrayList<>();
    }
    
    public Customer(String customerId, String firstName, String surname, String address,
                   String phoneNumber, String email) {
        this(customerId, firstName, surname, address, phoneNumber, email, Role.CUSTOMER);
    }

    public boolean isApproved() { return approved; }
    public void setApproved(boolean approved) { this.approved = approved; }
    public boolean isSuspended() { return suspended; }
    public void setSuspended(boolean suspended) { this.suspended = suspended; }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public void addAccount(Account account) {
        if (account != null && !accounts.contains(account)) {
            accounts.add(account);
            System.out.println("✓ Account " + account.getAccountNumber() + " added successfully");
        }
    }
    
    public Account getAccountByNumber(String accountNumber) {
        return accounts.stream()
            .filter(acc -> acc.getAccountNumber().equals(accountNumber))
            .findFirst()
            .orElse(null);
    }
    
    public List<Account> getSpecificAccounts() {
        return new ArrayList<>(accounts);
    }
    
    public void displayCustomerInfo() {
        System.out.println("\n========== CUSTOMER INFORMATION ==========");
        System.out.println("Customer ID: " + customerId);
        System.out.println("Name: " + firstName + " " + surname);
        System.out.println("Address: " + address);
        System.out.println("Phone: " + phoneNumber);
        System.out.println("Email: " + email);
        System.out.println("Date of Birth: " + dateOfBirth);
        System.out.println("Total Accounts: " + accounts.size());
        double totalBalance = accounts.stream().mapToDouble(Account::getBalance).sum();
        System.out.println("Total Balance: BWP " + String.format("%.2f", totalBalance));
        System.out.println("=========================================\n");
    }
    
    // Getters
    public String getCustomerId() { return customerId; }
    public String getFirstName() { return firstName; }
    public String getSurname() { return surname; }
    public String getAddress() { return address; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public Role getRole() { return role; }
    public List<Account> getAccounts() { return new ArrayList<>(accounts); }
    
    // Setters
    public void setEmail(String email) { this.email = email; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public void setRole(Role role) { this.role = role; }
}