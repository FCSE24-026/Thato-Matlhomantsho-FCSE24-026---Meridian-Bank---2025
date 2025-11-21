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
        if (account != null) {
            try { bank.logAction(customer.getCustomerId(), customer.getEmail(), "OPEN_ACCOUNT", "ACCOUNT", account.getAccountId(), "Opened savings account", "OK"); } catch (Exception ex) {}
        }
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
            // Persist the account with the initial deposit
            bank.updateAccount(account);
            try { bank.logAction(customer.getCustomerId(), customer.getEmail(), "OPEN_ACCOUNT", "ACCOUNT", account.getAccountId(), "Opened investment account", "OK"); } catch (Exception ex) {}
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
            try { bank.logAction(customer.getCustomerId(), customer.getEmail(), "OPEN_ACCOUNT", "ACCOUNT", account.getAccountId(), "Opened cheque account", "OK"); } catch (Exception ex) {}
        }
        return account;
    }
    
    /**
     * Open a new Money Market Account (requires minimum BWP 1000)
     */
    
    
    /**
     * Open a new Certificate of Deposit Account (requires minimum BWP 500)
     */
    

    
    /**
     * Get all accounts for a customer
     */
    public List<Account> getCustomerAccounts(Customer customer) {
        if (customer == null) {
            return new ArrayList<>();
        }
        // Always fetch fresh from DB to ensure we have latest balances and transactions
        List<Account> accounts = bank.getAllAccountsForCustomer(customer.getCustomerId());
        // Update the customer's in-memory accounts list with fresh data
        if (customer != null) {
            customer.getAccounts().clear();
            customer.getAccounts().addAll(accounts);
        }
        return accounts;
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
        // Always fetch fresh from DB instead of using in-memory cached accounts
        for (Customer customer : bank.getAllCustomers()) {
            if (customer.getCustomerId().equals(customerId)) {
                // Fetch fresh from DB and sync with in-memory customer object
                List<Account> accounts = bank.getAllAccountsForCustomer(customer.getCustomerId());
                customer.getAccounts().clear();
                customer.getAccounts().addAll(accounts);
                return accounts;
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
                    List<Transaction> txns = account.getTransactions();
                    System.out.println("✓ Loaded " + txns.size() + " transactions for account " + account.getAccountNumber());
                    allTransactions.addAll(txns);
                }
            }
        }
        System.out.println("✓ Total transactions for customer " + customerId + ": " + allTransactions.size());
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
            try {
                // actor is the owner of the source account
                // find owner
                String ownerId = null; String ownerEmail = null;
                for (Customer c : bank.getAllCustomers()) {
                    for (Account a : bank.getAllAccountsForCustomer(c.getCustomerId())) {
                        if (a.getAccountId().equals(fromAccountId)) { ownerId = c.getCustomerId(); ownerEmail = c.getEmail(); break; }
                    }
                    if (ownerId != null) break;
                }
                bank.logAction(ownerId, ownerEmail, "TRANSFER", "TRANSACTION", fromAccountId + "->" + toAccountId, "Amount: " + amount + (description != null ? " desc=" + description : ""), "OK");
                // persist transaction records and update accounts
                try {
                    String txOutId = "TXN_" + java.util.UUID.randomUUID().toString();
                    Transaction txOut = new Transaction(txOutId, "WITHDRAWAL", amount, java.time.LocalDate.now(), fromAccount.getAccountNumber(), "SUCCESS");
                    bank.recordTransaction(txOut);
                    bank.updateAccount(fromAccount);

                    String txInId = "TXN_" + java.util.UUID.randomUUID().toString();
                    Transaction txIn = new Transaction(txInId, "DEPOSIT", amount, java.time.LocalDate.now(), toAccount.getAccountNumber(), "SUCCESS");
                    bank.recordTransaction(txIn);
                    bank.updateAccount(toAccount);
                } catch (Exception ex) {
                    System.out.println("⚠ Warning: could not persist transfer transactions: " + ex.getMessage());
                }
            } catch (Exception ex) {}
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