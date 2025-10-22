package com.banking.view;

import com.banking.controller.*;
import com.banking.model.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.*;


public class TransactionView extends VBox {
    private TransactionController transactionController;
    private ComboBox<String> transactionTypeCombo;
    private TextField amountField;
    private Button submitButton;
    private Label messageLabel;
    
    public TransactionView(TransactionController transactionController) {
        this.transactionController = transactionController;
        initializeUI();
    }
    
    private void initializeUI() {
        this.setStyle("-fx-padding: 20; -fx-spacing: 10; -fx-font-size: 12;");
        
        Label titleLabel = new Label("Perform Transaction");
        titleLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");
        
        Label typeLabel = new Label("Transaction Type:");
        transactionTypeCombo = new ComboBox<>();
        transactionTypeCombo.getItems().addAll("Deposit", "Withdrawal");
        transactionTypeCombo.setPrefWidth(200);
        
        Label amountLabel = new Label("Amount (BWP):");
        amountField = new TextField();
        amountField.setPromptText("Enter amount");
        amountField.setPrefWidth(200);
        
        submitButton = new Button("Submit Transaction");
        submitButton.setStyle("-fx-font-size: 12;");
        
        messageLabel = new Label();
        messageLabel.setStyle("-fx-font-size: 12;");
        
        HBox typeBox = new HBox(10);
        typeBox.getChildren().addAll(typeLabel, transactionTypeCombo);
        
        HBox amountBox = new HBox(10);
        amountBox.getChildren().addAll(amountLabel, amountField);
        
        this.getChildren().addAll(
            titleLabel,
            new Separator(),
            typeBox,
            amountBox,
            submitButton,
            messageLabel
        );
    }
    
    public void processTransaction(Account account) {
        if (account == null) {
            messageLabel.setText("No account selected");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }
        
        String type = transactionTypeCombo.getValue();
        if (type == null) {
            messageLabel.setText("Select transaction type");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }
        
        try {
            double amount = Double.parseDouble(amountField.getText());
            
            boolean success = false;
            if ("Deposit".equals(type)) {
                success = transactionController.processDeposit(account, amount);
            } else if ("Withdrawal".equals(type)) {
                success = transactionController.processWithdrawal(account, amount);
            }
            
            if (success) {
                messageLabel.setText("Transaction successful! New balance: " + 
                                   transactionController.getFormattedBalance(account));
                messageLabel.setStyle("-fx-text-fill: green;");
                amountField.clear();
            } else {
                messageLabel.setText("Transaction failed!");
                messageLabel.setStyle("-fx-text-fill: red;");
            }
        } catch (NumberFormatException e) {
            messageLabel.setText("Invalid amount");
            messageLabel.setStyle("-fx-text-fill: red;");
        }
    }
    
    public Button getSubmitButton() {
        return submitButton;
    }
}