package com.banking.service;

import com.banking.model.*;
import com.banking.persistence.*;
import java.util.*;
import java.time.LocalDate;

public class Bank {
    private String bankName;
    private DatabaseManager dbManager;
    
    public Bank(String bankName) {
        this.bankName = bankName;
        this.dbManager = new DatabaseManager();
    }
    
    public void addCustomer(Customer customer) {
        if (customer != null) {
            if (dbManager.saveCustomer(customer)) {
                System.out.println("✓ Customer " + customer.getCustomerId() + " saved to database");
            }
        }
    }
    
    public Account openAccount(Customer customer, String accountType) {
        if (customer == null) {
            System.out.println("✗ Customer not found");
            return null;
        }
        
        // Ensure customer is in database
        Customer dbCustomer = dbManager.getCustomer(customer.getCustomerId());
        if (dbCustomer == null) {
            dbManager.saveCustomer(customer);
            dbCustomer = customer;
        }
        
        String accountNumber = generateAccountNumber(accountType);
        Account account = null;
        
        switch (accountType.toLowerCase()) {
            case "savings":
                account = new SavingsAccount(accountNumber, dbCustomer);
                break;
            case "investment":
                account = new InvestmentAccount(accountNumber, dbCustomer);
                break;
            case "cheque":
            case "checking":
                account = new ChequeAccount(accountNumber, dbCustomer, "", "");
                break;
            case "money market":
            case "moneymarket":
                account = new MoneyMarketAccount(accountNumber, dbCustomer);
                break;
            case "cd":
            case "certificate of deposit":
            case "certificateofdeposit":
                // Default to 12-month CD term
                account = new CertificateOfDepositAccount(accountNumber, dbCustomer, 12);
                break;
            default:
                System.out.println("✗ Unknown account type");
                return null;
        }
        
        if (account != null) {
            account.setDateOpened(LocalDate.now());
            if (dbManager.saveAccount(account)) {
                dbCustomer.addAccount(account);
                System.out.println("✓ " + accountType + " account " + accountNumber + " saved to database");
            }
        }
        
        return account;
    }
    
    public void processMonthlyInterest() {
        System.out.println("\n--- Processing Monthly Interest ---");
        List<Account> accounts = getAllAccounts();
        for (Account account : accounts) {
            account.payInterest();
            dbManager.updateAccount(account);
        }
    }
    
    public Customer getCustomerById(String customerId) {
        return dbManager.getCustomer(customerId);
    }
    
    public Customer getCustomerByEmail(String email) {
        List<Customer> customers = dbManager.getAllCustomers();
        return customers.stream()
            .filter(c -> c.getEmail().equals(email))
            .findFirst()
            .orElse(null);
    }

    public boolean updateCustomer(Customer customer) {
        return dbManager.updateCustomer(customer);
    }
    
    private String generateAccountNumber(String type) {
        return type.substring(0, 3).toUpperCase() + "_" + System.nanoTime();
    }
    
    public List<Customer> getAllCustomers() {
        return dbManager.getAllCustomers();
    }
    
    public List<Account> getAllAccounts() {
        return dbManager.getCustomerAccounts("*");
    }
    
    /**
     * Get all accounts - workaround to fetch all accounts from all customers
     */
    public List<Account> getAllAccountsForCustomer(String customerId) {
        return dbManager.getCustomerAccounts(customerId);
    }
    
    public void recordTransaction(Transaction transaction) {
        dbManager.saveTransaction(transaction);
    }
    
    public List<Transaction> getTransactionHistory(String accountNumber) {
        return dbManager.getAccountTransactions(accountNumber);
    }

    /**
     * Get a single account by account number
     */
    public Account getAccount(String accountNumber) {
        return dbManager.getAccount(accountNumber);
    }
    
    public String getBankName() { return bankName; }
}
