package com.banking.main;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import com.banking.controller.LoginController;
import com.banking.controller.AccountController;
import com.banking.model.Account;
import com.banking.model.Transaction;
import com.banking.service.Bank;


import java.util.List;
import java.time.format.DateTimeFormatter;

public class ModernBankingApp extends Application {
    private Stage primaryStage;
    private LoginController authController;
    private AccountController accountController;
    private User currentUser;
    private Bank bank;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        this.bank = new Bank("Meridian Bank");
        this.authController = new LoginController(bank);
        this.accountController = new AccountController(bank);
        showLoginScreen();
    }

    private void showLoginScreen() {
        VBox mainContainer = new VBox(20);
        mainContainer.setPadding(new Insets(60, 100, 60, 100));
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setStyle("-fx-background-color: #0a0e27;");

        Label titleLabel = new Label("Meridian Bank");
        titleLabel.setStyle("-fx-font-size: 48; -fx-font-weight: bold; -fx-text-fill: #00d4ff;");
        
        Label subtitleLabel = new Label("Banking System");
        subtitleLabel.setStyle("-fx-font-size: 16; -fx-text-fill: #888; -fx-letter-spacing: 2;");

        Separator sep1 = new Separator();
        sep1.setStyle("-fx-border-color: rgba(0,212,255,0.3); -fx-border-width: 1;");

        VBox formBox = new VBox(15);
        formBox.setStyle("-fx-background-color: #0f1433; -fx-border-color: #00d4ff; " +
                "-fx-border-width: 1; -fx-border-radius: 8; -fx-padding: 40; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,212,255,0.15), 10, 0, 0, 2);");

        Label formTitleLabel = new Label("Secure Access");
        formTitleLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #00d4ff;");

        Label usernameLabel = new Label("USERNAME");
        usernameLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #888; -fx-letter-spacing: 1;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter your username");
        usernameField.setStyle("-fx-padding: 12; -fx-font-size: 12; -fx-background-color: #151a35; " +
                "-fx-text-fill: #e0e0e0; -fx-control-inner-background: #151a35; -fx-border-color: #00d4ff; " +
                "-fx-border-width: 2; -fx-border-radius: 6;");
        usernameField.setPrefWidth(300);

        Label passwordLabel = new Label("PASSWORD");
        passwordLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #888; -fx-letter-spacing: 1;");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setStyle("-fx-padding: 12; -fx-font-size: 12; -fx-background-color: #151a35; " +
                "-fx-text-fill: #e0e0e0; -fx-control-inner-background: #151a35; -fx-border-color: #00d4ff; " +
                "-fx-border-width: 2; -fx-border-radius: 6;");
        passwordField.setPrefWidth(300);

        Label messageLabel = new Label();
        messageLabel.setWrapText(true);
        messageLabel.setPrefWidth(300);
        messageLabel.setStyle("-fx-font-size: 11; -fx-padding: 10; -fx-background-color: rgba(0,0,0,0.3); -fx-border-radius: 4;");

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        Button loginButton = new Button("LOGIN");
        loginButton.setPrefWidth(140);
        loginButton.setPrefHeight(45);
        loginButton.setStyle("-fx-padding: 0; -fx-font-size: 13; -fx-font-weight: bold; " +
                "-fx-background-color: linear-gradient(to right, #00d4ff, #0099cc); " +
                "-fx-text-fill: white; -fx-border-radius: 6; -fx-cursor: hand;");

        Button registerButton = new Button("SIGN UP");
        registerButton.setPrefWidth(140);
        registerButton.setPrefHeight(45);
        registerButton.setStyle("-fx-padding: 0; -fx-font-size: 13; -fx-font-weight: bold; " +
                "-fx-background-color: #1a1f3a; -fx-text-fill: #00d4ff; " +
                "-fx-border-color: #00d4ff; -fx-border-width: 2; -fx-border-radius: 6; -fx-cursor: hand;");

        loginButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                messageLabel.setText("⚠ Please fill in all fields");
                messageLabel.setStyle("-fx-text-fill: #f39c12; -fx-font-size: 11; -fx-padding: 10; " +
                        "-fx-background-color: rgba(243,156,18,0.1); -fx-border-radius: 4;");
            } else if (authController.authenticateUser(username, password)) {
                currentUser = authController.getUserByUsername(username);
                if (currentUser != null) {
                    showDashboard();
                } else {
                    messageLabel.setText("✗ Error retrieving user information");
                    messageLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 11; -fx-padding: 10; " +
                            "-fx-background-color: rgba(231,76,60,0.1); -fx-border-radius: 4;");
                }
            } else {
                messageLabel.setText("✗ Invalid username or password");
                messageLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 11; -fx-padding: 10; " +
                        "-fx-background-color: rgba(231,76,60,0.1); -fx-border-radius: 4;");
                passwordField.clear();
            }
        });

        registerButton.setOnAction(e -> showRegisterScreen());

        buttonBox.getChildren().addAll(loginButton, registerButton);

        formBox.getChildren().addAll(
            formTitleLabel, new Separator(),
            usernameLabel, usernameField,
            passwordLabel, passwordField,
            messageLabel, buttonBox
        );

        mainContainer.getChildren().addAll(titleLabel, subtitleLabel, sep1, formBox);

        Scene scene = new Scene(mainContainer, 700, 700);
        primaryStage.setTitle("FLECA - Banking System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showRegisterScreen() {
        VBox mainContainer = new VBox(15);
        mainContainer.setPadding(new Insets(40));
        mainContainer.setStyle("-fx-background-color: #0a0e27;");

        Label titleLabel = new Label("CREATE ACCOUNT");
        titleLabel.setStyle("-fx-font-size: 28; -fx-font-weight: bold; -fx-text-fill: #00d4ff;");

        VBox formBox = new VBox(12);
        formBox.setStyle("-fx-background-color: #0f1433; -fx-border-color: #00d4ff; " +
                "-fx-border-width: 1; -fx-border-radius: 8; -fx-padding: 30; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,212,255,0.15), 10, 0, 0, 2);");

        TextField usernameField = createStyledTextField("Choose username");
        PasswordField passwordField = createStyledPasswordField("Enter password");
        PasswordField confirmField = createStyledPasswordField("Confirm password");
        TextField emailField = createStyledTextField("Enter email address");
        TextField phoneField = createStyledTextField("Enter phone number");

        Label messageLabel = new Label();
        messageLabel.setWrapText(true);
        messageLabel.setStyle("-fx-font-size: 11; -fx-padding: 10; -fx-background-color: rgba(0,0,0,0.3); -fx-border-radius: 4;");

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        Button signupButton = new Button("CREATE ACCOUNT");
        signupButton.setPrefWidth(150);
        signupButton.setPrefHeight(40);
        signupButton.setStyle("-fx-background-color: linear-gradient(to right, #1abc9c, #16a085); " +
                "-fx-text-fill: white; -fx-font-weight: bold; -fx-border-radius: 6; -fx-cursor: hand;");

        Button backButton = new Button("BACK");
        backButton.setPrefWidth(150);
        backButton.setPrefHeight(40);
        backButton.setStyle("-fx-background-color: #1a1f3a; -fx-text-fill: #00d4ff; " +
                "-fx-border-color: #00d4ff; -fx-border-width: 2; -fx-border-radius: 6; -fx-cursor: hand;");

        signupButton.setOnAction(e -> {
            if (authController.registerUser(
                    usernameField.getText().trim(),
                    passwordField.getText(),
                    confirmField.getText(),
                    emailField.getText().trim(),
                    phoneField.getText().trim())) {
                messageLabel.setText("✓ Registration successful! Returning to login...");
                messageLabel.setStyle("-fx-text-fill: #1abc9c; -fx-font-size: 11; -fx-padding: 10; " +
                        "-fx-background-color: rgba(26,188,156,0.1); -fx-border-radius: 4;");
                try {
                    Thread.sleep(1500);
                    showLoginScreen();
                } catch (InterruptedException ex) {
                    showLoginScreen();
                }
            } else {
                messageLabel.setText("✗ Registration failed. Check your input or duplicate username.");
                messageLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 11; -fx-padding: 10; " +
                        "-fx-background-color: rgba(231,76,60,0.1); -fx-border-radius: 4;");
            }
        });

        backButton.setOnAction(e -> showLoginScreen());

        buttonBox.getChildren().addAll(signupButton, backButton);

        formBox.getChildren().addAll(
            createStyledLabel("USERNAME"),
            usernameField,
            createStyledLabel("PASSWORD"),
            passwordField,
            createStyledLabel("CONFIRM PASSWORD"),
            confirmField,
            createStyledLabel("EMAIL"),
            emailField,
            createStyledLabel("PHONE"),
            phoneField,
            messageLabel,
            buttonBox
        );

        mainContainer.getChildren().addAll(titleLabel, new Separator(), formBox);

        ScrollPane scrollPane = new ScrollPane(mainContainer);
        scrollPane.setStyle("-fx-background-color: #0a0e27; -fx-control-inner-background: #0a0e27;");
        scrollPane.setFitToWidth(true);

        Scene scene = new Scene(scrollPane, 700, 750);
        primaryStage.setTitle("FLECA - Register");
        primaryStage.setScene(scene);
    }

    private void showDashboard() {
        VBox mainContainer = new VBox(15);
        mainContainer.setPadding(new Insets(0));
        mainContainer.setStyle("-fx-background-color: #0a0e27;");

        HBox headerBox = new HBox(20);
        headerBox.setPadding(new Insets(25));
        headerBox.setStyle("-fx-background-color: linear-gradient(to right, #0f1433, #151a35); " +
                "-fx-border-color: #00d4ff; -fx-border-width: 0 0 2 0;");
        headerBox.setAlignment(Pos.CENTER_LEFT);

        VBox headerInfoBox = new VBox(5);
        Label welcomeLabel = new Label("Welcome back, " + currentUser.getUsername().toUpperCase());
        welcomeLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: #00d4ff;");
        
        Label balanceLabel = new Label("ACCOUNT BALANCE");
        balanceLabel.setStyle("-fx-font-size: 10; -fx-text-fill: #888; -fx-letter-spacing: 1;");

        double totalBalance = accountController.getTotalBalance(currentUser.getUserId());
        Label amountLabel = new Label("$" + String.format("%.2f", totalBalance));
        amountLabel.setStyle("-fx-font-size: 32; -fx-font-weight: bold; -fx-text-fill: #1abc9c;");

        headerInfoBox.getChildren().addAll(welcomeLabel, balanceLabel, amountLabel);

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button logoutButton = new Button("LOGOUT");
        logoutButton.setPrefWidth(120);
        logoutButton.setPrefHeight(40);
        logoutButton.setStyle("-fx-background-color: linear-gradient(to right, #e74c3c, #c0392b); " +
                "-fx-text-fill: white; -fx-font-weight: bold; -fx-border-radius: 6; -fx-cursor: hand;");
        logoutButton.setOnAction(e -> {
            currentUser = null;
            showLoginScreen();
        });

        headerBox.getChildren().addAll(headerInfoBox, spacer, logoutButton);

        VBox actionsPanel = new VBox(15);
        actionsPanel.setPadding(new Insets(30));
        actionsPanel.setStyle("-fx-background-color: #0a0e27;");

        Label actionsTitle = new Label("QUICK ACTIONS");
        actionsTitle.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #00d4ff; -fx-letter-spacing: 2;");

        HBox actionButtonsRow = new HBox(15);
        actionButtonsRow.setAlignment(Pos.CENTER_LEFT);

        Button[] actionButtons = new Button[4];
        String[] buttonLabels = {"👤 VIEW ACCOUNTS", "💸 TRANSFER", "📋 HISTORY", "✚ NEW ACCOUNT"};
        String[] buttonStyles = {
            "linear-gradient(to right, #3498db, #2980b9)",
            "linear-gradient(to right, #9b59b6, #8e44ad)",
            "linear-gradient(to right, #f39c12, #d68910)",
            "linear-gradient(to right, #1abc9c, #16a085)"
        };

        for (int i = 0; i < 4; i++) {
            actionButtons[i] = new Button(buttonLabels[i]);
            actionButtons[i].setPrefWidth(160);
            actionButtons[i].setPrefHeight(50);
            actionButtons[i].setStyle("-fx-background-color: " + buttonStyles[i] + "; " +
                    "-fx-text-fill: white; -fx-font-weight: bold; -fx-border-radius: 6; -fx-cursor: hand; -fx-font-size: 11;");
        }

        actionButtons[0].setOnAction(e -> showAccountsScreen());
        actionButtons[1].setOnAction(e -> showTransferScreen());
        actionButtons[2].setOnAction(e -> showTransactionHistory());
        actionButtons[3].setOnAction(e -> showCreateAccountDialog());

        actionButtonsRow.getChildren().addAll(actionButtons);

        actionsPanel.getChildren().addAll(actionsTitle, actionButtonsRow);

        mainContainer.getChildren().addAll(headerBox, actionsPanel);

        Scene scene = new Scene(mainContainer, 900, 400);
        primaryStage.setTitle("FLECA - Dashboard");
        primaryStage.setScene(scene);
    }

    private void showAccountsScreen() {
        VBox mainContainer = new VBox(15);
        mainContainer.setPadding(new Insets(30));
        mainContainer.setStyle("-fx-background-color: #0a0e27;");

        Label titleLabel = new Label("MY ACCOUNTS");
        titleLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: #00d4ff; -fx-letter-spacing: 1;");

        VBox accountsBox = new VBox(15);
        List<Account> accounts = accountController.getUserAccounts(currentUser.getUserId());

        if (accounts.isEmpty()) {
            Label emptyLabel = new Label("✗ No accounts found. Create one!");
            emptyLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #888;");
            accountsBox.getChildren().add(emptyLabel);
        } else {
            for (Account account : accounts) {
                HBox cardBox = new HBox(20);
                cardBox.setPadding(new Insets(20));
                cardBox.setStyle("-fx-background-color: #0f1433; -fx-border-color: #00d4ff; " +
                        "-fx-border-width: 2; -fx-border-radius: 8; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,212,255,0.15), 8, 0, 0, 2);");

                VBox cardInfo = new VBox(8);
                Label idLabel = new Label("ACC ID: " + account.getAccountId());
                idLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #888; -fx-letter-spacing: 1;");

                Label typeLabel = new Label(account.getAccountType().toUpperCase());
                typeLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #e0e0e0;");

                Label statusLabel = new Label(account.isActive() ? "✓ ACTIVE" : "✗ INACTIVE");
                statusLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #1abc9c;");

                cardInfo.getChildren().addAll(idLabel, typeLabel, statusLabel);

                HBox spacer2 = new HBox();
                HBox.setHgrow(spacer2, Priority.ALWAYS);

                Label balanceLabel = new Label("$" + String.format("%.2f", account.getBalance()));
                balanceLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: #1abc9c;");

                cardBox.getChildren().addAll(cardInfo, spacer2, balanceLabel);
                accountsBox.getChildren().add(cardBox);
            }
        }

        ScrollPane scrollPane = new ScrollPane(accountsBox);
        scrollPane.setStyle("-fx-background-color: #0a0e27; -fx-control-inner-background: #0a0e27;");
        scrollPane.setFitToWidth(true);

        Button backButton = new Button("← BACK TO DASHBOARD");
        backButton.setPrefWidth(180);
        backButton.setPrefHeight(40);
        backButton.setStyle("-fx-background-color: #1a1f3a; -fx-text-fill: #00d4ff; " +
                "-fx-border-color: #00d4ff; -fx-border-width: 2; -fx-border-radius: 6; -fx-cursor: hand;");
        backButton.setOnAction(e -> showDashboard());

        VBox bottomBox = new VBox(10);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(20));
        bottomBox.getChildren().add(backButton);

        mainContainer.getChildren().addAll(titleLabel, scrollPane, bottomBox);

        Scene scene = new Scene(mainContainer, 800, 650);
        primaryStage.setTitle("FLECA - My Accounts");
        primaryStage.setScene(scene);
    }

    private void showTransferScreen() {
        VBox mainContainer = new VBox(20);
        mainContainer.setPadding(new Insets(40));
        mainContainer.setStyle("-fx-background-color: #0a0e27;");

        Label titleLabel = new Label("FUND TRANSFER");
        titleLabel.setStyle("-fx-font-size: 28; -fx-font-weight: bold; -fx-text-fill: #00d4ff;");

        VBox formBox = new VBox(15);
        formBox.setStyle("-fx-background-color: #0f1433; -fx-border-color: #00d4ff; " +
                "-fx-border-width: 1; -fx-border-radius: 8; -fx-padding: 30; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,212,255,0.15), 10, 0, 0, 2);");

        List<Account> accounts = accountController.getUserAccounts(currentUser.getUserId());

        ComboBox<Account> fromCombo = new ComboBox<>();
        ComboBox<Account> toCombo = new ComboBox<>();
        fromCombo.getItems().addAll(accounts);
        toCombo.getItems().addAll(accounts);
        fromCombo.setPrefWidth(300);
        toCombo.setPrefWidth(300);

        TextField amountField = createStyledTextField("Enter amount");
        TextField descriptionField = createStyledTextField("Transfer description (optional)");

        Label messageLabel = new Label();
        messageLabel.setWrapText(true);
        messageLabel.setStyle("-fx-font-size: 11; -fx-padding: 10; -fx-background-color: rgba(0,0,0,0.3); -fx-border-radius: 4;");

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        Button transferButton = new Button("EXECUTE TRANSFER");
        transferButton.setPrefWidth(150);
        transferButton.setPrefHeight(40);
        transferButton.setStyle("-fx-background-color: linear-gradient(to right, #00d4ff, #0099cc); " +
                "-fx-text-fill: white; -fx-font-weight: bold; -fx-border-radius: 6; -fx-cursor: hand;");

        Button cancelButton = new Button("CANCEL");
        cancelButton.setPrefWidth(150);
        cancelButton.setPrefHeight(40);
        cancelButton.setStyle("-fx-background-color: #1a1f3a; -fx-text-fill: #00d4ff; " +
                "-fx-border-color: #00d4ff; -fx-border-width: 2; -fx-border-radius: 6; -fx-cursor: hand;");

        transferButton.setOnAction(e -> {
            try {
                Account fromAccount = fromCombo.getValue();
                Account toAccount = toCombo.getValue();
                double amount = Double.parseDouble(amountField.getText());
                String description = descriptionField.getText().isEmpty() ? "Transfer" : descriptionField.getText();

                if (fromAccount == null || toAccount == null) {
                    messageLabel.setText("⚠ Please select both accounts");
                    messageLabel.setStyle("-fx-text-fill: #f39c12;");
                } else if (amount <= 0) {
                    messageLabel.setText("⚠ Amount must be greater than 0");
                    messageLabel.setStyle("-fx-text-fill: #f39c12;");
                } else if (fromAccount.getAccountId() == toAccount.getAccountId()) {
                    messageLabel.setText("⚠ Cannot transfer to the same account");
                    messageLabel.setStyle("-fx-text-fill: #f39c12;");
                } else if (accountController.transferFunds(
                        fromAccount.getAccountId(),
                        toAccount.getAccountId(),
                        amount,
                        description)) {
                    messageLabel.setText("✓ Transfer successful!");
                    messageLabel.setStyle("-fx-text-fill: #1abc9c;");
                    amountField.clear();
                    descriptionField.clear();
                } else {
                    messageLabel.setText("✗ Transfer failed. Insufficient balance?");
                    messageLabel.setStyle("-fx-text-fill: #e74c3c;");
                }
            } catch (NumberFormatException ex) {
                messageLabel.setText("✗ Invalid amount entered");
                messageLabel.setStyle("-fx-text-fill: #e74c3c;");
            }
        });

        cancelButton.setOnAction(e -> showDashboard());

        buttonBox.getChildren().addAll(transferButton, cancelButton);

        formBox.getChildren().addAll(
            createStyledLabel("FROM ACCOUNT"),
            fromCombo,
            createStyledLabel("TO ACCOUNT"),
            toCombo,
            createStyledLabel("AMOUNT"),
            amountField,
            createStyledLabel("DESCRIPTION"),
            descriptionField,
            messageLabel,
            buttonBox
        );

        mainContainer.getChildren().addAll(titleLabel, formBox);

        Scene scene = new Scene(mainContainer, 700, 700);
        primaryStage.setTitle("FLECA - Transfer");
        primaryStage.setScene(scene);
    }

    private void showTransactionHistory() {
        VBox mainContainer = new VBox(15);
        mainContainer.setPadding(new Insets(30));
        mainContainer.setStyle("-fx-background-color: #0a0e27;");

        Label titleLabel = new Label("TRANSACTION HISTORY");
        titleLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: #00d4ff; -fx-letter-spacing: 1;");

        VBox transactionBox = new VBox(10);
        List<Transaction> transactions = accountController.getTransactionHistory(currentUser.getUserId());

        if (transactions.isEmpty()) {
            Label emptyLabel = new Label("No transactions found");
            emptyLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #888;");
            transactionBox.getChildren().add(emptyLabel);
        } else {
            for (Transaction trans : transactions) {
                HBox cardBox = new HBox(20);
                cardBox.setPadding(new Insets(15));
                cardBox.setStyle("-fx-background-color: #0f1433; -fx-border-color: #00d4ff; " +
                        "-fx-border-width: 1; -fx-border-radius: 6;");

                VBox cardInfo = new VBox(5);
                Label dateLabel = new Label(trans.getTransactionDate().format(dateFormatter));
                dateLabel.setStyle("-fx-font-size: 10; -fx-text-fill: #888;");

                Label descLabel = new Label(trans.getDescription());
                descLabel.setStyle("-fx-font-size: 13; -fx-font-weight: bold; -fx-text-fill: #e0e0e0;");

                Label typeLabel = new Label(trans.getTransactionType());
                typeLabel.setStyle("-fx-font-size: 10; -fx-text-fill: #00d4ff;");

                cardInfo.getChildren().addAll(dateLabel, descLabel, typeLabel);

                HBox spacer3 = new HBox();
                HBox.setHgrow(spacer3, Priority.ALWAYS);

                String amountColor = trans.getFromAccountId() > 0 ? "#e74c3c" : "#1abc9c";
                Label amountLabel = new Label("$" + String.format("%.2f", trans.getAmount()));
                amountLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: " + amountColor + ";");

                cardBox.getChildren().addAll(cardInfo, spacer3, amountLabel);
                transactionBox.getChildren().add(cardBox);
            }
        }

        ScrollPane scrollPane = new ScrollPane(transactionBox);
        scrollPane.setStyle("-fx-background-color: #0a0e27; -fx-control-inner-background: #0a0e27;");
        scrollPane.setFitToWidth(true);

        Button backButton = new Button("← BACK TO DASHBOARD");
        backButton.setPrefWidth(180);
        backButton.setPrefHeight(40);
        backButton.setStyle("-fx-background-color: #1a1f3a; -fx-text-fill: #00d4ff; " +
                "-fx-border-color: #00d4ff; -fx-border-width: 2; -fx-border-radius: 6; -fx-cursor: hand;");
        backButton.setOnAction(e -> showDashboard());

        VBox bottomBox = new VBox(10);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(20));
        bottomBox.getChildren().add(backButton);

        mainContainer.getChildren().addAll(titleLabel, scrollPane, bottomBox);

        Scene scene = new Scene(mainContainer, 800, 650);
        primaryStage.setTitle("FLECA - Transactions");
        primaryStage.setScene(scene);
    }

    private void showCreateAccountDialog() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Create New Account");
        dialog.setHeaderText("Select Account Type");
        dialog.getDialogPane().setStyle("-fx-background-color: #0f1433; -fx-text-fill: #e0e0e0;");

        ComboBox<String> accountTypeCombo = new ComboBox<>();
        accountTypeCombo.getItems().addAll("Checking", "Savings", "Money Market", "CD");
        accountTypeCombo.setPrefWidth(250);
        accountTypeCombo.setStyle("-fx-padding: 10; -fx-font-size: 12; -fx-background-color: #151a35; " +
                "-fx-text-fill: #e0e0e0; -fx-border-color: #00d4ff; -fx-border-width: 2; -fx-border-radius: 6;");

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: #0f1433;");

        Label typeLabel = createStyledLabel("ACCOUNT TYPE");
        content.getChildren().addAll(typeLabel, accountTypeCombo);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                return accountTypeCombo.getValue();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(accountType -> {
            if (accountController.createAccount(currentUser.getUserId(), accountType)) {
                showAlert("✓ Success", "Account Created", 
                    "New " + accountType + " account created successfully!", true);
                showDashboard();
            } else {
                showAlert("✗ Error", "Account Creation Failed", 
                    "Could not create account. Please try again.", false);
            }
        });
    }

    private void showAlert(String title, String header, String content, boolean isSuccess) {
        Alert alert = new Alert(isSuccess ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.getDialogPane().setStyle("-fx-background-color: #0f1433; -fx-text-fill: #e0e0e0;");
        alert.showAndWait();
    }

    private TextField createStyledTextField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setStyle("-fx-padding: 12; -fx-font-size: 12; -fx-background-color: #151a35; " +
                "-fx-text-fill: #e0e0e0; -fx-control-inner-background: #151a35; -fx-border-color: #00d4ff; " +
                "-fx-border-width: 2; -fx-border-radius: 6;");
        field.setPrefWidth(300);
        return field;
    }

    private PasswordField createStyledPasswordField(String prompt) {
        PasswordField field = new PasswordField();
        field.setPromptText(prompt);
        field.setStyle("-fx-padding: 12; -fx-font-size: 12; -fx-background-color: #151a35; " +
                "-fx-text-fill: #e0e0e0; -fx-control-inner-background: #151a35; -fx-border-color: #00d4ff; " +
                "-fx-border-width: 2; -fx-border-radius: 6;");
        field.setPrefWidth(300);
        return field;
    }

    private Label createStyledLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 11; -fx-text-fill: #888; -fx-letter-spacing: 1;");
        return label;
    }

    public static void main(String[] args) {
        launch(args);
    }
}