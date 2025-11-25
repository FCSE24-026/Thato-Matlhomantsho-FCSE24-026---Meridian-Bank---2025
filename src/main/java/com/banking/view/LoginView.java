package com.banking.view;

import com.banking.controller.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Pos;

// ============================================================================
// 1. LOGIN VIEW - JavaFX Login Window
// ============================================================================
public class LoginView extends VBox {
    private LoginController loginController;
    private TextField customerIdField;
    private Button loginButton;
    private Label messageLabel;
    private Runnable onLoginSuccess;
    
    public LoginView(LoginController loginController) {
        this.loginController = loginController;
        initializeUI();
    }
    
    private void initializeUI() {
        this.setStyle("-fx-padding: 20; -fx-spacing: 15; -fx-font-size: 12;");
        this.setAlignment(Pos.CENTER);
        
        Label titleLabel = new Label("Meridian Banking System - Login");
        titleLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");
        
        Label idLabel = new Label("Customer ID:");
        customerIdField = new TextField();
        customerIdField.setPromptText("Enter your customer ID");
        customerIdField.setPrefWidth(300);
        
        loginButton = new Button("Login");
        loginButton.setPrefWidth(100);
        loginButton.setStyle("-fx-font-size: 14;");
        loginButton.setOnAction(e -> handleLogin());
        
        messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12;");
        
        this.getChildren().addAll(
            titleLabel,
            new Separator(),
            idLabel,
            customerIdField,
            loginButton,
            messageLabel
        );
    }
    
    private void handleLogin() {
        String customerId = customerIdField.getText();
        
        if (customerId.isEmpty()) {
            messageLabel.setText("Please enter a customer ID");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }
        
        boolean success = loginController.authenticateCustomer(customerId);
        
        if (success) {
            messageLabel.setText("Login successful!");
            messageLabel.setStyle("-fx-text-fill: green;");
            if (onLoginSuccess != null) {
                onLoginSuccess.run();
            }
        } else {
            messageLabel.setText("Login failed. Invalid customer ID.");
            messageLabel.setStyle("-fx-text-fill: red;");
        }
    }
    
    public void setOnLoginSuccess(Runnable callback) {
        this.onLoginSuccess = callback;
    }
    
    public void clearFields() {
        customerIdField.clear();
        messageLabel.setText("");
    }
}