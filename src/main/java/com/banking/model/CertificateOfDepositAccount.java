package com.banking.model;

import java.time.LocalDate;

public class CertificateOfDepositAccount extends Account {
    private static final long serialVersionUID = 1L;
    private static final double MINIMUM_OPENING = 500.0;
    private static final double MONTHLY_INTEREST_RATE = 0.10; // 10% - highest interest rate
    
    private double interestRate;
    private double minimumBalance;
    private LocalDate maturityDate;
    private int termMonths; // CD term in months
    private boolean isMature;
    private LocalDate lastInterestDate;
    
    public CertificateOfDepositAccount(String accountNumber, Customer customer, int termMonths) {
        super(accountNumber, customer);
        this.interestRate = MONTHLY_INTEREST_RATE;
        this.minimumBalance = MINIMUM_OPENING;
        this.termMonths = termMonths;
        this.maturityDate = LocalDate.now().plusMonths(termMonths);
        this.isMature = false;
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
            String txnId = "CD_" + System.nanoTime();
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
        // CDs typically have penalties for early withdrawal
        if (!isMature && LocalDate.now().isBefore(maturityDate)) {
            System.out.println("✗ CD has not matured yet. Maturity date: " + maturityDate);
            System.out.println("  Early withdrawal may incur penalties");
            return false;
        }
        
        if (amount > 0 && (balance - amount) >= 0) {
            balance -= amount;
            String txnId = "CD_" + System.nanoTime();
            Transaction txn = new Transaction(txnId, "WITHDRAWAL", amount, LocalDate.now(),
                                             accountNumber, "SUCCESS");
            addTransaction(txn);
            System.out.println("✓ Withdrawal successful. New balance: BWP " + String.format("%.2f", balance));
            return true;
        }
        System.out.println("✗ Insufficient funds");
        return false;
    }
    
    @Override
    public void payInterest() {
        if (balance > 0) {
            double interest = balance * interestRate;
            balance += interest;
            lastInterestDate = LocalDate.now();
            
            // Check if CD has matured
            if (LocalDate.now().isAfter(maturityDate) || LocalDate.now().isEqual(maturityDate)) {
                isMature = true;
            }
            
            String txnId = "INT_" + System.nanoTime();
            Transaction txn = new Transaction(txnId, "INTEREST", interest, LocalDate.now(),
                                             accountNumber, "SUCCESS");
            addTransaction(txn);
            System.out.println("✓ Interest paid: BWP " + String.format("%.2f", interest) + 
                             ". New balance: BWP " + String.format("%.2f", balance));
            
            if (isMature) {
                System.out.println("  ℹ CD has matured and can now be withdrawn");
            }
        }
    }
    
    @Override
    public String getAccountDetails() {
        String status = isMature ? "MATURE" : "ACTIVE";
        return String.format("Certificate of Deposit (CD): %s\nBalance: BWP %.2f\nInterest Rate: %.2f%%\nMinimum Balance: BWP %.2f\nTerm: %d months\nMaturity Date: %s\nStatus: %s",
            accountNumber, balance, interestRate * 100, minimumBalance, termMonths, maturityDate, status);
    }
    
    @Override
    public void updateBalance(double amount) {
        if ((balance + amount) >= minimumBalance) {
            balance += amount;
        }
    }
    
    public double getInterestRate() { return interestRate; }
    public double getMinimumBalance() { return minimumBalance; }
    public LocalDate getMaturityDate() { return maturityDate; }
    public LocalDate getLastInterestDate() { return lastInterestDate; }
    public void setInterestRate(double rate) { this.interestRate = rate; }
    public int getTermMonths() { return termMonths; }
    public boolean isMature() { return isMature; }
    public static double getMinimumOpening() { return MINIMUM_OPENING; }
}
