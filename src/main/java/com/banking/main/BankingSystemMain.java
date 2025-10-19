package com.banking.main;

import com.banking.model.*;
import com.banking.service.*;
import java.time.LocalDate;

public class BankingSystemMain {
    public static void main(String[] args) {
        System.out.println("========== MERIDIAN BANKING SYSTEM - TASK 4 TEST ==========\n");
        
        // Create Bank
        Bank bank = new Bank("Meridian Bank");
        
        // Register Customers
        System.out.println("--- REGISTERING CUSTOMERS ---");
        Customer customer1 = new Customer("CUST001", "John", "Doe", 
                                         "123 Main Street", "71234567", "john@email.com");
        Customer customer2 = new Customer("CUST002", "Jane", "Smith", 
                                         "456 Oak Avenue", "72345678", "jane@email.com");
        
        bank.addCustomer(customer1);
        bank.addCustomer(customer2);
        
        // Open Accounts
        System.out.println("\n--- OPENING ACCOUNTS ---");
        Account savingsAcc = bank.openAccount(customer1, "savings");
        Account investmentAcc = bank.openAccount(customer1, "investment");
        Account chequeAcc = bank.openAccount(customer2, "cheque");
        
        // Set Cheque Account details
        if (chequeAcc instanceof ChequeAccount) {
            ChequeAccount chqAcc = (ChequeAccount) chequeAcc;
            chqAcc.setEmployer("Tech Corp");
            chqAcc.setCompanyAddress("789 Tech Park");
        }
        
        // Test Deposits
        System.out.println("\n--- TESTING DEPOSITS ---");
        savingsAcc.deposit(1000.0);
        investmentAcc.deposit(500.0);
        investmentAcc.deposit(1500.0);
        chequeAcc.deposit(5000.0);
        
        // Test Withdrawals
        System.out.println("\n--- TESTING WITHDRAWALS ---");
        savingsAcc.withdraw(500.0);  // Should fail
        investmentAcc.withdraw(500.0);  // Should succeed
        chequeAcc.withdraw(1000.0);  // Should succeed
        
        // Display Account Details
        System.out.println("\n--- ACCOUNT DETAILS ---");
        System.out.println(savingsAcc.getAccountDetails());
        System.out.println("\n" + investmentAcc.getAccountDetails());
        System.out.println("\n" + chequeAcc.getAccountDetails());
        
        // Display Customer Information
        customer1.displayCustomerInfo();
        customer2.displayCustomerInfo();
        
        // Test Interest
        System.out.println("\n--- PROCESSING MONTHLY INTEREST ---");
        bank.processMonthlyInterest();
        
        // Display Updated Balances
        System.out.println("\n--- UPDATED BALANCES ---");
        System.out.println("Savings Account: BWP " + String.format("%.2f", savingsAcc.getBalance()));
        System.out.println("Investment Account: BWP " + String.format("%.2f", investmentAcc.getBalance()));
        System.out.println("Cheque Account: BWP " + String.format("%.2f", chequeAcc.getBalance()));
        
        System.out.println("\n========== TEST COMPLETED SUCCESSFULLY ==========");
    }
}