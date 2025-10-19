package com.banking.persistence;

import com.banking.model.*;
import java.util.*;

public class DatabaseManager {
    private CustomerDAO customerDAO;
    private AccountDAO accountDAO;
    private TransactionDAO transactionDAO;

    public DatabaseManager() {
        this.customerDAO = new CustomerDAO();
        this.accountDAO = new AccountDAO();
        this.transactionDAO = new TransactionDAO();
    }

    // Customer operations
    public boolean saveCustomer(Customer customer) { return customerDAO.create(customer); }
    public Customer getCustomer(String customerId) { return customerDAO.read(customerId); }
    public List<Customer> getAllCustomers() { return customerDAO.readAll(); }
    public boolean updateCustomer(Customer customer) { return customerDAO.update(customer); }
    public boolean deleteCustomer(String customerId) { return customerDAO.delete(customerId); }

    // Account operations
    public boolean saveAccount(Account account) { return accountDAO.create(account); }
    public Account getAccount(String accountNumber) { return accountDAO.read(accountNumber); }
    public List<Account> getCustomerAccounts(String customerId) { return accountDAO.readByCustomer(customerId); }
    public boolean updateAccount(Account account) { return accountDAO.update(account); }
    public boolean deleteAccount(String accountNumber) { return accountDAO.delete(accountNumber); }

    // Transaction operations
    public boolean saveTransaction(Transaction transaction) { return transactionDAO.create(transaction); }
    public Transaction getTransaction(String transactionId) { return transactionDAO.read(transactionId); }
    public List<Transaction> getAccountTransactions(String accountNumber) { return transactionDAO.readByAccount(accountNumber); }
    public boolean updateTransactionStatus(String transactionId, String status) { return transactionDAO.updateStatus(transactionId, status); }
    public boolean deleteTransaction(String transactionId) { return transactionDAO.delete(transactionId); }
}