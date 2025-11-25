package com.banking.view;

import com.banking.controller.*;
import com.banking.model.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class CustomerRegistrationView extends VBox {
    private CustomerController customerController;
    private TextField firstNameField, surnameField, addressField, phoneField, emailField;
    private Button registerButton;
    private Label messageLabel;
    
    public CustomerRegistrationView(CustomerController customerController) {
        this.customerController = customerController;
        initializeUI();
    }
    
    private void initializeUI() {
        this.setStyle("-fx-padding: 20; -fx-spacing: 10; -fx-font-size: 12;");
        
        Label titleLabel = new Label("Customer Registration");
        titleLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");
        
        firstNameField = createTextField("First Name");
        surnameField = createTextField("Surname");
        addressField = createTextField("Address");
        phoneField = createTextField("Phone Number");
        emailField = createTextField("Email");
        
        registerButton = new Button("Register Customer");
        registerButton.setStyle("-fx-font-size: 12;");
        registerButton.setOnAction(e -> handleRegistration());
        
        messageLabel = new Label();
        messageLabel.setStyle("-fx-font-size: 12;");
        
        this.getChildren().addAll(
            titleLabel,
            new Separator(),
            firstNameField,
            surnameField,
            addressField,
            phoneField,
            emailField,
            registerButton,
            messageLabel
        );
    }
    
    private TextField createTextField(String promptText) {
        TextField field = new TextField();
        field.setPromptText(promptText);
        field.setPrefWidth(300);
        return field;
    }
    
    private void handleRegistration() {
        String firstName = firstNameField.getText();
        String surname = surnameField.getText();
        String address = addressField.getText();
        String phone = phoneField.getText();
        String email = emailField.getText();
        
        Customer customer = customerController.registerCustomer(
            firstName, surname, address, phone, email
        );
        
        if (customer != null) {
            messageLabel.setText("Customer registered successfully! ID: " + customer.getCustomerId());
            messageLabel.setStyle("-fx-text-fill: green;");
            clearFields();
        } else {
            messageLabel.setText("Registration failed. Please check all fields.");
            messageLabel.setStyle("-fx-text-fill: red;");
        }
    }
    
    private void clearFields() {
        firstNameField.clear();
        surnameField.clear();
        addressField.clear();
        phoneField.clear();
        emailField.clear();
    }
}
