package com.banking.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String transactionId;
    private String transactionType;
    private double amount;
    private LocalDate date;
    private String accountNumber;
    private String status;
       private String approvalStatus;
       private Integer approverId;
       private LocalDate approvalDate;
       private String denialReason;
    
    public Transaction(String transactionId, String transactionType, double amount, 
                      LocalDate date, String accountNumber, String status) {
        this.transactionId = transactionId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.date = date;
        this.accountNumber = accountNumber;
        this.status = status;
           this.approvalStatus = "PENDING";
           this.approverId = null;
           this.approvalDate = null;
           this.denialReason = null;
    }
    
    public boolean processTransaction(double amount) {
        return amount > 0;
    }
    
    public boolean validateTransaction() {
        return transactionId != null && amount > 0 && date != null;
    }
    
    public String getTransactionDetails() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return String.format("[%s] Type: %s | Amount: BWP %.2f | Date: %s | Account: %s | Status: %s",
            transactionId, transactionType, amount, date.format(formatter), accountNumber, status);
    }
    
    // Getters and Setters
    public String getTransactionId() { return transactionId; }
    public String getTransactionType() { return transactionType; }
    public double getAmount() { return amount; }
    public LocalDate getDate() { return date; }
    public String getAccountNumber() { return accountNumber; }
    public String getStatus() { return status; }
       public String getApprovalStatus() { return approvalStatus; }
       public Integer getApproverId() { return approverId; }
       public LocalDate getApprovalDate() { return approvalDate; }
       public String getDenialReason() { return denialReason; }
    
    /**
     * Alias for getDate() - used by ModernBankingApp
     */
    public LocalDate getTransactionDate() {
        return this.date;
    }
    
    /**
     * Get transaction description - used by ModernBankingApp
     */
    public String getDescription() {
        return this.transactionType + ": " + this.transactionId;
    }
    
    /**
     * Get from account ID - placeholder implementation
     */
    public int getFromAccountId() {
        // Extract numeric ID from account number or return 0
        try {
            return Integer.parseInt(accountNumber.replaceAll("[^0-9]", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setDate(LocalDate date) { this.date = date; }
    public void setStatus(String status) { this.status = status; }
       public void setApprovalStatus(String approvalStatus) { this.approvalStatus = approvalStatus; }
       public void setApproverId(Integer approverId) { this.approverId = approverId; }
       public void setApprovalDate(LocalDate approvalDate) { this.approvalDate = approvalDate; }
       public void setDenialReason(String denialReason) { this.denialReason = denialReason; }
   
       /**
        * Approve transaction
        */
       public void approve(Integer adminId) {
           this.approvalStatus = "APPROVED";
           this.approverId = adminId;
           this.approvalDate = LocalDate.now();
           this.denialReason = null;
       }
   
       /**
        * Deny transaction with reason
        */
       public void deny(Integer adminId, String reason) {
           this.approvalStatus = "DENIED";
           this.approverId = adminId;
           this.approvalDate = LocalDate.now();
           this.denialReason = reason;
       }
// Removed extra closing brace
}
