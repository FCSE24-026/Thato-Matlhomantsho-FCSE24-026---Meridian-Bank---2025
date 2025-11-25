package com.banking.persistence;

import com.banking.model.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {
    private Connection connection;

    public AccountDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    // CREATE
    public boolean create(Account account) {
        String sql = "INSERT INTO ACCOUNT (ACCOUNT_NUMBER, ACCOUNT_TYPE, BALANCE, BRANCH, CUSTOMER_ID, DATE_OPENED, INTEREST_RATE, MINIMUM_BALANCE) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, account.getAccountNumber());
            pstmt.setString(2, account.getAccountType());
            pstmt.setDouble(3, account.getBalance());
            pstmt.setString(4, account.getBranch());
            pstmt.setString(5, account.getCustomer().getCustomerId());
            pstmt.setDate(6, java.sql.Date.valueOf(account.getDateOpened()));

            if (account instanceof SavingsAccount) {
               SavingsAccount sa = (SavingsAccount) account;
               pstmt.setDouble(7, sa.getInterestRate());
               pstmt.setDouble(8, 0.0);
            } else if (account instanceof InvestmentAccount) {
               InvestmentAccount ia = (InvestmentAccount) account;
               pstmt.setDouble(7, ia.getInterestRate());
               pstmt.setDouble(8, ia.getMinimumBalance());
            } else {
               pstmt.setDouble(7, 0.0);
               pstmt.setDouble(8, 0.0);
            }


            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("✓ Account created: " + account.getAccountNumber());
                return true;
            }
        } catch (SQLException e) {
            System.out.println("✗ Error creating account: " + e.getMessage());
        }
        return false;
    }

    // READ
    public Account read(String accountNumber) {
        String sql = "SELECT * FROM ACCOUNT WHERE ACCOUNT_NUMBER = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String accountType = rs.getString("ACCOUNT_TYPE");
                // use a shallow customer load to avoid recursive account<->customer loading
                Customer customer = new CustomerDAO().readShallow(rs.getString("CUSTOMER_ID"));
                Account account = createAccountFromType(accountNumber, accountType, customer);

                if (account != null) {
                    account.setBalance(rs.getDouble("BALANCE"));
                    // load persisted transactions for this account
                    try {
                        java.util.List<com.banking.model.Transaction> txns = new TransactionDAO().readByAccount(accountNumber);
                        for (com.banking.model.Transaction t : txns) {
                            account.addTransaction(t);
                        }
                        System.out.println("✓ Loaded " + txns.size() + " transactions for account " + accountNumber);
                    } catch (Exception e) {
                        System.out.println("⚠ Warning: could not load transactions for account " + accountNumber + ": " + e.getMessage());
                    }
                    return account;
                }
            }
        } catch (SQLException e) {
            System.out.println("✗ Error reading account: " + e.getMessage());
        }
        return null;
    }

    // READ ALL BY CUSTOMER
    public List<Account> readByCustomer(String customerId) {
        List<Account> accounts = new ArrayList<>();
        String sql;
        
        // Special case: "*" means get all accounts
        if ("*".equals(customerId)) {
            sql = "SELECT * FROM ACCOUNT";
        } else {
            sql = "SELECT * FROM ACCOUNT WHERE CUSTOMER_ID = ?";
        }
        
        try {
            if ("*".equals(customerId)) {
                var stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql);
                
                while (rs.next()) {
                    String accountNumber = rs.getString("ACCOUNT_NUMBER");
                    String accountType = rs.getString("ACCOUNT_TYPE");
                    String custId = rs.getString("CUSTOMER_ID");
                    // use a shallow customer load to avoid recursive account<->customer loading
                    Customer customer = new CustomerDAO().readShallow(custId);
                    Account account = createAccountFromType(accountNumber, accountType, customer);

                    if (account != null) {
                        account.setBalance(rs.getDouble("BALANCE"));
                        // load persisted transactions for this account
                        try {
                            java.util.List<com.banking.model.Transaction> txns = new TransactionDAO().readByAccount(accountNumber);
                            for (com.banking.model.Transaction t : txns) {
                                account.addTransaction(t);
                            }
                        } catch (Exception e) {
                            System.out.println("⚠ Warning: could not load transactions for account " + accountNumber + ": " + e.getMessage());
                        }
                        accounts.add(account);
                    }
                }
                stmt.close();
            } else {
                try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                    pstmt.setString(1, customerId);
                    ResultSet rs = pstmt.executeQuery();
                    // load a shallow customer (avoid recursive loading of accounts)
                    Customer customer = new CustomerDAO().readShallow(customerId);

                    while (rs.next()) {
                        String accountNumber = rs.getString("ACCOUNT_NUMBER");
                        String accountType = rs.getString("ACCOUNT_TYPE");
                        Account account = createAccountFromType(accountNumber, accountType, customer);

                        if (account != null) {
                            account.setBalance(rs.getDouble("BALANCE"));
                            accounts.add(account);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("✗ Error reading accounts: " + e.getMessage());
        }
        return accounts;
    }

    // Helper to map stored ACCOUNT_TYPE strings to concrete Account instances
    private Account createAccountFromType(String accountNumber, String accountType, Customer customer) {
        if (accountType == null) return null;
        String at = accountType.toLowerCase();

        if (at.contains("saving")) {
            return new SavingsAccount(accountNumber, customer);
        } else if (at.contains("investment")) {
            return new InvestmentAccount(accountNumber, customer);
        } else if (at.contains("cheque") || at.contains("check")) {
            return new ChequeAccount(accountNumber, customer, "", "");
        }
        return null;
    }

    // UPDATE
    public boolean update(Account account) {
        String sql = "UPDATE ACCOUNT SET BALANCE=? WHERE ACCOUNT_NUMBER=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDouble(1, account.getBalance());
            pstmt.setString(2, account.getAccountNumber());

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("✓ Account updated: " + account.getAccountNumber());
                return true;
            }
        } catch (SQLException e) {
            System.out.println("✗ Error updating account: " + e.getMessage());
        }
        return false;
    }

    // DELETE
    public boolean delete(String accountNumber) {
        String sql = "DELETE FROM ACCOUNT WHERE ACCOUNT_NUMBER=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, accountNumber);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("✓ Account deleted: " + accountNumber);
                return true;
            }
        } catch (SQLException e) {
            System.out.println("✗ Error deleting account: " + e.getMessage());
        }
        return false;
    }
}
