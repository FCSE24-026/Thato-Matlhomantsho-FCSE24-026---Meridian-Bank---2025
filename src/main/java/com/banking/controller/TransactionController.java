package com.banking.controller;

import com.banking.model.*;
import com.banking.service.*;
import java.util.*;

public class TransactionController {
    private Bank bank;
    
    public TransactionController(Bank bank) {
        this.bank = bank;
    }
    
    /**
     * Process a deposit transaction
     */
    public boolean processDeposit(Account account, double amount) {
        if (account == null) {
            System.out.println("✗ Account is null");
            return false;
        }
        
        if (amount <= 0) {
            System.out.println("✗ Deposit amount must be greater than 0");
            return false;
        }
        
        boolean success = account.deposit(amount);

        if (success) {
            System.out.println("✓ Deposit processed successfully");
            // persist transaction and account state
            try {
                String txnId = "TXN_" + java.util.UUID.randomUUID().toString();
                Transaction txn = new Transaction(txnId, "DEPOSIT", amount, java.time.LocalDate.now(), account.getAccountNumber(), "SUCCESS");
                bank.recordTransaction(txn);
                bank.updateAccount(account);
            } catch (Exception ex) {
                System.out.println("⚠ Warning: could not persist deposit transaction: " + ex.getMessage());
            }
        }

        return success;
    }
    
    /**
     * Process a withdrawal transaction
     */
    public boolean processWithdrawal(Account account, double amount) {
        if (account == null) {
            System.out.println("✗ Account is null");
            return false;
        }
        
        if (amount <= 0) {
            System.out.println("✗ Withdrawal amount must be greater than 0");
            return false;
        }
        
        if (amount > account.getBalance()) {
            System.out.println("✗ Insufficient funds. Available: BWP " + 
                             String.format("%.2f", account.getBalance()));
            return false;
        }
        
        boolean success = account.withdraw(amount);

        if (success) {
            System.out.println("✓ Withdrawal processed successfully");
            try {
                String txnId = "TXN_" + java.util.UUID.randomUUID().toString();
                Transaction txn = new Transaction(txnId, "WITHDRAWAL", amount, java.time.LocalDate.now(), account.getAccountNumber(), "SUCCESS");
                bank.recordTransaction(txn);
                bank.updateAccount(account);
            } catch (Exception ex) {
                System.out.println("⚠ Warning: could not persist withdrawal transaction: " + ex.getMessage());
            }
        }

        return success;
    }
    
    /**
     * Get current balance for display
     */
    public double getAccountBalance(Account account) {
        if (account == null) {
            return 0.0;
        }
        return account.getBalance();
    }
    
    /**
     * Get formatted balance string
     */
    public String getFormattedBalance(Account account) {
        if (account == null) {
            return "N/A";
        }
        return "BWP " + String.format("%.2f", account.getBalance());
    }
    
    /**
     * Get transaction history for account
     */
    public List<Transaction> getAccountTransactionHistory(Account account) {
        if (account == null) {
            return new ArrayList<>();
        }
        return account.getTransactions();
    }
    
    /**
     * Process monthly interest for all accounts
     */
    public void processMonthlyInterestForBank() {
        System.out.println("\n--- Processing Monthly Interest ---");
        bank.processMonthlyInterest();
        System.out.println("✓ Monthly interest processed");
    }
    
    /**
     * Process monthly interest for specific customer
     */
    public void processMonthlyInterestForCustomer(Customer customer) {
        if (customer == null) {
            System.out.println("✗ Customer is null");
            return;
        }
        
        System.out.println("\n--- Processing Monthly Interest ---");
        for (Account account : customer.getAccounts()) {
            account.payInterest();
        }
        System.out.println("✓ Interest processed for customer");
    }
}