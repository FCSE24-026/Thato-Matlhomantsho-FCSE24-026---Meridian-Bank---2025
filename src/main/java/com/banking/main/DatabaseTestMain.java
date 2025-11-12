package com.banking.main;

import com.banking.model.*;
import com.banking.persistence.*;
import java.time.LocalDate;

public class DatabaseTestMain {
    public static void main(String[] args) {
        System.out.println("========== TASK 7: DATABASE TEST ==========\n");

        try {
            DatabaseManager dbManager = new DatabaseManager();
            
            System.out.println("--- Creating Customer ---");
            Customer cust1 = new Customer("CUST001", "John", "Doe", 
                                         "123 Main Street", "71234567", "john@example.com");
            cust1.setDateOfBirth(LocalDate.of(1990, 5, 15));
            dbManager.saveCustomer(cust1);
            
            System.out.println("\n--- Reading Customer ---");
            Customer retrieved = dbManager.getCustomer("CUST001");
            if (retrieved != null) {
                System.out.println("✓ Retrieved: " + retrieved.getFirstName() + " " + retrieved.getSurname());
            }
            
            System.out.println("\n--- Creating Account ---");
            Account savingsAcc = new SavingsAccount("SAV_001", cust1);
            savingsAcc.setBalance(1000.0);
            dbManager.saveAccount(savingsAcc);
            
            System.out.println("\n--- Reading Account ---");
            Account acc = dbManager.getAccount("SAV_001");
            if (acc != null) {
                System.out.println("✓ Retrieved account: " + acc.getAccountNumber() + 
                                 " | Balance: BWP " + String.format("%.2f", acc.getBalance()));
            }
            
            System.out.println("\n--- Creating Transaction ---");
            Transaction txn = new Transaction("TXN001", "DEPOSIT", 500.0, LocalDate.now(), 
                                            "SAV_001", "SUCCESS");
            dbManager.saveTransaction(txn);
            
            System.out.println("\n--- Reading Transaction ---");
            Transaction retrieved_txn = dbManager.getTransaction("TXN001");
            if (retrieved_txn != null) {
                System.out.println("✓ Retrieved transaction: " + retrieved_txn.getTransactionId() + 
                                 " | Amount: BWP " + String.format("%.2f", retrieved_txn.getAmount()));
            }
            
            System.out.println("\n========== TEST COMPLETED ==========");
            
        } catch (Exception e) {
            System.out.println("✗ ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}