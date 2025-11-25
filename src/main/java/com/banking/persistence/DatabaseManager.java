package com.banking.persistence;

import com.banking.model.*;
import java.util.*;
import java.sql.*;

public class DatabaseManager {
    private CustomerDAO customerDAO;
    private AccountDAO accountDAO;
    private TransactionDAO transactionDAO;
    private AuditDAO auditDAO;
    private Connection connection;
    private static final String[] INIT_SCRIPTS = {
        "CREATE DATABASE IF NOT EXISTS banking_system;",
        "USE banking_system;",
        "CREATE TABLE IF NOT EXISTS CUSTOMER (" +
            "CUSTOMER_ID VARCHAR(50) PRIMARY KEY," +
            "FIRST_NAME VARCHAR(100) NOT NULL," +
            "SURNAME VARCHAR(100) NOT NULL," +
            "ADDRESS VARCHAR(255) NOT NULL," +
            "PHONE_NUMBER VARCHAR(20) NOT NULL," +
            "EMAIL VARCHAR(100) NOT NULL," +
            "PASSWORD_HASH VARCHAR(255)," +
            "ROLE VARCHAR(20) DEFAULT 'CUSTOMER'," +
            "APPROVED TINYINT(1) DEFAULT 0," +
            "SUSPENDED TINYINT(1) DEFAULT 0," +
            "DATE_OF_BIRTH DATE," +
            "CREATED_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP);",
        "CREATE TABLE IF NOT EXISTS ACCOUNT (" +
            "ACCOUNT_NUMBER VARCHAR(50) PRIMARY KEY," +
            "ACCOUNT_TYPE VARCHAR(20) NOT NULL," +
            "BALANCE DOUBLE NOT NULL DEFAULT 0.0," +
            "BRANCH VARCHAR(100) NOT NULL," +
            "CUSTOMER_ID VARCHAR(50) NOT NULL," +
            "DATE_OPENED DATE NOT NULL," +
            "LAST_INTEREST_DATE DATE," +
            "EMPLOYER VARCHAR(100)," +
            "EMPLOYER_ADDRESS VARCHAR(255)," +
            "INTEREST_RATE DOUBLE," +
            "MINIMUM_BALANCE DOUBLE," +
            "FOREIGN KEY (CUSTOMER_ID) REFERENCES CUSTOMER(CUSTOMER_ID)," +
            "CREATED_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP);",
        "CREATE TABLE IF NOT EXISTS TRANSACTION (" +
            "TRANSACTION_ID VARCHAR(50) PRIMARY KEY," +
            "TRANSACTION_TYPE VARCHAR(20) NOT NULL," +
            "AMOUNT DOUBLE NOT NULL," +
            "TRANSACTION_DATE DATE NOT NULL," +
            "ACCOUNT_NUMBER VARCHAR(50) NOT NULL," +
            "STATUS VARCHAR(20) NOT NULL," +
            "CREATED_TIMESTAMP TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
            "FOREIGN KEY (ACCOUNT_NUMBER) REFERENCES ACCOUNT(ACCOUNT_NUMBER));",
        "CREATE INDEX IF NOT EXISTS idx_customer_id ON ACCOUNT(CUSTOMER_ID);",
        "CREATE INDEX IF NOT EXISTS idx_account_number ON TRANSACTION(ACCOUNT_NUMBER);",
        "CREATE TABLE IF NOT EXISTS AUDIT_LOG (" +
            "ID VARCHAR(100) PRIMARY KEY," +
            "TIMESTAMP VARCHAR(50) NOT NULL," +
            "ACTOR_ID VARCHAR(50)," +
            "ACTOR_EMAIL VARCHAR(100)," +
            "ACTION_TYPE VARCHAR(100)," +
            "TARGET_TYPE VARCHAR(50)," +
            "TARGET_ID VARCHAR(100)," +
            "DETAILS TEXT," +
            "STATUS VARCHAR(50)," +
            "CREATED_TIMESTAMP TIMESTAMP DEFAULT CURRENT_TIMESTAMP);",
        "CREATE INDEX IF NOT EXISTS idx_audit_ts ON AUDIT_LOG(TIMESTAMP);"
    };

    public DatabaseManager() {
        this.connection = DatabaseConnection.getInstance().getConnection();
        this.customerDAO = new CustomerDAO();
        this.accountDAO = new AccountDAO();
        this.transactionDAO = new TransactionDAO();
        this.auditDAO = new AuditDAO();
        initializeDatabase();
    }

    /**
     * Initialize the database schema if it doesn't exist
     */
    public void initializeDatabase() {
        try {
            System.out.println("\n📊 Initializing Database Schema...");
            Statement stmt = connection.createStatement();
            
            // Create database
            stmt.executeUpdate(INIT_SCRIPTS[0]);
            System.out.println("✓ Database created/verified");
            
            // Use database
            stmt.executeUpdate(INIT_SCRIPTS[1]);
            System.out.println("✓ Database selected");
            
            // Create tables
            for (int i = 2; i < INIT_SCRIPTS.length; i++) {
                try {
                    stmt.executeUpdate(INIT_SCRIPTS[i]);
                    System.out.println("✓ Schema initialized");
                } catch (SQLException e) {
                    // Table might already exist, which is fine
                    if (!e.getMessage().contains("already exists")) {
                        System.out.println("⚠ Warning: " + e.getMessage());
                    }
                }
            }
            // Ensure CUSTOMER has ROLE and APPROVED columns (for upgrades)
            try {
                stmt.executeUpdate("ALTER TABLE CUSTOMER ADD COLUMN PASSWORD_HASH VARCHAR(255)");
                System.out.println("✓ CUSTOMER.PASSWORD_HASH column added");
            } catch (SQLException ignored) {}
            try {
                stmt.executeUpdate("ALTER TABLE CUSTOMER ADD COLUMN ROLE VARCHAR(20) DEFAULT 'CUSTOMER'");
                System.out.println("✓ CUSTOMER.ROLE column added");
            } catch (SQLException ignored) {}
            try {
                stmt.executeUpdate("ALTER TABLE CUSTOMER ADD COLUMN APPROVED TINYINT(1) DEFAULT 0");
                System.out.println("✓ CUSTOMER.APPROVED column added");
            } catch (SQLException ignored) {}
            try {
                stmt.executeUpdate("ALTER TABLE CUSTOMER ADD COLUMN SUSPENDED TINYINT(1) DEFAULT 0");
                System.out.println("✓ CUSTOMER.SUSPENDED column added");
            } catch (SQLException ignored) {}
            // Ensure TRANSACTION has approval tracking columns
            try {
                stmt.executeUpdate("ALTER TABLE TRANSACTION ADD COLUMN APPROVAL_STATUS VARCHAR(20) DEFAULT 'PENDING'");
                System.out.println("✓ TRANSACTION.APPROVAL_STATUS column added");
            } catch (SQLException ignored) {}
            try {
                stmt.executeUpdate("ALTER TABLE TRANSACTION ADD COLUMN APPROVER_ID INT NULL");
                System.out.println("✓ TRANSACTION.APPROVER_ID column added");
            } catch (SQLException ignored) {}
            try {
                stmt.executeUpdate("ALTER TABLE TRANSACTION ADD COLUMN APPROVAL_DATE DATE NULL");
                System.out.println("✓ TRANSACTION.APPROVAL_DATE column added");
            } catch (SQLException ignored) {}
            try {
                stmt.executeUpdate("ALTER TABLE TRANSACTION ADD COLUMN DENIAL_REASON VARCHAR(255) NULL");
                System.out.println("✓ TRANSACTION.DENIAL_REASON column added");
            } catch (SQLException ignored) {}
            try {
                stmt.executeUpdate("ALTER TABLE AUDIT_LOG ADD COLUMN STATUS VARCHAR(50) DEFAULT 'LOGGED'");
                System.out.println("✓ AUDIT_LOG.STATUS column ensured");
            } catch (SQLException ignored) {}
            stmt.close();
            System.out.println("✓ Database schema ready\n");
        } catch (SQLException e) {
            System.out.println("✗ Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
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

    // Audit operations
    public boolean saveAuditLog(com.banking.model.AuditLog log) {
        return auditDAO.create(log);
    }

    public java.util.List<com.banking.model.AuditLog> getAllAuditLogs() {
        return auditDAO.readAll();
    }

    // Transaction operations
    public boolean saveTransaction(Transaction transaction) { return transactionDAO.create(transaction); }
    public Transaction getTransaction(String transactionId) { return transactionDAO.read(transactionId); }
    public List<Transaction> getAccountTransactions(String accountNumber) { return transactionDAO.readByAccount(accountNumber); }
    public boolean updateTransactionStatus(String transactionId, String status) { return transactionDAO.updateStatus(transactionId, status); }
    public boolean updateTransaction(Transaction transaction) { return transactionDAO.update(transaction); }
    public boolean deleteTransaction(String transactionId) { return transactionDAO.delete(transactionId); }
}