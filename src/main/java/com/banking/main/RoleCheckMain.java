package com.banking.main;

import com.banking.service.Bank;
import com.banking.model.Customer;

public class RoleCheckMain {
    public static void main(String[] args) {
        Bank bank = new Bank("RoleCheck");
        Customer admin = bank.getCustomerByEmail("admin@bank.com");
        if (admin == null) {
            System.out.println("No admin@bank.com found in DB");
        } else {
            System.out.println("Admin email found: " + admin.getEmail() + " role=" + (admin.getRole() != null ? admin.getRole().name() : "NULL") );
        }
    }
}
