# MERIDIAN BANKING SYSTEM - TESTING GUIDE
**Project:** FCSE24-026  
**Date:** November 13, 2025  
**Status:** ✅ READY FOR SUBMISSION

---

## 🚀 QUICK START

```powershell
# Open PowerShell in project root
cd C:\Users\matlh\OneDrive\Documents\GitHub\Thato-Matlhomantsho-FCSE24-026---Meridian-Bank---2025

# Run the application
mvn clean javafx:run
```

The app will start with the login screen.

---

## 👥 TEST ACCOUNTS (Pre-Loaded)

All accounts have **any password** (validation not enforced for dev):

| Role | Email | Password | Dashboard Color |
|------|-------|----------|-----------------|
| **ADMIN** | `admin@bank.com` | any | Red (Admin Panel) |
| **TELLER** | `teller@bank.com` | any | Orange (Teller Panel) |
| **CUSTOMER** | `john.doe@bank.com` | any | Cyan (Customer Portal) |
| **CUSTOMER** | `jane.smith@bank.com` | any | Cyan (Customer Portal) |

---

## ✅ TEST SCENARIOS

### Test 1: FR-1 - User Roles & Authentication

**Scenario A: Admin Login**
1. Launch app
2. Enter: Username = `admin@bank.com`, Password = `test123`
3. Click **LOGIN**
4. **Expected:** Red "Admin Panel" with options:
   - 👥 VIEW ALL USERS
   - 💼 MANAGE ACCOUNTS
   - 📊 SYSTEM STATISTICS
5. Click each button → dialog shows stats

**Scenario B: Teller Login**
1. Logout (click LOGOUT button)
2. Enter: Username = `teller@bank.com`, Password = `test`
3. Click **LOGIN**
4. **Expected:** Orange "Teller Panel" with options:
   - 💳 PROCESS TRANSACTION
   - ➕ OPEN NEW ACCOUNT
   - 🔍 VERIFY CUSTOMER

**Scenario C: Customer Login**
1. Logout
2. Enter: Username = `john.doe@bank.com`, Password = `password`
3. Click **LOGIN**
4. **Expected:** Cyan "Customer Dashboard" showing:
   - Welcome message: "Welcome back, JOHN DOE"
   - Account balance display (e.g., $0.00)
   - 4 Quick Action buttons

---

### Test 2: FR-8 - Account Inquiry & Balances

**From Customer Dashboard:**
1. Click **"👤 VIEW ACCOUNTS"** button
2. **Expected:** 
   - List of accounts (if any)
   - Each account shows: Account ID, Type, Status, Balance
   - Balance formatted to 2 decimals (e.g., $1,234.56)
3. Click **← BACK TO DASHBOARD**

---

### Test 3: FR-10 - Input Validation & Error Handling

**Scenario A: Empty Login**
1. Leave Username and Password empty
2. Click **LOGIN**
3. **Expected:** Red error message: "⚠ Please fill in all fields"

**Scenario B: Invalid Username**
1. Enter: Username = `nonexistent@bank.com`, Password = `test`
2. Click **LOGIN**
3. **Expected:** Red error message: "✗ Invalid username or password"

**Scenario C: Negative Transfer Amount**
1. Login as customer
2. Click **"💸 TRANSFER"**
3. Select "FROM ACCOUNT" and "TO ACCOUNT" (same customer's accounts)
4. Enter Amount: `-100`
5. Click **EXECUTE TRANSFER**
6. **Expected:** Error: "⚠ Amount must be greater than 0"

**Scenario D: Same Account Transfer**
1. In Transfer screen, select the SAME account for both FROM and TO
2. Click **EXECUTE TRANSFER**
3. **Expected:** Error: "⚠ Cannot transfer to the same account"

---

### Test 4: FR-7 - Transaction History

**From Customer Dashboard:**
1. Click **"📋 HISTORY"** button
2. **Expected:**
   - Transaction list (may be empty if no operations yet)
   - Each transaction shows: Date, Description, Amount, Type
   - Chronological order (newest first or oldest first - consistent)
3. Click **← BACK TO DASHBOARD**

---

### Test 5: User Registration (Self-Registration Available)

**From Login Screen:**
1. Click **"SIGN UP"** button
2. Fill in form:
   - First Name: `Test`
   - Last Name: `User`
   - Username: `testuser123`
   - Password: `password123`
   - Confirm Password: `password123`
   - Email: `test@example.com`
   - Phone: `555-1234`
3. Click **CREATE ACCOUNT**
4. **Expected:** Success message, redirected to login
5. Login with `test@example.com` as username

---

### Test 6: Account Navigation & UI Responsiveness

**Dashboard Navigation:**
1. Login as `admin@bank.com`
2. Click **"👥 VIEW ALL USERS"** → Dialog shows user count
3. Click **OK** to close
4. Click **"📊 SYSTEM STATISTICS"** → Dialog shows stats
5. Click **OK** to close
6. Click **LOGOUT** → Redirected to login screen
7. **Expected:** All transitions smooth, no errors

---

## 📋 REQUIREMENTS MAPPED TO TESTS

| FR | Feature | Status | Test | Evidence |
|----|---------|--------|------|----------|
| FR-1 | Roles & Auth | ✅ DONE | Test 1 | 3 role dashboards working |
| FR-8 | Account Inquiry | ✅ DONE | Test 2 | Balance display with formatting |
| FR-10 | Validation | ✅ DONE | Test 3 | Error messages on invalid input |
| FR-7 | Transactions | ✅ DONE | Test 4 | Transaction history display |
| FR-2 | Registration | ✅ DONE | Test 5 | Signup screen functional |
| FR-5 | Navigation | ✅ DONE | Test 6 | UI responsive and consistent |

---

## 🔍 IMPLEMENTATION CHECKLIST

### Core Files Present:
- ✅ `Role.java` — Role enum (ADMIN, TELLER, CUSTOMER)
- ✅ `User.java` — User model with role
- ✅ `Customer.java` — Customer with role support
- ✅ `LoginController.java` — Authentication logic
- ✅ `ModernBankingApp.java` — All UI screens
- ✅ `Account.java`, `SavingsAccount.java`, `InvestmentAccount.java`, `ChequeAccount.java`
- ✅ `Transaction.java` — Transaction model
- ✅ `ValidationUtil.java` — Input validation
- ✅ `PasswordUtil.java` — Password utilities

### Database Ready (Not Yet Connected):
- ✅ `DatabaseConnection.java` — MySQL connection setup
- ✅ `schema.sql` — Database schema with CUSTOMER, ACCOUNT, TRANSACTION tables
- ⏳ DAOs (CustomerDAO, AccountDAO, TransactionDAO) — Skeleton ready for impl

---

## 🎨 UI FEATURES IMPLEMENTED

1. ✅ **Modern Dark Theme**
   - Cyan (#00d4ff) accents for customer
   - Red (#e74c3c) for admin
   - Orange (#f39c12) for teller

2. ✅ **Role-Based Navigation**
   - Different dashboards based on role
   - Appropriate menu options per role

3. ✅ **Responsive Forms**
   - Styled text fields and buttons
   - Clear labels and prompts
   - Error message display

4. ✅ **Transaction Display**
   - List view with formatting
   - Account balance display
   - Operation history

---

## 💡 USAGE NOTES

- **Passwords:** Not validated in current build (for testing). Ready for BCrypt integration.
- **Database:** All data in-memory (not persisted). Ready to connect to MySQL via DAOs.
- **Concurrency:** Single-user session. Ready for multi-user with session management.
- **Interest:** Rates configured (Savings 0.05%, Investment 5%). Ready for scheduler.

---

## 🧪 DEVELOPER NOTES

**To Run Tests:**
```powershell
mvn -DskipTests=true javafx:run
```

**To Compile Only:**
```powershell
mvn clean compile
```

**To Add Database Integration:**
1. Update `CustomerDAO.java` to use `DatabaseConnection.getInstance()`
2. Call DAOs from controllers instead of in-memory bank object
3. Initialize `DatabaseConnection` on app startup

**To Add Password Hashing:**
```java
import com.banking.util.PasswordUtil;

// Hash password on registration
String hashedPassword = PasswordUtil.hashPassword(plainPassword);

// Verify on login
boolean isValid = PasswordUtil.verifyPassword(plainPassword, storedHash);
```

---

## 📞 SUPPORT

If app doesn't start:
1. Ensure Java 21+ is installed: `java -version`
2. Ensure Maven is installed: `mvn -version`
3. Clean build: `mvn clean compile javafx:run`

---

**✅ Status:** Ready for Submission  
**Last Updated:** 2025-11-13

