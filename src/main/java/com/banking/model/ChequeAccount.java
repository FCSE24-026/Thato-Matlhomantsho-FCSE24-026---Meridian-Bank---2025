package com.banking.model;

import java.time.LocalDate;

public class ChequeAccount extends Account {
    private static final long serialVersionUID = 1L;
    
    private String employer;
    private String companyAddress;
    private double salary;
    
    public ChequeAccount(String accountNumber, Customer customer, String employer,
                        String companyAddress) {
        super(accountNumber, customer);
        this.employer = employer;
        this.companyAddress = companyAddress;
        this.salary = 0.0;
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
            String txnId = "CHQ_" + System.nanoTime();
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
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            String txnId = "CHQ_" + System.nanoTime();
            Transaction txn = new Transaction(txnId, "WITHDRAWAL", amount, LocalDate.now(),
                                             accountNumber, "SUCCESS");
            addTransaction(txn);
            System.out.println("✓ Withdrawal successful. New balance: BWP " + String.format("%.2f", balance));
            return true;
        }
        System.out.println("✗ Insufficient funds or invalid amount");
        return false;
    }
    
    @Override
    public void payInterest() {
        System.out.println("ℹ Cheque accounts do not earn interest");
    }
    
    @Override
    public String getAccountDetails() {
        return String.format("Cheque Account: %s\nBalance: BWP %.2f\nEmployer: %s\nAddress: %s",
            accountNumber, balance, employer, companyAddress);
    }
    
    @Override
    public void updateBalance(double amount) {
        balance += amount;
    }
    
    public String getEmployer() { return employer; }
    public String getCompanyAddress() { return companyAddress; }
    public double getSalary() { return salary; }
    public void setEmployer(String employer) { this.employer = employer; }
    public void setCompanyAddress(String companyAddress) { this.companyAddress = companyAddress; }
    public void setSalary(double salary) { this.salary = salary; }
}