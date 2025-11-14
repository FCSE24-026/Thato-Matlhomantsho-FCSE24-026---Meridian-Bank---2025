package com.banking.main;

import com.banking.model.*;
import com.banking.service.Bank;
import com.banking.controller.*;
import com.banking.persistence.*;
import java.util.List;

/**
 * Verification harness to test seed data persistence and account creation
 * without running the full JavaFX UI.
 * 
 * Run this to:
 * 1. Initialize database and seed data
 * 2. Verify seed users are created
 * 3. Verify accounts are created for seed users
 * 4. Test account creation flow
 * 5. Inspect database state
 */
public class DataPersistenceTest {
    
    public static void main(String[] args) {
        System.out.println("=== Banking System Data Persistence Test ===\n");
        
        try {
            // Initialize the bank and database
            System.out.println("1. Initializing database and bank service...");
            Bank bank = new Bank("Meridian Bank");
            DatabaseManager dbManager = new DatabaseManager();
            System.out.println("   ✓ Database initialized\n");
            
            // Create seed users
            System.out.println("2. Creating seed users...");
            Customer john = new Customer("CUST_JOHN", "John", "Doe", "123 Main St", 
                                       "555-0001", "john.doe@bank.com", Role.CUSTOMER);
            Customer jane = new Customer("CUST_JANE", "Jane", "Smith", "456 Oak Ave", 
                                       "555-0002", "jane.smith@bank.com", Role.CUSTOMER);
            
            bank.addCustomer(john);
            bank.addCustomer(jane);
            System.out.println("   ✓ Seed users created\n");
            
            // Create accounts for seed users
            System.out.println("3. Creating accounts for seed users...");
            Account johnSavings = bank.openAccount(john, "savings");
            if (johnSavings != null) {
                johnSavings.deposit(1000.0);
                dbManager.updateAccount(johnSavings);
                System.out.println("   ✓ John's Savings account created with $1000.00 balance");
            }
            
            Account johnInvestment = bank.openAccount(john, "investment");
            if (johnInvestment != null) {
                johnInvestment.deposit(5000.0);
                dbManager.updateAccount(johnInvestment);
                System.out.println("   ✓ John's Investment account created with $5000.00 balance");
            }
            
            Account janeSavings = bank.openAccount(jane, "savings");
            if (janeSavings != null) {
                janeSavings.deposit(2000.0);
                dbManager.updateAccount(janeSavings);
                System.out.println("   ✓ Jane's Savings account created with $2000.00 balance");
            }
            System.out.println();
            
            // Verify accounts via database
            System.out.println("4. Verifying accounts loaded from database...");
            List<Account> johnAccounts = bank.getAllAccountsForCustomer(john.getCustomerId());
            List<Account> janeAccounts = bank.getAllAccountsForCustomer(jane.getCustomerId());
            
            System.out.println("   John's accounts (" + johnAccounts.size() + " total):");
            for (Account acc : johnAccounts) {
                System.out.println("     - " + acc.getAccountNumber() + " (" + acc.getAccountType() + "): $" + String.format("%.2f", acc.getBalance()));
            }
            
            System.out.println("   Jane's accounts (" + janeAccounts.size() + " total):");
            for (Account acc : janeAccounts) {
                System.out.println("     - " + acc.getAccountNumber() + " (" + acc.getAccountType() + "): $" + String.format("%.2f", acc.getBalance()));
            }
            System.out.println();
            
            // Test account creation via controller
            System.out.println("5. Testing account creation via controller...");
            AccountController accountController = new AccountController(bank);
            Customer testCustomer = new Customer("CUST_TEST", "Test", "User", "789 Test Ln", 
                                                "555-0003", "test@bank.com", Role.CUSTOMER);
            bank.addCustomer(testCustomer);
            
            Account chequeCreated = accountController.openChequeAccount(testCustomer, "TestCorp", "Downtown");
            System.out.println("   Cheque account creation: " + (chequeCreated != null ? "✓ SUCCESS" : "✗ FAILED"));
            
            List<Account> testAccounts = bank.getAllAccountsForCustomer(testCustomer.getCustomerId());
            System.out.println("   Test user's accounts: " + testAccounts.size() + " (expected: 1)");
            System.out.println();
            
            // Summary
            System.out.println("6. SUMMARY:");
            List<Customer> allCustomers = bank.getAllCustomers();
            System.out.println("   Total customers: " + allCustomers.size());
            List<Account> allAccounts = bank.getAllAccounts();
            System.out.println("   Total accounts: " + allAccounts.size());
            double totalBalance = allAccounts.stream().mapToDouble(Account::getBalance).sum();
            System.out.println("   Total balance across all accounts: $" + String.format("%.2f", totalBalance));
            System.out.println();
            
            System.out.println("=== Test Complete ===");
            System.exit(0);
            
        } catch (Exception e) {
            System.err.println("✗ Test failed with exception:");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
