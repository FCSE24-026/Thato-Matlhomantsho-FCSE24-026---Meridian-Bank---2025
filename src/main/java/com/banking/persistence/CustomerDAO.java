package com.banking.persistence;

import com.banking.model.Customer;
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
        String sql = "INSERT INTO CUSTOMER (CUSTOMER_ID, FIRST_NAME, SURNAME, ADDRESS, PHONE_NUMBER, EMAIL, DATE_OF_BIRTH) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, customer.getCustomerId());
            pstmt.setString(2, customer.getFirstName());
            pstmt.setString(3, customer.getSurname());
            pstmt.setString(4, customer.getAddress());
            pstmt.setString(5, customer.getPhoneNumber());
            pstmt.setString(6, customer.getEmail());
            pstmt.setDate(7, java.sql.Date.valueOf(customer.getDateOfBirth())); // fully qualified

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
                Customer customer = new Customer(
                    rs.getString("CUSTOMER_ID"),
                    rs.getString("FIRST_NAME"),
                    rs.getString("SURNAME"),
                    rs.getString("ADDRESS"),
                    rs.getString("PHONE_NUMBER"),
                    rs.getString("EMAIL")
                );
                customer.setDateOfBirth(rs.getDate("DATE_OF_BIRTH").toLocalDate()); // LocalDate
                return customer;
            }
        } catch (SQLException e) {
            System.out.println("✗ Error reading customer: " + e.getMessage());
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
                Customer customer = new Customer(
                    rs.getString("CUSTOMER_ID"),
                    rs.getString("FIRST_NAME"),
                    rs.getString("SURNAME"),
                    rs.getString("ADDRESS"),
                    rs.getString("PHONE_NUMBER"),
                    rs.getString("EMAIL")
                );
                customer.setDateOfBirth(rs.getDate("DATE_OF_BIRTH").toLocalDate());
                customers.add(customer);
            }
        } catch (SQLException e) {
            System.out.println("✗ Error reading all customers: " + e.getMessage());
        }
        return customers;
    }

    // UPDATE
    public boolean update(Customer customer) {
        String sql = "UPDATE CUSTOMER SET FIRST_NAME=?, SURNAME=?, ADDRESS=?, PHONE_NUMBER=?, EMAIL=?, DATE_OF_BIRTH=? " +
                     "WHERE CUSTOMER_ID=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, customer.getFirstName());
            pstmt.setString(2, customer.getSurname());
            pstmt.setString(3, customer.getAddress());
            pstmt.setString(4, customer.getPhoneNumber());
            pstmt.setString(5, customer.getEmail());
            pstmt.setDate(6, java.sql.Date.valueOf(customer.getDateOfBirth()));
            pstmt.setString(7, customer.getCustomerId());

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
