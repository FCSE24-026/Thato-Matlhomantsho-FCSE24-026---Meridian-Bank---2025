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

public class ApplicationView extends Application {
    private ApplicationController appController;
    private TabPane tabPane;
    private LoginView loginView;
    private CustomerRegistrationView registrationView;
    private AccountView accountView;
    private TransactionView transactionView;
    
    public ApplicationView() {
    }
    
    public void setAppController(ApplicationController controller) {
        this.appController = controller;
    }
    
    @Override
    public void start(Stage stage) {
        // Create controllers
        appController = new ApplicationController(new com.banking.service.Bank("Meridian Bank"));
        
        // Create views
        loginView = new LoginView(appController.getLoginController());
        registrationView = new CustomerRegistrationView(appController.getCustomerController());
        accountView = new AccountView(appController.getAccountController());
        transactionView = new TransactionView(appController.getTransactionController());
        
        // Setup login success callback
        loginView.setOnLoginSuccess(this::onLoginSuccess);
        
        // Setup transaction button
        transactionView.getSubmitButton().setOnAction(e -> {
            if (appController.getLoginController().isLoggedIn()) {
                Customer customer = appController.getLoginController().getCurrentCustomer();
                if (!customer.getAccounts().isEmpty()) {
                    transactionView.processTransaction(customer.getAccounts().get(0));
                    accountView.displayCustomerAccounts(customer);
                }
            }
        });
        
        // Setup refresh button
        accountView.getRefreshButton().setOnAction(e -> {
            if (appController.getLoginController().isLoggedIn()) {
                Customer customer = appController.getLoginController().getCurrentCustomer();
                accountView.displayCustomerAccounts(customer);
            }
        });
        
        // Create tabs
        tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        
        Tab loginTab = new Tab("Login", loginView);
        Tab registerTab = new Tab("Register", registrationView);
        Tab accountsTab = new Tab("My Accounts", accountView);
        Tab transactionTab = new Tab("Transactions", transactionView);
        
        tabPane.getTabs().addAll(loginTab, registerTab, accountsTab, transactionTab);
        
        // Create scene
        Scene scene = new Scene(tabPane, 800, 600);
        // Load CSS stylesheet
        String cssResource = getClass().getResource("/banking-style.css").toExternalForm();
        scene.getStylesheets().add(cssResource);
        
        stage.setTitle("MERIDIAN BANK - Professional Banking System");
        stage.setScene(scene);
        stage.show();
    }
    
    private void onLoginSuccess() {
        if (appController.getLoginController().isLoggedIn()) {
            Customer customer = appController.getLoginController().getCurrentCustomer();
            accountView.displayCustomerAccounts(customer);
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}