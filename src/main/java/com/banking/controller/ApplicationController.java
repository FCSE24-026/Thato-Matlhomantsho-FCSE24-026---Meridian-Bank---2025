package com.banking.controller;

import com.banking.model.*;
import com.banking.service.*;
import java.util.*;

public class ApplicationController {
    private Bank bank;
    private LoginController loginController;
    private CustomerController customerController;
    private AccountController accountController;
    private TransactionController transactionController;
    
    public ApplicationController(Bank bank) {
        this.bank = bank;
        this.loginController = new LoginController(bank);
        this.customerController = new CustomerController(bank);
        this.accountController = new AccountController(bank);
        this.transactionController = new TransactionController(bank);
    }
    
    // Provide access to all sub-controllers
    public LoginController getLoginController() {
        return loginController;
    }
    
    public CustomerController getCustomerController() {
        return customerController;
    }
    
    public AccountController getAccountController() {
        return accountController;
    }
    
    public TransactionController getTransactionController() {
        return transactionController;
    }
    
    public Bank getBank() {
        return bank;
    }
}