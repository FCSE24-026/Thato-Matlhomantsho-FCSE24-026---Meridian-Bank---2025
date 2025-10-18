package com.banking.model;

import java.time.LocalDate;

public class InvestmentAccount extends Account {
    private static final long serialVersionUID = 1L;
    private static final double MINIMUM_OPENING = 500.0;
    private static final double MONTHLY_INTEREST_RATE = 0.05;
    
    private double interestRate;
    private double minimumBalance;
    private LocalDate lastInterestDate;
    
    public InvestmentAccount(String accountNumber, Customer customer) {
        super(accountNumber, customer);
        this.interestRate = MONTHLY_INTEREST_RATE;
        this.minimumBalance = MINIMUM_OPENING;
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
        if (balance >= minimumBalance) {
            this.balance = balance;
        }
    }
    
    @Override
    public boolean deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            String txnId = "INV_" + System.nanoTime();
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
        if (amount > 0 && (balance - amount) >= minimumBalance) {
            balance -= amount;
            String txnId = "INV_" + System.nanoTime();
            Transaction txn = new Transaction(txnId, "WITHDRAWAL", amount, LocalDate.now(),
                                             accountNumber, "SUCCESS");
            addTransaction(txn);
            System.out.println("✓ Withdrawal successful. New balance: BWP " + String.format("%.2f", balance));
            return true;
        }
        System.out.println("✗ Insufficient funds or would fall below minimum balance");
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
            System.out.println("✓ Interest paid: BWP " + String.format("%.2f", interest) + 
                             ". New balance: BWP " + String.format("%.2f", balance));
        }
    }
    
    @Override
    public String getAccountDetails() {
        return String.format("Investment Account: %s\nBalance: BWP %.2f\nInterest Rate: %.2f%%\nMinimum Balance: BWP %.2f",
            accountNumber, balance, interestRate * 100, minimumBalance);
    }
    
    @Override
    public void updateBalance(double amount) {
        if ((balance + amount) >= minimumBalance) {
            balance += amount;
        }
    }
    
    public double getInterestRate() { return interestRate; }
    public double getMinimumBalance() { return minimumBalance; }
    public LocalDate getLastInterestDate() { return lastInterestDate; }
    public void setInterestRate(double rate) { this.interestRate = rate; }
    public static double getMinimumOpening() { return MINIMUM_OPENING; }
}