package com.banking.persistence;

import com.banking.model.Transaction;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

public class TransactionDAO {
    private Connection connection;

    public TransactionDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    // CREATE
    public boolean create(Transaction transaction) {
        String sql = "INSERT INTO TRANSACTION (TRANSACTION_ID, TRANSACTION_TYPE, AMOUNT, TRANSACTION_DATE, ACCOUNT_NUMBER, STATUS) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, transaction.getTransactionId());
            pstmt.setString(2, transaction.getTransactionType());
            pstmt.setDouble(3, transaction.getAmount());
            pstmt.setDate(4, java.sql.Date.valueOf(transaction.getDate()));
            pstmt.setString(5, transaction.getAccountNumber());
            pstmt.setString(6, transaction.getStatus());

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("✓ Transaction recorded: " + transaction.getTransactionId());
                return true;
            }
        } catch (SQLException e) {
            System.out.println("✗ Error creating transaction: " + e.getMessage());
        }
        return false;
    }

    // READ
    public Transaction read(String transactionId) {
        String sql = "SELECT * FROM TRANSACTION WHERE TRANSACTION_ID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, transactionId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Transaction(
                    rs.getString("TRANSACTION_ID"),
                    rs.getString("TRANSACTION_TYPE"),
                    rs.getDouble("AMOUNT"),
                    rs.getDate("TRANSACTION_DATE").toLocalDate(),
                    rs.getString("ACCOUNT_NUMBER"),
                    rs.getString("STATUS")
                );
            }
        } catch (SQLException e) {
            System.out.println("✗ Error reading transaction: " + e.getMessage());
        }
        return null;
    }

    // READ ALL BY ACCOUNT
    public List<Transaction> readByAccount(String accountNumber) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM TRANSACTION WHERE ACCOUNT_NUMBER = ? ORDER BY TRANSACTION_DATE DESC";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Transaction txn = new Transaction(
                    rs.getString("TRANSACTION_ID"),
                    rs.getString("TRANSACTION_TYPE"),
                    rs.getDouble("AMOUNT"),
                    rs.getDate("TRANSACTION_DATE").toLocalDate(),
                    rs.getString("ACCOUNT_NUMBER"),
                    rs.getString("STATUS")
                );
                transactions.add(txn);
            }
        } catch (SQLException e) {
            System.out.println("✗ Error reading transactions: " + e.getMessage());
        }
        return transactions;
    }

    // UPDATE
    public boolean updateStatus(String transactionId, String newStatus) {
        String sql = "UPDATE TRANSACTION SET STATUS=? WHERE TRANSACTION_ID=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, newStatus);
            pstmt.setString(2, transactionId);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("✓ Transaction status updated");
                return true;
            }
        } catch (SQLException e) {
            System.out.println("✗ Error updating transaction: " + e.getMessage());
        }
        return false;
    }

    // DELETE
    public boolean delete(String transactionId) {
        String sql = "DELETE FROM TRANSACTION WHERE TRANSACTION_ID=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, transactionId);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("✓ Transaction deleted");
                return true;
            }
        } catch (SQLException e) {
            System.out.println("✗ Error deleting transaction: " + e.getMessage());
        }
        return false;
    }
}
