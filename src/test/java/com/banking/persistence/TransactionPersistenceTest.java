package com.banking.persistence;

import com.banking.model.*;
import com.banking.service.Bank;
import com.banking.controller.TransactionController;
import com.banking.main.Role;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class TransactionPersistenceTest {

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
    public void depositPersistsTransactionAndReloadsAccount() throws Exception {
        // create a test customer
        Customer customer = new Customer("TEST_CUST_2", "Test", "User", "123 Test St", "000", "test2@example.com", Role.CUSTOMER);
        bank.addCustomer(customer);
        testCustomerId = customer.getCustomerId();

        // open a savings account
        Account account = bank.openAccount(customer, "savings");
        assertNotNull(account, "Account should be created");
        testAccountNumber = account.getAccountNumber();

        // perform deposit via controller (this persists transaction and updates account)
        TransactionController txController = new TransactionController(bank);
        boolean result = txController.processDeposit(account, 500.00);
        assertTrue(result, "Deposit should succeed");

        // reload account from DB to pick up persisted transactions
        Account reloaded = bank.getAccount(account.getAccountNumber());
        assertNotNull(reloaded, "Reloaded account should not be null");

        // check transaction rows via service
        List<Transaction> txns = bank.getTransactionHistory(account.getAccountNumber());
        assertNotNull(txns, "Transaction list should not be null");
        assertTrue(txns.stream().anyMatch(t -> t.getAmount() == 500.00 && t.getTransactionType().equals("DEPOSIT")), "There should be a DEPOSIT transaction of 500.00");

        // ensure reloaded account has transactions populated
        List<Transaction> accTxns = reloaded.getTransactions();
        assertNotNull(accTxns);
        assertTrue(accTxns.stream().anyMatch(t -> t.getAmount() == 500.00 && t.getTransactionType().equals("DEPOSIT")), "Reloaded account should contain the deposit transaction");
    }
}
