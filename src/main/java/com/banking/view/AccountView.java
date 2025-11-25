package com.banking.view;

import com.banking.controller.*;
import com.banking.model.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.*;

public class AccountView extends VBox {
    private AccountController accountController;
    private TextArea accountDetailsArea;
    private Button refreshButton;
    
    public AccountView(AccountController accountController) {
        this.accountController = accountController;
        initializeUI();
    }
    
    private void initializeUI() {
        this.setStyle("-fx-padding: 20; -fx-spacing: 10;");
        
        Label titleLabel = new Label("Account Management");
        titleLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");
        
        accountDetailsArea = new TextArea();
        accountDetailsArea.setEditable(false);
        accountDetailsArea.setPrefRowCount(10);
        accountDetailsArea.setWrapText(true);
        
        refreshButton = new Button("Refresh Accounts");
        refreshButton.setStyle("-fx-font-size: 12;");
        
        this.getChildren().addAll(
            titleLabel,
            new Separator(),
            accountDetailsArea,
            refreshButton
        );
    }
    
    public void displayCustomerAccounts(Customer customer) {
        if (customer == null) {
            accountDetailsArea.setText("No customer selected");
            return;
        }
        
        StringBuilder text = new StringBuilder();
        text.append("Customer: ").append(customer.getFirstName()).append(" ")
            .append(customer.getSurname()).append("\n\n");
        
        List<Account> accounts = customer.getAccounts();
        if (accounts.isEmpty()) {
            text.append("No accounts found");
        } else {
            for (Account acc : accounts) {
                text.append(accountController.getAccountDetailsForDisplay(acc))
                    .append("\n\n");
            }
        }
        
        accountDetailsArea.setText(text.toString());
    }
    
    public Button getRefreshButton() {
        return refreshButton;
    }
}