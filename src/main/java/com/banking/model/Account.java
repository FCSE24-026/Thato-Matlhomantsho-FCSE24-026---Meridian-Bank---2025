// ============================================================================
// FIXED: src/main/java/com/banking/model/Account.java
// Added missing getter methods: getAccountType(), getBranch(), getDateOpened()
// ============================================================================

package com.banking.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

public abstract class Account implements Serializable {
    private static final long serialVersionUID = 1L;
    
    protected String accountNumber;
    protected double balance;
    protected String branch;
    protected LocalDate dateOpened;
    protected Customer customer;
    protected List<Transaction> transactions;
    
    public Account(String accountNumber, Customer customer) {
        this.accountNumber = accountNumber;
        this.balance = 0.0;
        this.branch = "Main Branch";
        this.dateOpened = LocalDate.now();
        this.customer = customer;
        this.transactions = new ArrayList<>();
    }
    
    public abstract String getAccountNumber();
    public abstract double getBalance();
    public abstract void setBalance(double balance);
    public abstract boolean deposit(double amount);
    public abstract boolean withdraw(double amount);
    public abstract void payInterest();
    public abstract String getAccountDetails();
    public abstract void updateBalance(double amount);
    
    // ==================== ADDED GETTER METHODS ====================
    // These were missing and causing compilation errors
    
    /**
     * Get account type (Savings Account, Investment Account, Cheque Account)
     */
    public String getAccountType() {
        return this.getClass().getSimpleName()
            .replace("Account", "")
            .isEmpty() ? "Account" : 
            this.getClass().getSimpleName().replace("Account", "") + " Account";
    }
    
    /**
     * Get branch where account is held
     */
    public String getBranch() {
        return branch;
    }
    
    /**
     * Get date when account was opened
     */
    public LocalDate getDateOpened() {
        return dateOpened;
    }
    
    // ==================== OTHER GETTER METHODS ====================
    
    public void addTransaction(Transaction transaction) {
        if (transaction != null && transaction.validateTransaction()) {
            transactions.add(transaction);
        }
    }
    
    public List<Transaction> getTransactions() {
        return new ArrayList<>(transactions);
    }
    
    public Customer getCustomer() {
        return customer;
    }
    
    public void setBranch(String branch) {
        this.branch = branch;
    }
    
    public void setDateOpened(LocalDate dateOpened) {
        this.dateOpened = dateOpened;
    }
    
    @Override
    public String toString() {
        return String.format("Account: %s | Balance: BWP %.2f | Type: %s",
            accountNumber, balance, this.getClass().getSimpleName());
    }
}