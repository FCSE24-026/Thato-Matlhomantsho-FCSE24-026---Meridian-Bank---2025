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
}