package com.banking.persistence;

import com.banking.model.*;
import com.banking.service.Bank;
import com.banking.controller.AccountController;
import com.banking.main.Role;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class InvestmentAccountInterestTest {

    private Bank bank;
    private String testCustomerId;
    private String testAccountNumber;

    @BeforeEach
    public void setUp() {
        bank = new Bank("Test Bank");
    }

    @AfterEach
    public void tearDown() {
        // Clean up test data to avoid leaving rows in the database
        if (testAccountNumber != null) {
            try {
                bank.deleteAccount(testAccountNumber);
                System.out.println("✓ Test cleanup: Account " + testAccountNumber + " deleted");
            } catch (Exception e) {
                System.out.println("⚠ Cleanup warning: Could not delete account " + testAccountNumber);
            }
        }
        if (testCustomerId != null) {
            try {
                bank.deleteCustomer(testCustomerId);
                System.out.println("✓ Test cleanup: Customer " + testCustomerId + " deleted");
            } catch (Exception e) {
                System.out.println("⚠ Cleanup warning: Could not delete customer " + testCustomerId);
            }
        }
    }

    @Test
    public void investmentAccountEarnsCorrectInterestRate() throws Exception {
        // create a test customer
        Customer customer = new Customer("TEST_INV_CUST", "Investment", "Tester", "456 Investment Ave", "111-2222", "investor@example.com", Role.CUSTOMER);
        bank.addCustomer(customer);
        testCustomerId = customer.getCustomerId();

        // open an investment account with initial deposit of BWP 1000
        AccountController accountController = new AccountController(bank);
        Account account = accountController.openInvestmentAccount(customer, 1000.00);
        assertNotNull(account, "Investment account should be created");
        testAccountNumber = account.getAccountNumber();
        assertTrue(account instanceof InvestmentAccount, "Account should be InvestmentAccount");

        // verify initial balance
        Account loaded1 = bank.getAccount(account.getAccountNumber());
        assertEquals(1000.00, loaded1.getBalance(), 0.01, "Initial balance should be BWP 1000");

        // cast to InvestmentAccount to access interest rate
        InvestmentAccount investmentAcc = (InvestmentAccount) loaded1;
        assertEquals(0.05, investmentAcc.getInterestRate(), "Interest rate should be 5% (0.05)");

        // pay interest (updates in-memory account and transaction list)
        investmentAcc.payInterest();

        // persist the updated balance and all transactions (including the interest transaction)
        bank.updateAccount(investmentAcc);
        
        // also explicitly record the interest transaction that was just created
        List<Transaction> pendingTxns = investmentAcc.getTransactions();
        if (!pendingTxns.isEmpty()) {
            Transaction lastTxn = pendingTxns.get(pendingTxns.size() - 1);
            if ("INTEREST".equals(lastTxn.getTransactionType())) {
                bank.recordTransaction(lastTxn);
            }
        }

        // reload account and verify new balance = 1000 + (1000 * 0.05) = 1050
        Account loaded2 = bank.getAccount(account.getAccountNumber());
        double expectedBalance = 1000.00 * (1 + 0.05);
        assertEquals(expectedBalance, loaded2.getBalance(), 0.01, "Balance after interest should be BWP 1050");

        // verify interest transaction was created and persisted
        List<Transaction> txns = bank.getTransactionHistory(account.getAccountNumber());
        assertTrue(txns.stream().anyMatch(t -> "INTEREST".equals(t.getTransactionType())), "Should have INTEREST transaction");
        
        Transaction interestTxn = txns.stream()
            .filter(t -> "INTEREST".equals(t.getTransactionType()))
            .findFirst()
            .orElse(null);
        assertNotNull(interestTxn, "Interest transaction should exist");
        assertEquals(50.00, interestTxn.getAmount(), 0.01, "Interest amount should be BWP 50 (5% of 1000)");

        System.out.println("✓ Investment account interest test passed: 5% rate correctly applied");
    }

    @Test
    public void investmentAccountWithMultipleInterestPayments() throws Exception {
        // create a test customer
        Customer customer = new Customer("TEST_INV_MULTI", "MultiInterest", "Tester", "789 Investment Blvd", "333-4444", "multi@example.com", Role.CUSTOMER);
        bank.addCustomer(customer);
        testCustomerId = customer.getCustomerId();

        // open an investment account with initial deposit of BWP 2000
        AccountController accountController = new AccountController(bank);
        Account account = accountController.openInvestmentAccount(customer, 2000.00);
        assertNotNull(account, "Investment account should be created");
        testAccountNumber = account.getAccountNumber();

        // reload and cast
        InvestmentAccount investmentAcc = (InvestmentAccount) bank.getAccount(account.getAccountNumber());
        assertEquals(2000.00, investmentAcc.getBalance(), 0.01, "Initial balance should be BWP 2000");

        // First interest payment: 2000 * 0.05 = 100 → new balance = 2100
        investmentAcc.payInterest();
        bank.updateAccount(investmentAcc);
        // persist the interest transaction
        List<Transaction> txns1 = investmentAcc.getTransactions();
        if (!txns1.isEmpty()) {
            Transaction lastTxn = txns1.get(txns1.size() - 1);
            if ("INTEREST".equals(lastTxn.getTransactionType())) {
                bank.recordTransaction(lastTxn);
            }
        }
        
        InvestmentAccount reloaded1 = (InvestmentAccount) bank.getAccount(account.getAccountNumber());
        assertEquals(2100.00, reloaded1.getBalance(), 0.01, "After 1st interest: BWP 2100");

        // Second interest payment: 2100 * 0.05 = 105 → new balance = 2205
        reloaded1.payInterest();
        bank.updateAccount(reloaded1);
        // persist the interest transaction
        List<Transaction> txns2 = reloaded1.getTransactions();
        if (!txns2.isEmpty()) {
            Transaction lastTxn = txns2.get(txns2.size() - 1);
            if ("INTEREST".equals(lastTxn.getTransactionType())) {
                bank.recordTransaction(lastTxn);
            }
        }
        
        InvestmentAccount reloaded2 = (InvestmentAccount) bank.getAccount(account.getAccountNumber());
        assertEquals(2205.00, reloaded2.getBalance(), 0.01, "After 2nd interest: BWP 2205 (compounding)");

        // verify both interest transactions exist
        List<Transaction> allTxns = bank.getTransactionHistory(account.getAccountNumber());
        long interestCount = allTxns.stream().filter(t -> "INTEREST".equals(t.getTransactionType())).count();
        assertEquals(2, interestCount, "Should have 2 INTEREST transactions");

        System.out.println("✓ Investment account multiple interest payments test passed");
    }
}
