package com.banking.controller;

import com.banking.model.*;
import com.banking.service.*;
import java.util.*;

public class AccountController {
    private Bank bank;
    
    public AccountController(Bank bank) {
        this.bank = bank;
    }
    
    /**
     * Open a new Savings Account
     */
    public Account openSavingsAccount(Customer customer) {
        if (customer == null) {
            System.out.println("✗ Customer is null");
            return null;
        }
        Account account = bank.openAccount(customer, "savings");
        return account;
    }
    
    /**
     * Open a new Investment Account (requires minimum BWP 500)
     */
    public Account openInvestmentAccount(Customer customer, double initialDeposit) {
        if (customer == null) {
            System.out.println("✗ Customer is null");
            return null;
        }
        
        if (initialDeposit < InvestmentAccount.getMinimumOpening()) {
            System.out.println("✗ Minimum opening balance: BWP " + 
                             InvestmentAccount.getMinimumOpening());
            return null;
        }
        
        Account account = bank.openAccount(customer, "investment");
        if (account != null) {
            account.deposit(initialDeposit);
        }
        return account;
    }
    
    /**
     * Open a new Cheque Account (requires employer info)
     */
    public Account openChequeAccount(Customer customer, String employerName, 
                                     String employerAddress) {
        if (customer == null) {
            System.out.println("✗ Customer is null");
            return null;
        }
        
        if (employerName == null || employerName.isEmpty()) {
            System.out.println("✗ Employer name is required");
            return null;
        }
        
        if (employerAddress == null || employerAddress.isEmpty()) {
            System.out.println("✗ Employer address is required");
            return null;
        }
        
        Account account = bank.openAccount(customer, "cheque");
        if (account instanceof ChequeAccount) {
            ChequeAccount chequeAcc = (ChequeAccount) account;
            chequeAcc.setEmployer(employerName);
            chequeAcc.setCompanyAddress(employerAddress);
        }
        return account;
    }
    
    /**
     * Get all accounts for a customer
     */
    public List<Account> getCustomerAccounts(Customer customer) {
        if (customer == null) {
            return new ArrayList<>();
        }
        return customer.getAccounts();
    }
    
    /**
     * Get specific account by number
     */
    public Account getAccountByNumber(Customer customer, String accountNumber) {
        if (customer == null || accountNumber == null) {
            return null;
        }
        return customer.getAccountByNumber(accountNumber);
    }
    
    /**
     * Get account details formatted for display
     */
    public String getAccountDetailsForDisplay(Account account) {
        if (account == null) {
            return "Account not found";
        }
        return account.getAccountDetails();
    }
    
    /**
     * Get total balance across all customer accounts
     */
    public double getTotalCustomerBalance(Customer customer) {
        if (customer == null) {
            return 0.0;
        }
        return customer.getAccounts().stream()
            .mapToDouble(Account::getBalance)
            .sum();
    }
    
    /**
     * Get total balance for a user (by customer ID) - used by ModernBankingApp
     */
    public double getTotalBalance(int customerId) {
        // Find customer by ID and return total balance
        for (Customer customer : bank.getAllCustomers()) {
            if (customer.getCustomerId().hashCode() == customerId) {
                return getTotalCustomerBalance(customer);
            }
        }
        return 0.0;
    }
    
    /**
     * Get user accounts by user ID - used by ModernBankingApp
     */
    public List<Account> getUserAccounts(int customerId) {
        // Find customer by ID and return accounts
        for (Customer customer : bank.getAllCustomers()) {
            if (customer.getCustomerId().hashCode() == customerId) {
                return customer.getAccounts();
            }
        }
        return new ArrayList<>();
    }
    
    /**
     * Get transaction history for a user - used by ModernBankingApp
     */
    public List<Transaction> getTransactionHistory(int customerId) {
        List<Transaction> allTransactions = new ArrayList<>();
        for (Customer customer : bank.getAllCustomers()) {
            if (customer.getCustomerId().hashCode() == customerId) {
                for (Account account : customer.getAccounts()) {
                    allTransactions.addAll(account.getTransactions());
                }
            }
        }
        return allTransactions;
    }
    
    /**
     * Create account for user - used by ModernBankingApp
     */
    public boolean createAccount(int customerId, String accountType) {
        for (Customer customer : bank.getAllCustomers()) {
            if (customer.getCustomerId().hashCode() == customerId) {
                Account account = bank.openAccount(customer, accountType.toLowerCase());
                return account != null;
            }
        }
        return false;
    }
    
    /**
     * Transfer funds between accounts - used by ModernBankingApp
     */
    public boolean transferFunds(String fromAccountId, String toAccountId, 
                                 double amount, String description) {
        if (fromAccountId == null || toAccountId == null || amount <= 0) {
            System.out.println("✗ Invalid transfer parameters");
            return false;
        }
        
        // Find accounts by ID
        Account fromAccount = null;
        Account toAccount = null;
        
        for (Account account : bank.getAllAccounts()) {
            if (account.getAccountId().equals(fromAccountId)) {
                fromAccount = account;
            }
            if (account.getAccountId().equals(toAccountId)) {
                toAccount = account;
            }
        }
        
        if (fromAccount == null || toAccount == null) {
            System.out.println("✗ One or both accounts not found");
            return false;
        }
        
        if (fromAccount.getBalance() < amount) {
            System.out.println("✗ Insufficient funds");
            return false;
        }
        
        // Perform transfer
        boolean success = fromAccount.withdraw(amount) && toAccount.deposit(amount);
        if (success) {
            System.out.println("✓ Transfer of " + amount + " completed: " + 
                             (description != null ? description : "No description"));
        }
        return success;
    }
}