package com.banking.service;

import com.banking.model.*;
import java.util.*;

public class Bank {
    private String bankName;
    private List<Customer> customers;
    private List<Account> accounts;
    
    public Bank(String bankName) {
        this.bankName = bankName;
        this.customers = new ArrayList<>();
        this.accounts = new ArrayList<>();
    }
    
    public void addCustomer(Customer customer) {
        if (customer != null && !customers.contains(customer)) {
            customers.add(customer);
            System.out.println("✓ Customer " + customer.getCustomerId() + " added to bank");
        }
    }
    
    public Account openAccount(Customer customer, String accountType) {
        if (customer == null) {
            System.out.println("✗ Customer not found");
            return null;
        }
        
        String accountNumber = generateAccountNumber(accountType);
        Account account = null;
        
        switch (accountType.toLowerCase()) {
            case "savings":
                account = new SavingsAccount(accountNumber, customer);
                break;
            case "investment":
                account = new InvestmentAccount(accountNumber, customer);
                break;
            case "cheque":
                account = new ChequeAccount(accountNumber, customer, "", "");
                break;
            default:
                System.out.println("✗ Unknown account type");
                return null;
        }
        
        if (account != null) {
            accounts.add(account);
            customer.addAccount(account);
            System.out.println("✓ " + accountType + " account " + accountNumber + " opened successfully");
        }
        
        return account;
    }
    
    public void processMonthlyInterest() {
        System.out.println("\n--- Processing Monthly Interest ---");
        for (Account account : accounts) {
            account.payInterest();
        }
    }
    
    public Customer getCustomerById(String customerId) {
        return customers.stream()
            .filter(c -> c.getCustomerId().equals(customerId))
            .findFirst()
            .orElse(null);
    }
    
    public Customer getCustomerByEmail(String email) {
        return customers.stream()
            .filter(c -> c.getEmail().equals(email))
            .findFirst()
            .orElse(null);
    }
    
    private String generateAccountNumber(String type) {
        return type.substring(0, 3).toUpperCase() + "_" + System.nanoTime();
    }
    
    public List<Customer> getAllCustomers() {
        return new ArrayList<>(customers);
    }
    
    public List<Account> getAllAccounts() {
        return new ArrayList<>(accounts);
    }
    
    public String getBankName() { return bankName; }
}
