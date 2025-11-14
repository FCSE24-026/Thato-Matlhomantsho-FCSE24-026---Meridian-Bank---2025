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
     * Open a new Money Market Account (requires minimum BWP 1000)
     */
    public Account openMoneyMarketAccount(Customer customer, double initialDeposit) {
        if (customer == null) {
            System.out.println("✗ Customer is null");
            return null;
        }
        
        if (initialDeposit < MoneyMarketAccount.getMinimumOpening()) {
            System.out.println("✗ Minimum opening balance: BWP " + 
                             MoneyMarketAccount.getMinimumOpening());
            return null;
        }
        
        Account account = bank.openAccount(customer, "money market");
        if (account != null) {
            account.deposit(initialDeposit);
        }
        return account;
    }
    
    /**
     * Open a new Certificate of Deposit Account (requires minimum BWP 500)
     */
    public Account openCertificateOfDepositAccount(Customer customer, double initialDeposit, int termMonths) {
        if (customer == null) {
            System.out.println("✗ Customer is null");
            return null;
        }
        
        if (initialDeposit < CertificateOfDepositAccount.getMinimumOpening()) {
            System.out.println("✗ Minimum opening balance: BWP " + 
                             CertificateOfDepositAccount.getMinimumOpening());
            return null;
        }
        
        if (termMonths <= 0) {
            System.out.println("✗ CD term must be greater than 0 months");
            return null;
        }
        
        // For simplicity, directly create CD account via bank service
        Account account = bank.openAccount(customer, "cd");
        if (account != null) {
            account.deposit(initialDeposit);
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
        return bank.getAllAccountsForCustomer(customer.getCustomerId());
    }
    
    /**
     * Get specific account by number
     */
    public Account getAccountByNumber(Customer customer, String accountNumber) {
        if (customer == null || accountNumber == null) {
            return null;
        }
        return bank.getAccount(accountNumber);
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
        return bank.getAllAccountsForCustomer(customer.getCustomerId()).stream()
            .mapToDouble(Account::getBalance)
            .sum();
    }
    
    /**
     * Get total balance for a user (by customer ID) - used by ModernBankingApp
     */
    public double getTotalBalance(String customerId) {
        if (customerId == null) return 0.0;
        for (Customer customer : bank.getAllCustomers()) {
            if (customer.getCustomerId().equals(customerId)) {
                return getTotalCustomerBalance(customer);
            }
        }
        return 0.0;
    }
    
    /**
     * Get user accounts by user ID - used by ModernBankingApp
     */
    public List<Account> getUserAccounts(String customerId) {
        if (customerId == null) return new ArrayList<>();
        // Find customer by ID and return accounts
        for (Customer customer : bank.getAllCustomers()) {
            if (customer.getCustomerId().equals(customerId)) {
                return bank.getAllAccountsForCustomer(customer.getCustomerId());
            }
        }
        return new ArrayList<>();
    }
    
    /**
     * Get transaction history for a user - used by ModernBankingApp
     */
    public List<Transaction> getTransactionHistory(String customerId) {
        List<Transaction> allTransactions = new ArrayList<>();
        if (customerId == null) return allTransactions;
        for (Customer customer : bank.getAllCustomers()) {
            if (customer.getCustomerId().equals(customerId)) {
                for (Account account : bank.getAllAccountsForCustomer(customer.getCustomerId())) {
                    allTransactions.addAll(account.getTransactions());
                }
            }
        }
        return allTransactions;
    }
    
    /**
     * Create account for user - used by ModernBankingApp
     */
    public boolean createAccount(String customerId, String accountType) {
        if (customerId == null) return false;
        for (Customer customer : bank.getAllCustomers()) {
            if (customer.getCustomerId().equals(customerId)) {
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

    /**
     * Owner-checked transfer: ensures caller owns the source account before transfer.
     * callerCustomerId is the hashed customerId passed from the UI (currentUser.getUserId()).
     */
    public boolean transferFunds(String callerCustomerId, String fromAccountId, String toAccountId,
                                 double amount, String description) {
        // locate the owner of the source account
        Customer owner = null;
        for (Customer c : bank.getAllCustomers()) {
            for (Account a : bank.getAllAccountsForCustomer(c.getCustomerId())) {
                if (a.getAccountId().equals(fromAccountId)) {
                    owner = c;
                    break;
                }
            }
            if (owner != null) break;
        }

        if (owner == null) {
            System.out.println("✗ Source account owner not found");
            return false;
        }

        if (!owner.getCustomerId().equals(callerCustomerId)) {
            System.out.println("✗ Unauthorized: caller does not own the source account");
            return false;
        }

        // delegate to existing transfer method
        return transferFunds(fromAccountId, toAccountId, amount, description);
    }
}