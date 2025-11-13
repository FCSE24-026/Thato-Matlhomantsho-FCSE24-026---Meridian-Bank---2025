# Functional Requirements Coverage Report
**Project:** Meridian Banking System (FCSE24-026)  
**Date:** 2025-11-13  
**Status:** In Progress

---

## Executive Summary
- **Total FRs:** 10 + NFRs
- **Implemented:** 3/10 (30%)
- **Partially Implemented:** 4/10 (40%)
- **Not Started:** 3/10 (30%)
- **Critical Priority:** FR-1 (In Progress), FR-10 (Partial)

---

## Detailed Requirements Mapping

### ✅ FR-1: User Roles Authentication (CRITICAL)
**Requirement:** Support 3 roles (Admin/Teller, Customer), secure login, role-based menus, secure password storage.

**Status:** 🟡 PARTIALLY IMPLEMENTED (60% complete)

**What's Done:**
- [x] Three roles defined: `ADMIN`, `TELLER`, `CUSTOMER` (in `Role.java`)
- [x] Role-based authentication in `LoginController.authenticateUser()`
- [x] Role-specific dashboards:
  - Admin Dashboard (red theme, system stats)
  - Teller Dashboard (orange theme, transaction processing)
  - Customer Dashboard (cyan theme, accounts/transfers)
- [x] UI shows role in header

**What's Missing:**
- [ ] Password hashing (BCrypt or similar) — currently no password validation
- [ ] Secure password storage in database
- [ ] Session management
- [ ] Password reset/recovery
- [ ] Login audit trail

**Next Steps:**
1. Integrate BCrypt for password hashing
2. Store hashed passwords in `CUSTOMER` table
3. Validate password on login
4. Add login attempt logging

**Files Involved:**
- `Role.java` ✓
- `User.java` ✓
- `LoginController.java` (need password hashing)
- `DatabaseConnection.java` ✓

---

### ❌ FR-2: Customer Registration & Management (HIGH)
**Requirement:** Admin/Teller registers customers (NOT self-registration), captures full name/address/national ID/employment, auto-assigns Customer ID, validates duplicates.

**Status:** 🔴 NOT IMPLEMENTED (0% complete)

**What's Done:**
- [x] Basic `Customer` model with name, address, phone, email
- [x] Auto Customer ID generation (in `LoginController.registerUser()`)

**What's Missing:**
- [ ] National ID field in `Customer` model
- [ ] Employment details (employer, employer address) — partially in `Account` model
- [ ] Admin/Teller-only registration UI (not customer self-registration)
- [ ] Duplicate email validation ✓ (exists but needs UI)
- [ ] Deactivate/update customer records
- [ ] Admin customer management screen

**Next Steps:**
1. Add `nationalId` and employment fields to `Customer` model
2. Create admin-only "Register Customer" screen (not accessible to customers)
3. Add customer list/edit/deactivate screen for admin
4. Integrate with database persistence

**Files to Create/Modify:**
- `Customer.java` (add fields)
- `AdminController.java` (new)
- `AdminCustomerRegistrationView.java` (new)
- `CustomerDAO.java` (implement)

---

### 🟡 FR-3: Account Management (HIGH)
**Requirement:** 3 account types (Savings/Investment/Cheque), admin opens accounts, prevent duplicates, each account belongs to one customer.

**Status:** 🟡 PARTIALLY IMPLEMENTED (50% complete)

**What's Done:**
- [x] Three account classes: `SavingsAccount`, `InvestmentAccount`, `ChequeAccount`
- [x] Account interest rates configured:
  - Savings: 0.05% monthly
  - Investment: 5% monthly
  - Cheque: 0% (no interest)
- [x] `Account` model has customer reference
- [x] Unique account number generation

**What's Missing:**
- [ ] Investment Account: minimum BWP 500 deposit validation
- [ ] Cheque Account: employment details required for creation
- [ ] Admin UI to create/close accounts
- [ ] Account closure logic (only if balance = 0)
- [ ] Customer-side "open account" request UI (not direct creation)
- [ ] Prevent duplicate account numbers ✓ (logic exists, needs testing)

**Next Steps:**
1. Add minimum balance validation to `InvestmentAccount` constructor
2. Create admin "Open Account" screen
3. Add account closure logic
4. Implement customer "Request Account" feature

**Files to Modify:**
- `InvestmentAccount.java` (add minimum deposit)
- `ChequeAccount.java` (employer validation)
- `AccountController.java` (add create/close methods)
- `AdminAccountManagementView.java` (new)

---

### ❌ FR-4: Deposit Operations (HIGH)
**Requirement:** Customer/Admin deposits, update balance, log transaction, reject negative.

**Status:** 🔴 NOT IMPLEMENTED (0% complete)

**What's Done:**
- [x] `Account.deposit()` method exists in model
- [x] `Transaction` model exists
- [x] Balance update logic in account

**What's Missing:**
- [ ] Deposit UI (customer/admin screens)
- [ ] Negative/zero amount validation in UI
- [ ] Transaction record creation after deposit
- [ ] Confirmation message display
- [ ] Deposit persistence to database

**Next Steps:**
1. Create "Deposit" screen (accessible to customer & teller)
2. Add validation for positive amounts
3. Call `Account.deposit()` → create `Transaction` record
4. Display confirmation with transaction ID

**Files to Create/Modify:**
- `TransactionController.java` (new or extend)
- `DepositView.java` (new)
- `TransactionDAO.java` (implement)

---

### ❌ FR-5: Withdrawal Operations (MEDIUM)
**Requirement:** Withdrawals only from Investment/Cheque, prevent overdraft, log transaction with timestamp.

**Status:** 🔴 NOT IMPLEMENTED (0% complete)

**What's Done:**
- [x] `Account.withdraw()` method exists
- [x] Account types support withdrawal logic

**What's Missing:**
- [ ] Withdraw UI (customer/teller screens)
- [ ] Overdraft prevention (check balance before allowing)
- [ ] Savings Account: prevent withdrawal entirely
- [ ] Transaction logging with timestamp
- [ ] Error messages for insufficient funds

**Next Steps:**
1. Create "Withdraw" screen (Investment/Cheque only)
2. Validate account type before allowing withdrawal
3. Check balance >= withdrawal amount
4. Log transaction with `LocalDateTime.now()`
5. Update balance atomically

**Files to Create/Modify:**
- `WithdrawalView.java` (new)
- `Account.withdraw()` (add overdraft check)
- `TransactionController.java`

---

### ❌ FR-6: Interest Calculation (MEDIUM)
**Requirement:** Auto calculate monthly interest (Savings 0.05%, Investment 5%), record as transaction, no duplication, complete in <1 second.

**Status:** 🔴 NOT IMPLEMENTED (0% complete)

**What's Done:**
- [x] Interest rates configured in account classes
- [x] `Account.payInterest()` method exists
- [x] `Bank.processMonthlyInterest()` loop exists

**What's Missing:**
- [ ] Scheduled task/scheduler to run monthly (cron job or timer)
- [ ] Transaction record creation for interest (currently just updates balance)
- [ ] Prevent duplicate interest on same day
- [ ] Track last interest payment date
- [ ] Database persistence of interest transactions

**Next Steps:**
1. Add `lastInterestDate` field to `Account` model
2. Create scheduler (e.g., `Timer` or Spring `@Scheduled`)
3. Modify `payInterest()` to create `Transaction` record
4. Run at month-end or via admin trigger
5. Log interest application to database

**Files to Modify:**
- `Account.java` (add lastInterestDate)
- `InterestScheduler.java` (new - implement scheduler)
- `Bank.java` (enhance processMonthlyInterest)
- `TransactionDAO.java`

---

### 🟡 FR-7: Transaction Management (MEDIUM)
**Requirement:** All ops create transaction, admin sees all, customer sees own, immutable records.

**Status:** 🟡 PARTIALLY IMPLEMENTED (40% complete)

**What's Done:**
- [x] `Transaction` model created with deposit/withdrawal types
- [x] Transaction fields: ID, type, amount, date, account, status
- [x] `TransactionDAO` skeleton exists

**What's Missing:**
- [ ] Transaction creation on every operation (deposit/withdraw/interest)
- [ ] Role-based transaction visibility:
  - Admin: see all transactions
  - Customer: see only their account transactions
- [ ] Immutability enforcement (read-only after creation)
- [ ] Transaction history UI for customer
- [ ] Admin transaction report view
- [ ] Database persistence

**Next Steps:**
1. Modify deposit/withdraw/interest methods to create `Transaction`
2. Add transaction visibility filter in `TransactionController`
3. Create "Transaction History" screen (customer + admin versions)
4. Implement `TransactionDAO.findByCustomer()`, `findAll()`

**Files to Modify:**
- `TransactionController.java` (role-based filtering)
- `TransactionDAO.java` (implement)
- `TransactionHistoryView.java` (new - already partially exists)

---

### 🟡 FR-8: Account Inquiry & Statements (MEDIUM)
**Requirement:** View balances, mini/full statements, chronological order, 2 decimal formatting.

**Status:** 🟡 PARTIALLY IMPLEMENTED (50% complete)

**What's Done:**
- [x] `AccountView.java` displays account list with balances
- [x] Balance formatted to 2 decimals (in `ModernBankingApp`)
- [x] `TransactionView` exists

**What's Missing:**
- [ ] Mini-statement (last N transactions)
- [ ] Full statement (date range selection)
- [ ] Chronological ordering (ensure transactions are sorted by date)
- [ ] Statement generation (PDF or printable format)
- [ ] Download/export statement option
- [ ] Account-specific transaction filtering

**Next Steps:**
1. Add date range picker to transaction history
2. Filter transactions by date range
3. Sort by date (DESC)
4. Generate downloadable statement (CSV/PDF)

**Files to Modify:**
- `TransactionHistoryView.java` (add date range)
- `AccountController.java` (add statement generation)

---

### ❌ FR-9: Audit Logging & Reporting (LOW)
**Requirement:** Log all activities (user ID, action, timestamp, account), admin reports, read-only.

**Status:** 🔴 NOT IMPLEMENTED (0% complete)

**What's Done:**
- [x] Console logging via `System.out.println()` (not secure)

**What's Missing:**
- [ ] Audit table in database
- [ ] Structured audit logging (user/action/timestamp/resource)
- [ ] Admin audit log viewer
- [ ] Admin report generation
- [ ] Read-only enforcement (append-only)

**Next Steps:**
1. Create `AuditLog` model
2. Create `AUDIT_LOG` table in schema
3. Log all admin actions + transactions
4. Create admin audit report screen

**Files to Create:**
- `AuditLog.java` (model)
- `AuditLogDAO.java` (persistence)
- `AuditLogView.java` (admin report screen)

---

### 🟡 FR-10: Exception Handling & Validation (HIGH)
**Requirement:** Validate inputs (amounts, IDs, types), graceful errors, descriptive messages, don't alter balance on failure.

**Status:** 🟡 PARTIALLY IMPLEMENTED (60% complete)

**What's Done:**
- [x] `ValidationUtil.java` exists with basic checks
- [x] Error messages displayed in UI (red text)
- [x] Try-catch blocks in controllers
- [x] Amount validation (non-negative)

**What's Missing:**
- [ ] Comprehensive input validation utility (expand `ValidationUtil`)
- [ ] Transaction atomicity (ensure balance updates only on success)
- [ ] Descriptive error messages for all scenarios
- [ ] Input type validation (numeric fields)
- [ ] Email validation
- [ ] Phone number format validation

**Next Steps:**
1. Expand `ValidationUtil` with more validators
2. Add pre-condition checks before any state change
3. Ensure database transactions are atomic
4. Add user-friendly error codes

**Files to Modify:**
- `ValidationUtil.java` (expand)
- `AccountController.java`, `TransactionController.java` (add validation calls)

---

## Non-Functional Requirements (NFR) Coverage

| NFR | Requirement | Status | Notes |
|-----|-------------|--------|-------|
| NFR2 | Clear class responsibilities | 🟢 DONE | Separate controller/model/view/persistence |
| NFR3 | JavaDoc & inline comments | 🟡 PARTIAL | Comments exist, need more JavaDoc |
| NFR4 | Validation, exception handling | 🟡 PARTIAL | Exists but needs expansion |
| NFR5 | Intuitive UI, role-specific menus | 🟢 DONE | Role-based dashboards implemented |
| NFR6 | Performance <2 sec | 🟢 LIKELY | Need profiling, no complex queries yet |
| NFR7 | Prevent negative balance, duplicates | 🟡 PARTIAL | Logic exists, needs enforcement |
| NFR8 | Java conventions, maintainable | 🟢 DONE | Follows camelCase, clear naming |

---

## Implementation Roadmap (Prioritized)

### Phase 1: CRITICAL (FR-1 Enhancement + Core Ops)
- [ ] Add password hashing (BCrypt) to FR-1
- [ ] Implement FR-4 (Deposit) - highest value
- [ ] Implement FR-5 (Withdrawal)
- [ ] Persist transactions to database

### Phase 2: HIGH (Admin Functions)
- [ ] Implement FR-2 (Admin customer registration)
- [ ] Implement FR-3 (Admin account management)
- [ ] Implement FR-7 (Transaction visibility)
- [ ] Add admin management screens

### Phase 3: MEDIUM (Auto Features)
- [ ] Implement FR-6 (Scheduled interest)
- [ ] Implement FR-8 (Statements)
- [ ] Enhance FR-10 (validation)

### Phase 4: LOW (Audit)
- [ ] Implement FR-9 (Audit logging)

---

## Database Schema Additions Needed

Current tables: `CUSTOMER`, `ACCOUNT`, `TRANSACTION`

**New/Modified:**
```sql
-- Add to CUSTOMER table
ALTER TABLE CUSTOMER ADD COLUMN NATIONAL_ID VARCHAR(20) NOT NULL;
ALTER TABLE CUSTOMER ADD COLUMN STATUS ENUM('ACTIVE', 'INACTIVE') DEFAULT 'ACTIVE';

-- New AUDIT_LOG table
CREATE TABLE AUDIT_LOG (
    LOG_ID VARCHAR(50) PRIMARY KEY,
    USER_ID VARCHAR(50),
    ACTION VARCHAR(100),
    RESOURCE_TYPE VARCHAR(50),
    RESOURCE_ID VARCHAR(50),
    TIMESTAMP DATETIME DEFAULT CURRENT_TIMESTAMP,
    DETAILS TEXT,
    INDEX idx_user_timestamp (USER_ID, TIMESTAMP)
);
```

---

## Testing Checklist

- [ ] FR-1: Login as Admin/Teller/Customer, verify role-specific dashboard
- [ ] FR-2: Admin registers customer, verify unique ID assigned
- [ ] FR-3: Admin creates Savings/Investment/Cheque account
- [ ] FR-4: Customer deposits, balance updates, transaction recorded
- [ ] FR-5: Customer withdraws from Investment, prevented from Savings
- [ ] FR-6: Monthly interest calculates, no duplicate entries
- [ ] FR-7: Admin sees all transactions, customer sees own only
- [ ] FR-8: View statement for date range
- [ ] FR-9: Admin views audit log
- [ ] FR-10: Negative deposit rejected, error message shown

---

## Files Summary

**Implemented:**
- `Role.java` ✓
- `User.java` ✓
- `Customer.java` (partial)
- `Account.java`, `SavingsAccount.java`, `InvestmentAccount.java`, `ChequeAccount.java` ✓
- `Transaction.java` ✓
- `LoginController.java` ✓
- `ModernBankingApp.java` (UI) ✓

**Skeleton Only:**
- `DatabaseConnection.java` ✓
- `TransactionDAO.java`
- `CustomerDAO.java`
- `AccountDAO.java`

**To Create:**
- `AdminCustomerRegistrationView.java`
- `AdminAccountManagementView.java`
- `DepositView.java`
- `WithdrawalView.java`
- `InterestScheduler.java`
- `AuditLog.java`
- `AuditLogDAO.java`
- `AuditLogView.java`

---

## Conclusion

Your app has a strong foundation (30-40% of core functionality). The next priorities are:
1. **Database persistence** — connect DAO classes
2. **Deposit/Withdrawal UI** — most critical user flows
3. **Admin screens** — customer management
4. **Password hashing** — security
5. **Scheduled interest** — automated feature

Expected completion time (estimated): **2-3 weeks** with focused development.

