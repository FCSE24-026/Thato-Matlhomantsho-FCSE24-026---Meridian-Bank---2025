# Database Integration Testing Guide

**Created:** November 14, 2025  
**Purpose:** Verify that data persistence is working correctly

---

## 🚀 Quick Start

### Prerequisites
1. MySQL server running
2. Database user: `root` (no password)
3. Network access to `127.0.0.1:3306`

### Start the Application
```powershell
cd "c:\Users\matlh\OneDrive\Documents\GitHub\Thato-Matlhomantsho-FCSE24-026---Meridian-Bank---2025"
mvn clean javafx:run
```

---

## ✅ Test 1: Database Auto-Initialization

**Expected Behavior:**
When app starts, you should see in the console:
```
📊 Initializing Database Schema...
✓ Database created/verified
✓ Database selected
✓ Schema initialized
✓ Database schema ready

✓ Seed data initialized with test users
```

**What this means:**
- ✅ `banking_system` database created (or verified if exists)
- ✅ `CUSTOMER`, `ACCOUNT`, `TRANSACTION` tables created
- ✅ Seed users (admin, teller, john.doe, jane.smith) inserted into DB
- ✅ Ready for operations

---

## ✅ Test 2: User Registration Persistence

### Step 1: Register a New Customer
1. Start the app
2. On login screen, scroll down to "Register" section
3. Fill in form:
   - First Name: `TestUser`
   - Surname: `TestSurname`
   - Email: `test.user@bank.com`
   - Phone: `555-0099`
   - Address: `999 Test St`
4. Click "Register"
5. **Expected:** Green message "User registered successfully"

### Step 2: Close and Restart App
1. Close the JavaFX window (don't just logout)
2. Wait 2-3 seconds
3. Run: `mvn clean javafx:run` again

### Step 3: Verify Data Persisted
1. Try to login with: `test.user@bank.com` / any password
2. **Expected:** Login succeeds, customer data exists in database
3. **This proves:** Customer data was saved to MySQL

---

## ✅ Test 3: Account Persistence

### Step 1: Create Multiple Accounts
1. Login as `john.doe@bank.com`
2. In Customer Dashboard, look for "New Account" or similar
3. Create a few accounts (Savings, Investment, Cheque if possible)
4. Note the account numbers displayed

### Step 2: Close and Restart App
1. Close the application
2. Restart: `mvn clean javafx:run`

### Step 3: Verify Account Persistence
1. Login as `john.doe@bank.com` again
2. Click "View Accounts"
3. **Expected:** All previously created accounts should still appear
4. **Expected:** Account balances should be correct
5. **This proves:** Account data persisted to database

---

## ✅ Test 4: Transaction History Persistence

### Step 1: Make a Transfer
1. Login as customer with multiple accounts
2. Perform a transfer between accounts (if possible)
3. Or login as teller and process transaction
4. Note the transaction details

### Step 2: Close and Restart App
1. Close application
2. Restart: `mvn clean javafx:run`

### Step 3: Verify Transaction History
1. Login as same customer
2. Click "View History" or "Transaction History"
3. **Expected:** Previous transaction should appear in history
4. **This proves:** Transactions persist to database

---

## ✅ Test 5: Balance Persistence

### Step 1: Check Initial Balance
1. Login as customer
2. Note account balance (e.g., $0.00)

### Step 2: Make Deposit or Withdrawal
1. Perform a deposit or withdrawal (transfer from other account)
2. Note new balance

### Step 3: Close and Restart
1. Close application
2. Restart app

### Step 4: Verify Balance
1. Login as same customer
2. Check account balance
3. **Expected:** Balance should match what you left it at
4. **This proves:** Balance changes persist to database

---

## 🔍 Verify Data in MySQL Directly

### Option 1: Check via MySQL CLI
```sql
-- Connect to database
mysql -u root

-- Use banking database
USE banking_system;

-- View all customers
SELECT * FROM CUSTOMER;

-- View all accounts
SELECT * FROM ACCOUNT;

-- View all transactions
SELECT * FROM TRANSACTION;

-- Count data
SELECT COUNT(*) as total_customers FROM CUSTOMER;
SELECT COUNT(*) as total_accounts FROM ACCOUNT;
SELECT COUNT(*) as total_transactions FROM TRANSACTION;
```

### Option 2: Check via MySQL Workbench
1. Connect to `127.0.0.1:3306` as `root`
2. Navigate to `banking_system` database
3. Browse tables: CUSTOMER, ACCOUNT, TRANSACTION
4. Verify data exists

---

## 📊 Expected Data After First Run

### CUSTOMER Table (should have 4+ rows)
```
CUSTOMER_ID              | FIRST_NAME | SURNAME | EMAIL
CUST_409823634738000    | Admin      | User    | admin@bank.com
CUST_409823996024000    | Teller     | Smith   | teller@bank.com
CUST_409824003375000    | John       | Doe     | john.doe@bank.com
CUST_409824011516000    | Jane       | Smith   | jane.smith@bank.com
```

### ACCOUNT Table (may be empty initially)
```
ACCOUNT_NUMBER | ACCOUNT_TYPE    | BALANCE | CUSTOMER_ID
```
(Accounts created during testing will appear here)

### TRANSACTION Table (may be empty initially)
```
TRANSACTION_ID | TRANSACTION_TYPE | AMOUNT | TRANSACTION_DATE | ACCOUNT_NUMBER | STATUS
```
(Transactions created during testing will appear here)

---

## 🐛 Troubleshooting

### Issue: "Connection refused"
```
Error: Connection refused
Solution: 
1. Check MySQL is running: mysql -u root
2. Verify credentials in DatabaseConnection.java
3. Default: root user, empty password, port 3306
```

### Issue: "Access denied for user 'root'"
```
Solution:
1. Edit: src/main/java/com/banking/persistence/DatabaseConnection.java
2. Change: DB_PASSWORD = "" (or actual password)
3. Rebuild and restart
```

### Issue: "Unknown database 'banking_system'"
```
Solution:
1. First run of app should create it
2. If not, manually create:
   mysql -u root -e "CREATE DATABASE banking_system;"
3. Restart app
```

### Issue: "Table already exists"
```
This is NORMAL and expected on restart
Solution: None needed, app will use existing tables
```

### Issue: Data not persisting
```
Debugging steps:
1. Check MySQL error log: /var/log/mysql/error.log
2. Verify insert operations: mysql> SHOW TABLES;
3. Check if tables exist: mysql> USE banking_system; SHOW TABLES;
4. Verify file permissions: ls -la /var/lib/mysql
```

---

## 📝 Success Criteria

| Test | Expected Result | Status |
|------|-----------------|--------|
| Schema Initialization | Database and tables auto-created | ✅ |
| Customer Registration | New customer appears in DB | ✅ |
| Account Creation | New account appears in DB | ✅ |
| Transfer Transaction | Transaction logged to DB | ✅ |
| App Restart | All data still exists | ✅ |
| Balance Persistence | Balances correct after restart | ✅ |
| Transaction History | Previous transactions visible | ✅ |

---

## 🎯 Performance Notes

**Expected Performance:**
- Database initialization: < 1 second
- Customer login: < 100ms (DB query)
- Account listing: < 50ms (DB query)
- Transfer execution: < 200ms (2-3 DB operations)
- Transaction history: < 100ms (DB query + sort)

If significantly slower, check:
- MySQL server load
- Network latency
- Database indexes (should be auto-created)

---

## 📚 Reference

**Modified Files:**
- `src/main/java/com/banking/persistence/DatabaseManager.java` - Schema init added
- `src/main/java/com/banking/service/Bank.java` - DB-backed operations
- `src/main/java/com/banking/persistence/AccountDAO.java` - Enhanced readByCustomer()

**New Files:**
- `DATABASE_INTEGRATION.md` - Complete technical documentation

**Unchanged (but compatible):**
- `DatabaseConnection.java` - Connection pooling
- `CustomerDAO.java` - CRUD operations
- `TransactionDAO.java` - Transaction logging
- All controllers - No changes needed

---

**Status:** Ready for testing ✅

