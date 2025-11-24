package com.banking.persistence;

import com.banking.model.Customer;
import com.banking.model.Account;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

public class CustomerDAO {
    private Connection connection;

    public CustomerDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    // CREATE
    public boolean create(Customer customer) {
        String sql = "INSERT INTO CUSTOMER (CUSTOMER_ID, FIRST_NAME, SURNAME, ADDRESS, PHONE_NUMBER, EMAIL, PASSWORD_HASH, ROLE, APPROVED, SUSPENDED, DATE_OF_BIRTH) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, customer.getCustomerId());
            pstmt.setString(2, customer.getFirstName());
            pstmt.setString(3, customer.getSurname());
            pstmt.setString(4, customer.getAddress());
            pstmt.setString(5, customer.getPhoneNumber());
            pstmt.setString(6, customer.getEmail());
            pstmt.setString(7, customer.getPasswordHash());
            pstmt.setString(8, customer.getRole() != null ? customer.getRole().name() : "CUSTOMER");
            pstmt.setInt(9, customer.isApproved() ? 1 : 0);
            pstmt.setInt(10, customer.isSuspended() ? 1 : 0);
            pstmt.setDate(11, java.sql.Date.valueOf(customer.getDateOfBirth())); // fully qualified

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("✓ Customer created: " + customer.getCustomerId());
                return true;
            }
        } catch (SQLException e) {
            System.out.println("✗ Error creating customer: " + e.getMessage());
        }
        return false;
    }

    // READ
    public Customer read(String customerId) {
        String sql = "SELECT * FROM CUSTOMER WHERE CUSTOMER_ID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, customerId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String roleStr = null;
                try { roleStr = rs.getString("ROLE"); } catch (Exception ex) { roleStr = "CUSTOMER"; }
                com.banking.main.Role role = com.banking.main.Role.CUSTOMER;
                if (roleStr != null) {
                    try {
                        role = com.banking.main.Role.valueOf(roleStr);
                    } catch (Exception ex) {
                        // Try matching by display name (tolerant parsing)
                        for (com.banking.main.Role r : com.banking.main.Role.values()) {
                            if (r.getDisplayName().equalsIgnoreCase(roleStr) || r.name().equalsIgnoreCase(roleStr)) {
                                role = r;
                                break;
                            }
                        }
                    }
                }

                boolean approved = true;
                try { approved = rs.getInt("APPROVED") == 1; } catch (Exception ex) { approved = true; }

                String passwordHash = "";
                try { passwordHash = rs.getString("PASSWORD_HASH"); } catch (Exception ex) { passwordHash = ""; }

                Customer customer = new Customer(
                    rs.getString("CUSTOMER_ID"),
                    rs.getString("FIRST_NAME"),
                    rs.getString("SURNAME"),
                    rs.getString("ADDRESS"),
                    rs.getString("PHONE_NUMBER"),
                    rs.getString("EMAIL"),
                    passwordHash,
                    role
                );
                customer.setDateOfBirth(rs.getDate("DATE_OF_BIRTH").toLocalDate()); // LocalDate
                customer.setApproved(approved);

                // load accounts for this customer
                try {
                    List<Account> accounts = new AccountDAO().readByCustomer(customerId);
                    for (Account a : accounts) customer.addAccount(a);
                } catch (Exception ex) {
                    // ignore account loading errors
                }

                return customer;
            }
        } catch (SQLException e) {
            System.out.println("✗ Error reading customer: " + e.getMessage());
        }
        return null;
    }

    // READ shallow (no accounts) - helper to avoid recursive loads
    public Customer readShallow(String customerId) {
        String sql = "SELECT * FROM CUSTOMER WHERE CUSTOMER_ID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, customerId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String roleStr = null;
                try { roleStr = rs.getString("ROLE"); } catch (Exception ex) { roleStr = "CUSTOMER"; }
                com.banking.main.Role role = com.banking.main.Role.CUSTOMER;
                if (roleStr != null) {
                    try {
                        role = com.banking.main.Role.valueOf(roleStr);
                    } catch (Exception ex) {
                        for (com.banking.main.Role r : com.banking.main.Role.values()) {
                            if (r.getDisplayName().equalsIgnoreCase(roleStr) || r.name().equalsIgnoreCase(roleStr)) {
                                role = r;
                                break;
                            }
                        }
                    }
                }

                boolean approved = true;
                try { approved = rs.getInt("APPROVED") == 1; } catch (Exception ex) { approved = true; }

                String passwordHash = "";
                try { passwordHash = rs.getString("PASSWORD_HASH"); } catch (Exception ex) { passwordHash = ""; }

                Customer customer = new Customer(
                    rs.getString("CUSTOMER_ID"),
                    rs.getString("FIRST_NAME"),
                    rs.getString("SURNAME"),
                    rs.getString("ADDRESS"),
                    rs.getString("PHONE_NUMBER"),
                    rs.getString("EMAIL"),
                    passwordHash,
                    role
                );
                try { customer.setDateOfBirth(rs.getDate("DATE_OF_BIRTH").toLocalDate()); } catch (Exception ex) {}
                customer.setApproved(approved);

                return customer;
            }
        } catch (SQLException e) {
            System.out.println("✗ Error reading customer (shallow): " + e.getMessage());
        }
        return null;
    }

    // READ ALL
    public List<Customer> readAll() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM CUSTOMER";
        try (var stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String roleStr = null;
                try { roleStr = rs.getString("ROLE"); } catch (Exception ex) { roleStr = "CUSTOMER"; }
                com.banking.main.Role role = com.banking.main.Role.CUSTOMER;
                if (roleStr != null) {
                    try {
                        role = com.banking.main.Role.valueOf(roleStr);
                    } catch (Exception ex) {
                        for (com.banking.main.Role r : com.banking.main.Role.values()) {
                            if (r.getDisplayName().equalsIgnoreCase(roleStr) || r.name().equalsIgnoreCase(roleStr)) {
                                role = r;
                                break;
                            }
                        }
                    }
                }

                boolean approved = true;
                try { approved = rs.getInt("APPROVED") == 1; } catch (Exception ex) { approved = true; }

                String passwordHash = "";
                try { passwordHash = rs.getString("PASSWORD_HASH"); } catch (Exception ex) { passwordHash = ""; }

                Customer customer = new Customer(
                    rs.getString("CUSTOMER_ID"),
                    rs.getString("FIRST_NAME"),
                    rs.getString("SURNAME"),
                    rs.getString("ADDRESS"),
                    rs.getString("PHONE_NUMBER"),
                    rs.getString("EMAIL"),
                    passwordHash,
                    role
                );
                try { customer.setDateOfBirth(rs.getDate("DATE_OF_BIRTH").toLocalDate()); } catch (Exception ex) {}
                customer.setApproved(approved);

                // attempt to load accounts
                try {
                    List<Account> accounts = new AccountDAO().readByCustomer(customer.getCustomerId());
                    for (Account a : accounts) customer.addAccount(a);
                } catch (Exception ex) {}

                customers.add(customer);
            }
        } catch (SQLException e) {
            System.out.println("✗ Error reading all customers: " + e.getMessage());
        }
        return customers;
    }

    // UPDATE
    public boolean update(Customer customer) {
        String sql = "UPDATE CUSTOMER SET FIRST_NAME=?, SURNAME=?, ADDRESS=?, PHONE_NUMBER=?, EMAIL=?, PASSWORD_HASH=?, DATE_OF_BIRTH=?, ROLE=?, APPROVED=?, SUSPENDED=? " +
                     "WHERE CUSTOMER_ID=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, customer.getFirstName());
            pstmt.setString(2, customer.getSurname());
            pstmt.setString(3, customer.getAddress());
            pstmt.setString(4, customer.getPhoneNumber());
            pstmt.setString(5, customer.getEmail());
            pstmt.setString(6, customer.getPasswordHash());
            pstmt.setDate(7, java.sql.Date.valueOf(customer.getDateOfBirth()));
            pstmt.setString(8, customer.getRole() != null ? customer.getRole().name() : "CUSTOMER");
            pstmt.setInt(9, customer.isApproved() ? 1 : 0);
            pstmt.setInt(10, customer.isSuspended() ? 1 : 0);
            pstmt.setString(11, customer.getCustomerId());

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("✓ Customer updated: " + customer.getCustomerId());
                return true;
            }
        } catch (SQLException e) {
            System.out.println("✗ Error updating customer: " + e.getMessage());
        }
        return false;
    }

    // DELETE
    public boolean delete(String customerId) {
        String sql = "DELETE FROM CUSTOMER WHERE CUSTOMER_ID=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, customerId);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("✓ Customer deleted: " + customerId);
                return true;
            }
        } catch (SQLException e) {
            System.out.println("✗ Error deleting customer: " + e.getMessage());
        }
        return false;
    }
}
