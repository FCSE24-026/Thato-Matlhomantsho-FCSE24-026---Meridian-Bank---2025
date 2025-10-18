package com.banking.model;

import java.time.LocalDate;

public class SavingsAccount extends Account {
    private static final long serialVersionUID = 1L;
    private static final double MONTHLY_INTEREST_RATE = 0.0005;
    
    private double interestRate;
    private LocalDate lastInterestDate;
    
    public SavingsAccount(String accountNumber, Customer customer) {
        super(accountNumber, customer);
        this.interestRate = MONTHLY_INTEREST_RATE;
        this.lastInterestDate = LocalDate.now();
    }
    
    @Override
    public String getAccountNumber() {
        return accountNumber;
    }
    
    @Override
    public double getBalance() {
        return balance;
    }
    
    @Override
    public void setBalance(double balance) {
        if (balance >= 0) {
            this.balance = balance;
        }
    }
    
    @Override
    public boolean deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            String txnId = "SAV_" + System.nanoTime();
            Transaction txn = new Transaction(txnId, "DEPOSIT", amount, LocalDate.now(),
                                             accountNumber, "SUCCESS");
            addTransaction(txn);
            System.out.println("✓ Deposit successful. New balance: BWP " + String.format("%.2f", balance));
            return true;
        }
        System.out.println("✗ Invalid deposit amount");
        return false;
    }
    
    @Override
    public boolean withdraw(double amount) {
        System.out.println("✗ Withdrawals are not allowed on Savings Accounts");
        return false;
    }
    
    @Override
    public void payInterest() {
        if (balance > 0) {
            double interest = balance * interestRate;
            balance += interest;
            lastInterestDate = LocalDate.now();
            String txnId = "INT_" + System.nanoTime();
            Transaction txn = new Transaction(txnId, "INTEREST", interest, LocalDate.now(),
                                             accountNumber, "SUCCESS");
            addTransaction(txn);
            System.out.println("✓ Interest paid: BWP " + String.format("%.4f", interest) + 
                             ". New balance: BWP " + String.format("%.2f", balance));
        }
    }
    
    @Override
    public String getAccountDetails() {
        return String.format("Savings Account: %s\nBalance: BWP %.2f\nInterest Rate: %.4f%%\nNo Withdrawals Allowed",
            accountNumber, balance, interestRate * 100);
    }
    
    @Override
    public void updateBalance(double amount) {
        if (amount > 0) {
            balance += amount;
        }
    }
    
    public double getInterestRate() { return interestRate; }
    public LocalDate getLastInterestDate() { return lastInterestDate; }
    public void setInterestRate(double rate) { this.interestRate = rate; }
}