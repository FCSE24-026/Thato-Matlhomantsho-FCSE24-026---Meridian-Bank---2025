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
    
    @Override
    public String toString() {
        return String.format("Account: %s | Balance: BWP %.2f | Type: %s",
            accountNumber, balance, this.getClass().getSimpleName());
    }
}