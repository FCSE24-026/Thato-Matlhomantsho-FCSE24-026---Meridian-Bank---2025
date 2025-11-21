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
import com.banking.controller.TransactionController;
import com.banking.model.Account;
import com.banking.model.Customer;
import com.banking.model.Transaction;
import com.banking.service.Bank;


import java.util.List;
import java.util.ArrayList;
import java.time.format.DateTimeFormatter;

public class ModernBankingApp extends Application {
    private Stage primaryStage;
    private LoginController authController;
    private AccountController accountController;
    private TransactionController transactionController;
    private User currentUser;
    private Bank bank;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        this.bank = new Bank("Meridian Bank");
        this.authController = new LoginController(bank);
        this.accountController = new AccountController(bank);
        this.transactionController = new TransactionController(bank);
        
        // Initialize with seed data (test accounts)
        initializeSeedData();
        
        showLoginScreen();
    }

    private void initializeSeedData() {
        // Create minimal test users: Thato (Admin), Tellers, and Customers (idempotent)
        // Phone format: 7[1-5]XXXXXX (Botswana mobile format)
        String[] seedEmails = new String[] {"thato.matlhomantsho@bank.com", "teller1@bank.com", "teller2@bank.com", "naledi.customer@bank.com", "simone.customer@bank.com"};
        String[] seedFirstNames = new String[] {"Thato", "Teller", "Teller", "Naledi", "Simone"};
        String[] seedLastNames = new String[] {"Matlhomantsho", "One", "Two", "Mophatlhana", "Matsebela"};
        String[] seedPhones = new String[] {"71456789", "72567890", "73678901", "74789012", "75890123"};
        String[] seedPasswords = new String[] {"AdminPass123", "TellerPass123", "TellerPass456", "CustomerPass123", "CustomerPass456"};
        java.util.Map<String, Role> seedRoles = new java.util.HashMap<>();
        seedRoles.put("thato.matlhomantsho@bank.com", Role.ADMIN);
        seedRoles.put("teller1@bank.com", Role.TELLER);
        seedRoles.put("teller2@bank.com", Role.TELLER);
        seedRoles.put("naledi.customer@bank.com", Role.CUSTOMER);
        seedRoles.put("simone.customer@bank.com", Role.CUSTOMER);

        System.out.println("\n========== MERIDIAN BANK - SEED USER LOGIN CREDENTIALS ==========");
        System.out.println("ADMIN:\n");
        for (int i = 0; i < seedEmails.length; i++) {
            String email = seedEmails[i];
            try {
                com.banking.model.Customer existing = bank.getCustomerByEmail(email);
                if (existing == null) {
                    authController.registerUser(seedFirstNames[i], seedLastNames[i], email, seedPhones[i], "Meridian Bank Botswana", seedRoles.get(email));
                    if (i == 0) System.out.println("  Email: " + email + " | Password: " + seedPasswords[i]);
                } else {
                    if (i == 0) System.out.println("  Email: " + email + " | Password: " + seedPasswords[i] + " (existing)");
                }
            } catch (Exception ex) {
                System.out.println("⚠ Seed user creation failed for " + email + ": " + ex.getMessage());
            }
        }
        System.out.println("\nTELLERS:\n");
        for (int i = 1; i <= 2; i++) {
            String email = seedEmails[i];
            System.out.println("  Email: " + email + " | Password: " + seedPasswords[i]);
        }
        System.out.println("\nCUSTOMERS:\n");
        for (int i = 3; i < seedEmails.length; i++) {
            String email = seedEmails[i];
            System.out.println("  Email: " + email + " | Password: " + seedPasswords[i]);
        }
        System.out.println("\n====================================================================\n");
        // Ensure seeded administrative users have correct roles (fix existing DB rows from older runs)
        try {
            com.banking.model.Customer admin = bank.getCustomerByEmail("admin@bank.com");
            if (admin != null && admin.getRole() != Role.ADMIN) {
                admin.setRole(Role.ADMIN);
                admin.setApproved(true);
                bank.updateCustomer(admin);
                System.out.println("✓ Seed admin corrected to ADMIN role");
            }
            com.banking.model.Customer teller = bank.getCustomerByEmail("teller@bank.com");
            if (teller != null && teller.getRole() != Role.TELLER) {
                teller.setRole(Role.TELLER);
                teller.setApproved(true);
                bank.updateCustomer(teller);
                System.out.println("✓ Seed teller corrected to TELLER role");
            }
        } catch (Exception ex) {
            System.out.println("⚠ Seed role correction skipped: " + ex.getMessage());
        }
        
        // Create default accounts for seeded customers only if they don't already have accounts
        try {
            com.banking.model.Customer john = bank.getCustomerByEmail("john.doe@bank.com");
            com.banking.model.Customer jane = bank.getCustomerByEmail("jane.smith@bank.com");

            if (john != null) {
                List<Account> johnAccounts = bank.getAllAccountsForCustomer(john.getCustomerId());
                if (johnAccounts == null || johnAccounts.isEmpty()) {
                    Account a1 = accountController.openSavingsAccount(john);
                    Account a2 = accountController.openInvestmentAccount(john, 1000.0);
                    System.out.println("✓ Seed accounts created for John: " + (a1 != null ? a1.getAccountId() : "n/a") + ", " + (a2 != null ? a2.getAccountId() : "n/a"));
                } else {
                    System.out.println("✓ John already has accounts: ");
                    for (Account a : johnAccounts) System.out.println("   - " + a.getAccountId() + " (" + a.getAccountType() + ")");
                }
            }

            if (jane != null) {
                List<Account> janeAccounts = bank.getAllAccountsForCustomer(jane.getCustomerId());
                if (janeAccounts == null || janeAccounts.isEmpty()) {
                    Account a = accountController.openSavingsAccount(jane);
                    System.out.println("✓ Seed account created for Jane: " + (a != null ? a.getAccountId() : "n/a"));
                } else {
                    System.out.println("✓ Jane already has accounts: ");
                    for (Account a : janeAccounts) System.out.println("   - " + a.getAccountId() + " (" + a.getAccountType() + ")");
                }
            }
        } catch (Exception ex) {
            System.out.println("⚠ Seed account creation error: " + ex.getMessage());
        }

        System.out.println("✓ Seed data initialized with test users and sample accounts");
    }

    private void showLoginScreen() {
        VBox mainContainer = new VBox(20);
        mainContainer.setPadding(new Insets(60, 100, 60, 100));
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setStyle("-fx-background-color: #ffffff;");

        Label titleLabel = new Label("Meridian Bank");
        titleLabel.setStyle("-fx-font-size: 48; -fx-font-weight: bold; -fx-text-fill: #000000;");
        
        Label subtitleLabel = new Label("Banking System");
        subtitleLabel.setStyle("-fx-font-size: 16; -fx-text-fill: #666666; -fx-letter-spacing: 2;");

        Separator sep1 = new Separator();
        sep1.setStyle("-fx-border-color: rgba(0,0,0,0.2); -fx-border-width: 1;");

        VBox formBox = new VBox(15);
        formBox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; " +
                "-fx-border-width: 1; -fx-border-radius: 8; -fx-padding: 40; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 10, 0, 0, 2);");

        Label formTitleLabel = new Label("Secure Access");
        formTitleLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #000000;");

        Label usernameLabel = new Label("USERNAME");
        usernameLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #666666; -fx-letter-spacing: 1;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter your username");
        usernameField.setStyle("-fx-padding: 12; -fx-font-size: 12; -fx-background-color: #ffffff; " +
                "-fx-text-fill: #1a1a1a; -fx-control-inner-background: #ffffff; -fx-border-color: #cccccc; " +
                "-fx-border-width: 2; -fx-border-radius: 6;");
        usernameField.setPrefWidth(300);

        Label passwordLabel = new Label("PASSWORD");
        passwordLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #666666; -fx-letter-spacing: 1;");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setStyle("-fx-padding: 12; -fx-font-size: 12; -fx-background-color: #ffffff; " +
                "-fx-text-fill: #1a1a1a; -fx-control-inner-background: #ffffff; -fx-border-color: #cccccc; " +
                "-fx-border-width: 2; -fx-border-radius: 6;");
        passwordField.setPrefWidth(300);

        Label messageLabel = new Label();
        messageLabel.setWrapText(true);
        messageLabel.setPrefWidth(300);
        messageLabel.setStyle("-fx-font-size: 11; -fx-padding: 10; -fx-background-color: rgba(0,0,0,0.05); -fx-border-radius: 4;");

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        Button loginButton = new Button("LOGIN");
        loginButton.setPrefWidth(140);
        loginButton.setPrefHeight(45);
        loginButton.setStyle("-fx-padding: 0; -fx-font-size: 13; -fx-font-weight: bold; " +
                "-fx-background-color: linear-gradient(to right, #000000, #1a1a1a); " +
                "-fx-text-fill: white; -fx-border-radius: 6; -fx-cursor: hand;");

        Button registerButton = new Button("SIGN UP");
        registerButton.setPrefWidth(140);
        registerButton.setPrefHeight(45);
        registerButton.setStyle("-fx-padding: 0; -fx-font-size: 13; -fx-font-weight: bold; " +
                "-fx-background-color: #f5f5f5; -fx-text-fill: #000000; " +
                "-fx-border-color: #cccccc; -fx-border-width: 2; -fx-border-radius: 6; -fx-cursor: hand;");

        loginButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                messageLabel.setText("⚠ Please fill in all fields");
                messageLabel.setStyle("-fx-text-fill: #f57c00; -fx-font-size: 11; -fx-padding: 10; " +
                        "-fx-background-color: rgba(245,124,0,0.1); -fx-border-radius: 4;");
            } else if (password.length() < 6) {
                messageLabel.setText("⚠ Password must be at least 6 characters");
                messageLabel.setStyle("-fx-text-fill: #f57c00; -fx-font-size: 11; -fx-padding: 10; " +
                        "-fx-background-color: rgba(245,124,0,0.1); -fx-border-radius: 4;");
            } else if (authController.authenticateUser(username, password)) {
                currentUser = authController.getUserByUsername(username);
                System.out.println("[UI] Logged in user: " + (currentUser != null ? currentUser.getUsername() + " role=" + currentUser.getRole() : "null"));
                if (currentUser != null) {
                    showDashboard();
                } else {
                    messageLabel.setText("✗ Error retrieving user information");
                    messageLabel.setStyle("-fx-text-fill: #d32f2f; -fx-font-size: 11; -fx-padding: 10; " +
                            "-fx-background-color: rgba(211,47,47,0.1); -fx-border-radius: 4;");
                }
            } else {
                messageLabel.setText("✗ Invalid username or password");
                messageLabel.setStyle("-fx-text-fill: #d32f2f; -fx-font-size: 11; -fx-padding: 10; " +
                        "-fx-background-color: rgba(211,47,47,0.1); -fx-border-radius: 4;");
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
        primaryStage.setTitle("MERIDIAN BANK - Professional Banking System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Require the current user to have a specific role. If not, show an alert and return false.
     */
    private boolean requireRole(Role required) {
        if (currentUser == null || currentUser.getRole() == null || currentUser.getRole() != required) {
            showAlert("Unauthorized", "Access denied", "You do not have permission to access this area", false);
            showLoginScreen();
            return false;
        }
        return true;
    }

    private void showRegisterScreen() {
        VBox mainContainer = new VBox(15);
        mainContainer.setPadding(new Insets(40));
        mainContainer.setStyle("-fx-background-color: #ffffff;");

        Label titleLabel = new Label("CREATE ACCOUNT");
        titleLabel.setStyle("-fx-font-size: 28; -fx-font-weight: bold; -fx-text-fill: #000000;");

        VBox formBox = new VBox(12);
        formBox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; " +
                "-fx-border-width: 1; -fx-border-radius: 8; -fx-padding: 30; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 10, 0, 0, 2)");

    TextField firstNameField = createStyledTextField("First name");
    TextField lastNameField = createStyledTextField("Last name");
    TextField usernameField = createStyledTextField("Choose username");
    PasswordField passwordField = createStyledPasswordField("Enter password (min 6 chars)");
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
        String pwd = passwordField.getText();
        String confirmPwd = confirmField.getText();
        String pwd_err = com.banking.util.PasswordUtil.getPasswordValidationError(pwd);
        
        if (pwd_err != null) {
            messageLabel.setText("✗ " + pwd_err);
            messageLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 11; -fx-padding: 10; " +
                    "-fx-background-color: rgba(231,76,60,0.1); -fx-border-radius: 4;");
        } else if (!pwd.equals(confirmPwd)) {
            messageLabel.setText("✗ Passwords do not match");
            messageLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 11; -fx-padding: 10; " +
                    "-fx-background-color: rgba(231,76,60,0.1); -fx-border-radius: 4;");
        } else if (authController.registerUser(
            firstNameField.getText().trim(),
            lastNameField.getText().trim(),
            emailField.getText().trim(),
            phoneField.getText().trim(),
            "")) {
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
            createStyledLabel("FIRST NAME"),
            firstNameField,
            createStyledLabel("LAST NAME"),
            lastNameField,
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
        // Route to role-specific dashboard
        if (currentUser.getRole() == Role.ADMIN) {
            showAdminDashboard();
        } else if (currentUser.getRole() == Role.TELLER) {
            showTellerDashboard();
        } else {
            showCustomerDashboard();
        }
    }

    private void showAdminDashboard() {
        if (!requireRole(Role.ADMIN)) return;
        VBox mainContainer = new VBox(15);
        mainContainer.setPadding(new Insets(0));
        mainContainer.setStyle("-fx-background-color: #0a0e27;");

        HBox headerBox = new HBox(20);
        headerBox.setPadding(new Insets(25));
        headerBox.setStyle("-fx-background-color: linear-gradient(to right, #c0392b, #8b0000); " +
                "-fx-border-color: #e74c3c; -fx-border-width: 0 0 2 0;");
        headerBox.setAlignment(Pos.CENTER_LEFT);

        Label welcomeLabel = new Label("🔐 ADMIN PANEL - " + currentUser.getUsername().toUpperCase());
        welcomeLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: #e74c3c;");

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

        headerBox.getChildren().addAll(welcomeLabel, spacer, logoutButton);

        VBox contentPanel = new VBox(15);
        contentPanel.setPadding(new Insets(30));
        contentPanel.setStyle("-fx-background-color: #0a0e27;");

        Label titleLabel = new Label("ADMINISTRATOR FUNCTIONS");
        titleLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #e74c3c; -fx-letter-spacing: 2;");

        HBox buttonRowBox = new HBox(15);
        buttonRowBox.setAlignment(Pos.CENTER_LEFT);

        Button viewUsersButton = new Button("👥 VIEW ALL USERS");
        viewUsersButton.setPrefWidth(180);
        viewUsersButton.setPrefHeight(50);
        viewUsersButton.setStyle("-fx-background-color: linear-gradient(to right, #c0392b, #8b0000); " +
                "-fx-text-fill: white; -fx-font-weight: bold; -fx-border-radius: 6; -fx-cursor: hand; -fx-font-size: 11;");
        viewUsersButton.setOnAction(e -> showViewAllUsersScreen());

        Button approveRegistrationsButton = new Button("✓ APPROVE REGISTRATIONS");
        approveRegistrationsButton.setPrefWidth(180);
        approveRegistrationsButton.setPrefHeight(50);
        approveRegistrationsButton.setStyle("-fx-background-color: linear-gradient(to right, #27ae60, #1e8449); " +
                "-fx-text-fill: white; -fx-font-weight: bold; -fx-border-radius: 6; -fx-cursor: hand; -fx-font-size: 11;");
        approveRegistrationsButton.setOnAction(e -> showApproveRegistrationsScreen());

        Button manageAccountsButton = new Button("💼 MANAGE ACCOUNTS");
        manageAccountsButton.setPrefWidth(180);
        manageAccountsButton.setPrefHeight(50);
        manageAccountsButton.setStyle("-fx-background-color: linear-gradient(to right, #c0392b, #8b0000); " +
                "-fx-text-fill: white; -fx-font-weight: bold; -fx-border-radius: 6; -fx-cursor: hand; -fx-font-size: 11;");
        manageAccountsButton.setOnAction(e -> showManageAccountsScreen());

        Button systemStatsButton = new Button("📊 SYSTEM STATISTICS");
        systemStatsButton.setPrefWidth(180);
        systemStatsButton.setPrefHeight(50);
        systemStatsButton.setStyle("-fx-background-color: linear-gradient(to right, #c0392b, #8b0000); " +
                "-fx-text-fill: white; -fx-font-weight: bold; -fx-border-radius: 6; -fx-cursor: hand; -fx-font-size: 11;");
        systemStatsButton.setOnAction(e -> showSystemStatisticsScreen());

        Button auditLogButton = new Button("📝 AUDIT LOGS");
        auditLogButton.setPrefWidth(180);
        auditLogButton.setPrefHeight(50);
        auditLogButton.setStyle("-fx-background-color: linear-gradient(to right, #34495e, #2c3e50); " +
            "-fx-text-fill: white; -fx-font-weight: bold; -fx-border-radius: 6; -fx-cursor: hand; -fx-font-size: 11;");
        auditLogButton.setOnAction(e -> showAuditLogScreen());

        Button createTellerButton = new Button("➕ CREATE TELLER");
        createTellerButton.setPrefWidth(180);
        createTellerButton.setPrefHeight(50);
        createTellerButton.setStyle("-fx-background-color: linear-gradient(to right, #2980b9, #1f618d); " +
            "-fx-text-fill: white; -fx-font-weight: bold; -fx-border-radius: 6; -fx-cursor: hand; -fx-font-size: 11;");
        createTellerButton.setOnAction(e -> showCreateTellerScreen());

        buttonRowBox.getChildren().addAll(viewUsersButton, approveRegistrationsButton, manageAccountsButton, systemStatsButton, auditLogButton, createTellerButton);
        contentPanel.getChildren().addAll(titleLabel, buttonRowBox);

        mainContainer.getChildren().addAll(headerBox, contentPanel);

        Scene scene = new Scene(mainContainer, 900, 400);
        primaryStage.setTitle("Meridian Bank - Admin Dashboard");
        primaryStage.setScene(scene);
    }

    private void showTellerDashboard() {
        if (!requireRole(Role.TELLER)) return;
        VBox mainContainer = new VBox(15);
        mainContainer.setPadding(new Insets(0));
        mainContainer.setStyle("-fx-background-color: #0a0e27;");

        HBox headerBox = new HBox(20);
        headerBox.setPadding(new Insets(25));
        headerBox.setStyle("-fx-background-color: linear-gradient(to right, #f39c12, #d68910); " +
                "-fx-border-color: #f39c12; -fx-border-width: 0 0 2 0;");
        headerBox.setAlignment(Pos.CENTER_LEFT);

        Label welcomeLabel = new Label("📋 TELLER PANEL - " + currentUser.getUsername().toUpperCase());
        welcomeLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: #f39c12;");

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button logoutButton = new Button("LOGOUT");
        logoutButton.setPrefWidth(120);
        logoutButton.setPrefHeight(40);
        logoutButton.setStyle("-fx-background-color: linear-gradient(to right, #f39c12, #d68910); " +
                "-fx-text-fill: white; -fx-font-weight: bold; -fx-border-radius: 6; -fx-cursor: hand;");
        logoutButton.setOnAction(e -> {
            currentUser = null;
            showLoginScreen();
        });

        headerBox.getChildren().addAll(welcomeLabel, spacer, logoutButton);

        VBox contentPanel = new VBox(15);
        contentPanel.setPadding(new Insets(30));
        contentPanel.setStyle("-fx-background-color: #0a0e27;");

        Label titleLabel = new Label("TELLER OPERATIONS");
        titleLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #f39c12; -fx-letter-spacing: 2;");

        HBox buttonRowBox = new HBox(15);
        buttonRowBox.setAlignment(Pos.CENTER_LEFT);

    Button processTransactionButton = new Button("💳 PROCESS TRANSACTION");
        processTransactionButton.setPrefWidth(180);
        processTransactionButton.setPrefHeight(50);
        processTransactionButton.setStyle("-fx-background-color: linear-gradient(to right, #f39c12, #d68910); " +
                "-fx-text-fill: white; -fx-font-weight: bold; -fx-border-radius: 6; -fx-cursor: hand; -fx-font-size: 11;");
    processTransactionButton.setOnAction(e -> showProcessTransactionScreen());

    Button openAccountButton = new Button("➕ OPEN NEW ACCOUNT");
        openAccountButton.setPrefWidth(180);
        openAccountButton.setPrefHeight(50);
        openAccountButton.setStyle("-fx-background-color: linear-gradient(to right, #f39c12, #d68910); " +
                "-fx-text-fill: white; -fx-font-weight: bold; -fx-border-radius: 6; -fx-cursor: hand; -fx-font-size: 11;");
    openAccountButton.setOnAction(e -> showOpenAccountScreen());

    Button verifyCustomerButton = new Button("🔍 VERIFY CUSTOMER");
        verifyCustomerButton.setPrefWidth(180);
        verifyCustomerButton.setPrefHeight(50);
        verifyCustomerButton.setStyle("-fx-background-color: linear-gradient(to right, #f39c12, #d68910); " +
                "-fx-text-fill: white; -fx-font-weight: bold; -fx-border-radius: 6; -fx-cursor: hand; -fx-font-size: 11;");
    verifyCustomerButton.setOnAction(e -> showVerifyCustomerScreen());

        buttonRowBox.getChildren().addAll(processTransactionButton, openAccountButton, verifyCustomerButton);
        contentPanel.getChildren().addAll(titleLabel, buttonRowBox);

        mainContainer.getChildren().addAll(headerBox, contentPanel);

        Scene scene = new Scene(mainContainer, 900, 400);
        primaryStage.setTitle("Meridian Bank - Teller Dashboard");
        primaryStage.setScene(scene);
    }

    private void showViewAllUsersScreen() {
        VBox mainContainer = new VBox(15);
        mainContainer.setPadding(new Insets(0));
        mainContainer.setStyle("-fx-background-color: #0a0e27;");

        HBox headerBox = new HBox(20);
        headerBox.setPadding(new Insets(25));
        headerBox.setStyle("-fx-background-color: linear-gradient(to right, #c0392b, #8b0000); " +
                "-fx-border-color: #e74c3c; -fx-border-width: 0 0 2 0;");
        headerBox.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label("👥 ALL REGISTERED USERS");
        titleLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #e74c3c;");

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button backButton = new Button("← BACK");
        backButton.setPrefWidth(100);
        backButton.setPrefHeight(40);
        backButton.setStyle("-fx-background-color: linear-gradient(to right, #e74c3c, #c0392b); " +
                "-fx-text-fill: white; -fx-font-weight: bold; -fx-border-radius: 6; -fx-cursor: hand;");
        backButton.setOnAction(e -> showAdminDashboard());

        headerBox.getChildren().addAll(titleLabel, spacer, backButton);

        VBox contentPanel = new VBox(15);
        contentPanel.setPadding(new Insets(20));
        contentPanel.setStyle("-fx-background-color: #0a0e27;");

        TableView<Customer> usersTable = new TableView<>();
        usersTable.setStyle("-fx-background-color: #0f1433; -fx-control-inner-background: #0f1433; " +
                "-fx-text-fill: #e0e0e0; -fx-border-color: #e74c3c; -fx-border-width: 1;");

        TableColumn<Customer, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setPrefWidth(150);
        nameColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getFirstName() + " " + cellData.getValue().getSurname()));

        TableColumn<Customer, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setPrefWidth(200);
        emailColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEmail()));

        TableColumn<Customer, String> phoneColumn = new TableColumn<>("Phone");
        phoneColumn.setPrefWidth(120);
        phoneColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPhoneNumber()));

        TableColumn<Customer, String> addressColumn = new TableColumn<>("Address");
        addressColumn.setPrefWidth(200);
        addressColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getAddress()));

        TableColumn<Customer, String> roleColumn = new TableColumn<>("Role");
        roleColumn.setPrefWidth(100);
        roleColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getRole() != null ? cellData.getValue().getRole().getDisplayName() : "N/A"));

        TableColumn<Customer, String> accountsColumn = new TableColumn<>("Accounts");
        accountsColumn.setPrefWidth(300);
        accountsColumn.setCellValueFactory(cellData -> {
            Customer c = cellData.getValue();
            List<Account> accts = bank.getAllAccountsForCustomer(c.getCustomerId());
            String joined = accts == null || accts.isEmpty() ? "-" : String.join(", ", accts.stream().map(a -> a.getAccountId()).toArray(String[]::new));
            return new javafx.beans.property.SimpleStringProperty(joined);
        });

        TableColumn<Customer, Void> userActionColumn = new TableColumn<>("Action");
        userActionColumn.setPrefWidth(140);
        userActionColumn.setCellFactory(param -> new TableCell<Customer, Void>() {
            private final Button deleteBtn = new Button("Delete");
            {
                deleteBtn.setStyle("-fx-padding: 5; -fx-font-size: 11; -fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand;");
                deleteBtn.setOnAction(e -> {
                    Customer customer = getTableView().getItems().get(getIndex());
                    if (customer == null) return;
                    // Prevent admin deleting their own user account
                    if (currentUser != null && currentUser.getUserId() != null && currentUser.getUserId().equals(customer.getCustomerId())) {
                        showAlert("Unauthorized", "Action denied", "You cannot delete your own admin account while logged in.", false);
                        return;
                    }
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                    confirm.setTitle("Confirm Delete");
                    confirm.setHeaderText("Delete User: " + customer.getFirstName() + " " + customer.getSurname());
                    confirm.setContentText("Are you sure you want to delete this user? This will remove all associated accounts.");
                    java.util.Optional<javafx.scene.control.ButtonType> result = confirm.showAndWait();
                    if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {
                        boolean ok = bank.deleteCustomer(customer.getCustomerId());
                        if (ok) {
                            getTableView().getItems().remove(customer);
                            showAlert("Success", "User Deleted", "User removed successfully.", true);
                            try {
                                String actorId = currentUser != null ? currentUser.getUserId() : null;
                                String actorEmail = currentUser != null ? currentUser.getUsername() : null;
                                bank.logAction(actorId, actorEmail, "DELETE_USER", "CUSTOMER", customer.getCustomerId(), "Deleted user: " + customer.getEmail(), "OK");
                            } catch (Exception ex) {}
                        } else {
                            showAlert("Error", "Delete Failed", "Could not delete user from database.", false);
                        }
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() < 0 || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteBtn);
                }
            }
        });

        usersTable.getColumns().addAll(nameColumn, emailColumn, phoneColumn, addressColumn, roleColumn, accountsColumn, userActionColumn);

        List<Customer> allCustomers = bank.getAllCustomers();
        usersTable.getItems().addAll(allCustomers);

        Label countLabel = new Label("Total Users: " + allCustomers.size());
        countLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #e74c3c;");

        contentPanel.getChildren().addAll(countLabel, usersTable);
        VBox.setVgrow(usersTable, Priority.ALWAYS);

        mainContainer.getChildren().addAll(headerBox, contentPanel);

        Scene scene = new Scene(mainContainer, 1000, 600);
        primaryStage.setTitle("Meridian Bank - View All Users");
        primaryStage.setScene(scene);
    }

    private void showApproveRegistrationsScreen() {
        if (!requireRole(Role.ADMIN)) return;
        VBox mainContainer = new VBox(15);
        mainContainer.setPadding(new Insets(0));
        mainContainer.setStyle("-fx-background-color: #0a0e27;");

        HBox headerBox = new HBox(20);
        headerBox.setPadding(new Insets(25));
        headerBox.setStyle("-fx-background-color: linear-gradient(to right, #27ae60, #1e8449); " +
                "-fx-border-color: #27ae60; -fx-border-width: 0 0 2 0;");
        headerBox.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label("✓ APPROVE REGISTRATIONS");
        titleLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #27ae60;");

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button backButton = new Button("← BACK");
        backButton.setPrefWidth(100);
        backButton.setPrefHeight(40);
        backButton.setStyle("-fx-background-color: linear-gradient(to right, #27ae60, #1e8449); " +
                "-fx-text-fill: white; -fx-font-weight: bold; -fx-border-radius: 6; -fx-cursor: hand;");
        backButton.setOnAction(e -> showAdminDashboard());

        headerBox.getChildren().addAll(titleLabel, spacer, backButton);

        VBox contentPanel = new VBox(15);
        contentPanel.setPadding(new Insets(20));
        contentPanel.setStyle("-fx-background-color: #0a0e27;");

        TableView<Customer> pendingTable = new TableView<>();
        pendingTable.setStyle("-fx-background-color: #0f1433; -fx-control-inner-background: #0f1433; " +
                "-fx-text-fill: #e0e0e0; -fx-border-color: #27ae60; -fx-border-width: 1;");

        TableColumn<Customer, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setPrefWidth(150);
        nameColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getFirstName() + " " + cellData.getValue().getSurname()));

        TableColumn<Customer, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setPrefWidth(200);
        emailColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEmail()));

        TableColumn<Customer, String> statusColumn = new TableColumn<>("Approval Status");
        statusColumn.setPrefWidth(150);
        statusColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().isApproved() ? "✓ APPROVED" : "⏳ PENDING"));

        TableColumn<Customer, Void> actionColumn = new TableColumn<>("Action");
        actionColumn.setPrefWidth(150);
        actionColumn.setCellFactory(param -> new TableCell<Customer, Void>() {
            private final Button approveBtn = new Button("✓ Approve");
            private final Button denyBtn = new Button("✗ Deny");
            {
                approveBtn.setStyle("-fx-padding: 5; -fx-font-size: 10; -fx-background-color: #27ae60; " +
                        "-fx-text-fill: white; -fx-border-radius: 3; -fx-cursor: hand;");
                approveBtn.setOnAction(event -> {
                    Customer customer = getTableView().getItems().get(getIndex());
                    if (customer != null) {
                        customer.setApproved(true);
                        boolean ok = bank.updateCustomer(customer);
                        if (!ok) {
                            showAlert("Error", "Save Failed", "Could not persist approval for " + customer.getEmail(), false);
                            return;
                        }
                        getTableView().refresh();
                        showAlert("Success", "Customer Approved", customer.getFirstName() + " " + customer.getSurname() + " has been approved and can now login.", true);
                    }
                });

                denyBtn.setStyle("-fx-padding: 5; -fx-font-size: 10; -fx-background-color: #e74c3c; " +
                        "-fx-text-fill: white; -fx-border-radius: 3; -fx-cursor: hand;");
                denyBtn.setOnAction(event -> {
                    Customer customer = getTableView().getItems().get(getIndex());
                    if (customer != null) {
                        bank.deleteCustomer(customer.getCustomerId());
                        getTableView().getItems().remove(customer);
                        getTableView().refresh();
                        showAlert("Info", "Registration Denied", "Registration of " + customer.getFirstName() + " has been denied and removed.", true);
                        try { String actorId = currentUser != null ? currentUser.getUserId() : null; String actorEmail = currentUser != null ? currentUser.getUsername() : null; bank.logAction(actorId, actorEmail, "DENY_REGISTRATION", "CUSTOMER", customer.getCustomerId(), "Denied registration for " + customer.getEmail(), "DENIED"); } catch (Exception ex) {}
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() < 0 || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else if (getTableView().getItems().get(getIndex()).isApproved()) {
                    setGraphic(null);
                } else {
                    HBox buttonBox = new HBox(5);
                    buttonBox.getChildren().addAll(approveBtn, denyBtn);
                    setGraphic(buttonBox);
                }
            }
        });

        pendingTable.getColumns().addAll(nameColumn, emailColumn, statusColumn, actionColumn);

        // Filter to show customers (not admin/teller) who need approval
        List<Customer> pendingApprovals = bank.getAllCustomers().stream()
            .filter(c -> c.getRole() == Role.CUSTOMER && !c.isApproved())
            .collect(java.util.stream.Collectors.toList());

        pendingTable.getItems().addAll(pendingApprovals);

        Label countLabel = new Label("Pending Approvals: " + pendingApprovals.size());
        countLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #27ae60;");

        contentPanel.getChildren().addAll(countLabel, pendingTable);
        VBox.setVgrow(pendingTable, Priority.ALWAYS);

        mainContainer.getChildren().addAll(headerBox, contentPanel);

        Scene scene = new Scene(mainContainer, 1000, 600);
        primaryStage.setTitle("Meridian Bank - Approve Registrations");
        primaryStage.setScene(scene);
    }

    private void showProcessTransactionScreen() {
        // allow tellers and admins to access this screen
        if (currentUser == null || (currentUser.getRole() != Role.TELLER && currentUser.getRole() != Role.ADMIN)) {
            showAlert("Unauthorized", "Access denied", "You do not have permission to access this area", false);
            showDashboard();
            return;
        }
        VBox mainContainer = new VBox(15);
        mainContainer.setPadding(new Insets(0));
        mainContainer.setStyle("-fx-background-color: #0a0e27;");

        HBox headerBox = new HBox(20);
        headerBox.setPadding(new Insets(25));
        headerBox.setStyle("-fx-background-color: linear-gradient(to right, #f39c12, #d68910); " +
                "-fx-border-color: #f39c12; -fx-border-width: 0 0 2 0;");
        headerBox.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label("💳 PROCESS TRANSACTION");
        titleLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #f39c12;");

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button backButton = new Button("← BACK");
        backButton.setPrefWidth(100);
        backButton.setPrefHeight(40);
        backButton.setStyle("-fx-background-color: linear-gradient(to right, #f39c12, #d68910); " +
                "-fx-text-fill: white; -fx-font-weight: bold; -fx-border-radius: 6; -fx-cursor: hand;");
        backButton.setOnAction(e -> showTellerDashboard());

        headerBox.getChildren().addAll(titleLabel, spacer, backButton);

        VBox contentPanel = new VBox(12);
        contentPanel.setPadding(new Insets(20));
        contentPanel.setStyle("-fx-background-color: #0a0e27;");

        // Customer selector
        ComboBox<Customer> customerCombo = new ComboBox<>();
        customerCombo.getItems().addAll(bank.getAllCustomers());
        customerCombo.setPromptText("Select Customer");
        customerCombo.setConverter(new javafx.util.StringConverter<Customer>(){
            @Override public String toString(Customer c) { return c == null ? "" : c.getFirstName() + " " + c.getSurname() + " (" + c.getEmail() + ")"; }
            @Override public Customer fromString(String string) { return null; }
        });

        // Account selector
        ComboBox<Account> accountCombo = new ComboBox<>();
        accountCombo.setPromptText("Select Account");
        accountCombo.setConverter(new javafx.util.StringConverter<Account>(){
            @Override public String toString(Account a) { return a == null ? "" : a.getAccountId() + " - BWP " + String.format("%.2f", a.getBalance()); }
            @Override public Account fromString(String string) { return null; }
        });

        // Operation selector
        ComboBox<String> opCombo = new ComboBox<>();
        opCombo.getItems().addAll("Deposit", "Withdraw", "Transfer");
        opCombo.setValue("Deposit");

        TextField amountField = new TextField();
        amountField.setPromptText("Amount (BWP)");

        // Target account selection: allow searching by customer name/email and show owner info
        TextField searchTargetField = new TextField();
        searchTargetField.setPromptText("Search target by customer name or email");
        searchTargetField.setStyle("-fx-padding: 8; -fx-background-color: #151a35; -fx-text-fill: #e0e0e0;");

        ComboBox<Account> targetAccountCombo = new ComboBox<>();
        targetAccountCombo.setPromptText("Target Account (for Transfer)");
        targetAccountCombo.setConverter(new javafx.util.StringConverter<Account>(){
            @Override public String toString(Account a) {
                if (a == null) return "";
                String owner = "Unknown";
                try {
                    com.banking.model.Customer ownerCust = a.getCustomer();
                    if (ownerCust != null) owner = ownerCust.getFirstName() + " " + ownerCust.getSurname() + " (" + ownerCust.getEmail() + ")";
                } catch (Exception ex) {}
                return a.getAccountId() + " - " + owner + " - BWP " + String.format("%.2f", a.getBalance());
            }
            @Override public Account fromString(String string) { return null; }
        });
        targetAccountCombo.setVisible(false);

        // Helper to populate target accounts list (optionally filtered by query)
        // NOTE: allow same-customer accounts (so transfers between a customer's own accounts are possible)
        // but exclude the currently-selected source account to avoid transferring into the same account.
        java.util.function.Consumer<String> populateTargets = (query) -> {
            List<Account> all = bank.getAllAccounts();
            Account sourceAccount = accountCombo.getValue();
            if (query == null || query.trim().isEmpty()) {
                if (sourceAccount == null) {
                    targetAccountCombo.getItems().setAll(all);
                } else {
                    List<Account> filtered = new ArrayList<>();
                    for (Account a : all) {
                        if (a.getAccountId() != null && sourceAccount.getAccountId() != null && a.getAccountId().equals(sourceAccount.getAccountId())) continue;
                        filtered.add(a);
                    }
                    targetAccountCombo.getItems().setAll(filtered);
                }
            } else {
                String q = query.toLowerCase();
                List<Account> filtered = new ArrayList<>();
                for (Account a : all) {
                    com.banking.model.Customer c = a.getCustomer();
                    String name = c == null ? "" : (c.getFirstName() + " " + c.getSurname()).toLowerCase();
                    String email = c == null ? "" : (c.getEmail() == null ? "" : c.getEmail().toLowerCase());
                    if (name.contains(q) || email.contains(q) || a.getAccountId().toLowerCase().contains(q)) {
                        if (sourceAccount != null && a.getAccountId() != null && a.getAccountId().equals(sourceAccount.getAccountId())) continue;
                        filtered.add(a);
                    }
                }
                targetAccountCombo.getItems().setAll(filtered);
            }
        };

        // Initialize with full list
        populateTargets.accept("");

        // Small owner preview panel for the selected target account
        VBox ownerPreview = new VBox(4);
        ownerPreview.setStyle("-fx-background-color: #0f1433; -fx-border-color: #00d4ff; -fx-border-width: 1; -fx-padding: 10; -fx-border-radius: 6;");
        ownerPreview.setVisible(false);
        Label ownerTitle = new Label("Target Owner");
        ownerTitle.setStyle("-fx-font-size: 12; -fx-font-weight: bold; -fx-text-fill: #00d4ff;");
        Label ownerNameLabel = new Label("");
        ownerNameLabel.setStyle("-fx-text-fill: #e0e0e0;");
        Label ownerEmailLabel = new Label("");
        ownerEmailLabel.setStyle("-fx-text-fill: #e0e0e0;");
        Label ownerIdLabel = new Label("");
        ownerIdLabel.setStyle("-fx-text-fill: #e0e0e0;");
        Label ownerPhoneLabel = new Label("");
        ownerPhoneLabel.setStyle("-fx-text-fill: #e0e0e0;");
        ownerPreview.getChildren().addAll(ownerTitle, ownerNameLabel, ownerEmailLabel, ownerIdLabel, ownerPhoneLabel);

        // Filter as user types
        searchTargetField.textProperty().addListener((obs, oldV, newV) -> populateTargets.accept(newV));

        // Update owner preview when target account changes
        targetAccountCombo.valueProperty().addListener((obs, oldA, newA) -> {
            if (newA == null) {
                ownerPreview.setVisible(false);
                ownerNameLabel.setText(""); ownerEmailLabel.setText(""); ownerIdLabel.setText(""); ownerPhoneLabel.setText("");
            } else {
                com.banking.model.Customer oc = newA.getCustomer();
                if (oc != null) {
                    ownerNameLabel.setText("Name: " + oc.getFirstName() + " " + oc.getSurname());
                    ownerEmailLabel.setText("Email: " + oc.getEmail());
                    ownerIdLabel.setText("Customer ID: " + oc.getCustomerId());
                    ownerPhoneLabel.setText("Phone: " + (oc.getPhoneNumber() == null ? "-" : oc.getPhoneNumber()));
                } else {
                    ownerNameLabel.setText("Name: Unknown");
                    ownerEmailLabel.setText("Email: -");
                    ownerIdLabel.setText("Customer ID: -");
                    ownerPhoneLabel.setText("Phone: -");
                }
                ownerPreview.setVisible(true);
            }
        });

        customerCombo.setOnAction(e -> {
            Customer sel = customerCombo.getValue();
            accountCombo.getItems().clear();
            if (sel != null) accountCombo.getItems().addAll(accountController.getCustomerAccounts(sel));
            // when a customer is selected, pre-filter the target list to exclude their own accounts
            searchTargetField.clear();
            populateTargets.accept("");
        });

        opCombo.setOnAction(e -> {
            String op = opCombo.getValue();
            targetAccountCombo.setVisible("Transfer".equals(op));
        });

        Button submitBtn = new Button("Submit");
        submitBtn.setStyle("-fx-background-color: linear-gradient(to right, #f39c12, #d68910); -fx-text-fill: white; -fx-font-weight: bold;");
        submitBtn.setOnAction(e -> {
            Customer cust = customerCombo.getValue();
            Account acc = accountCombo.getValue();
            String op = opCombo.getValue();
            double amt = 0;
            try { amt = Double.parseDouble(amountField.getText()); } catch (Exception ex) { showAlert("Error", "Invalid amount", "Please enter a valid numeric amount", false); return; }
            if (cust == null || acc == null) { showAlert("Error", "Missing selection", "Select a customer and account", false); return; }
            if (amt <= 0) { showAlert("Error", "Invalid amount", "Amount must be greater than zero", false); return; }

            boolean success = false;
            if ("Deposit".equals(op)) {
                success = transactionController.processDeposit(acc, amt);
            } else if ("Withdraw".equals(op)) {
                success = transactionController.processWithdrawal(acc, amt);
            } else if ("Transfer".equals(op)) {
                Account target = targetAccountCombo.getValue();
                if (target == null) { showAlert("Error", "Missing target", "Select a target account for transfer", false); return; }
                // Prevent transferring into the exact same account
                if (acc.getAccountId() != null && acc.getAccountId().equals(target.getAccountId())) {
                    showAlert("Error", "Invalid target", "Cannot transfer into the same account", false);
                    return;
                }
                success = accountController.transferFunds(acc.getAccountId(), target.getAccountId(), amt, "Teller transfer");
            }

            if (success) {
                showAlert("Success", "Transaction Completed", "Operation successful", true);
                try { Thread.sleep(1000); } catch (InterruptedException ex) {}
                showTellerDashboard();
            } else {
                showAlert("Failure", "Transaction Failed", "Operation could not be completed", false);
            }
        });

        contentPanel.getChildren().addAll(titleLabel, customerCombo, accountCombo, opCombo, amountField, searchTargetField, targetAccountCombo, ownerPreview, submitBtn);
        mainContainer.getChildren().addAll(headerBox, contentPanel);

        Scene scene = new Scene(mainContainer, 900, 500);
        primaryStage.setTitle("Meridian Bank - Process Transaction");
        primaryStage.setScene(scene);
    }

    private void showOpenAccountScreen() {
        // tellers and admins can open accounts for customers via this screen
        if (currentUser == null || (currentUser.getRole() != Role.TELLER && currentUser.getRole() != Role.ADMIN)) {
            showAlert("Unauthorized", "Access denied", "You do not have permission to access this area", false);
            showDashboard();
            return;
        }
        VBox mainContainer = new VBox(12);
        mainContainer.setPadding(new Insets(0));
        mainContainer.setStyle("-fx-background-color: #0a0e27;");

        HBox headerBox = new HBox(20);
        headerBox.setPadding(new Insets(25));
        headerBox.setStyle("-fx-background-color: linear-gradient(to right, #f39c12, #d68910); " +
                "-fx-border-color: #f39c12; -fx-border-width: 0 0 2 0;");
        headerBox.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label("➕ OPEN NEW ACCOUNT");
        titleLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #f39c12;");

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button backButton = new Button("← BACK");
        backButton.setPrefWidth(100);
        backButton.setPrefHeight(40);
        backButton.setStyle("-fx-background-color: linear-gradient(to right, #f39c12, #d68910); " +
                "-fx-text-fill: white; -fx-font-weight: bold; -fx-border-radius: 6; -fx-cursor: hand;");
        backButton.setOnAction(e -> showTellerDashboard());

        headerBox.getChildren().addAll(titleLabel, spacer, backButton);

        VBox contentPanel = new VBox(10);
        contentPanel.setPadding(new Insets(20));
        contentPanel.setStyle("-fx-background-color: #0a0e27;");

        ComboBox<Customer> customerCombo = new ComboBox<>();
        customerCombo.getItems().addAll(bank.getAllCustomers());
        customerCombo.setPromptText("Select Customer");
        customerCombo.setConverter(new javafx.util.StringConverter<Customer>(){
            @Override public String toString(Customer c) { return c == null ? "" : c.getFirstName() + " " + c.getSurname() + " (" + c.getEmail() + ")"; }
            @Override public Customer fromString(String string) { return null; }
        });

        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("Savings", "Investment", "Cheque");
        typeCombo.setValue("Savings");

        TextField initialDepositField = new TextField();
        initialDepositField.setPromptText("Initial deposit (for Investment)");
        initialDepositField.setVisible(false);

        TextField employerField = new TextField();
        employerField.setPromptText("Employer Name (for Cheque)");
        employerField.setVisible(false);

        TextField employerAddressField = new TextField();
        employerAddressField.setPromptText("Employer Address (for Cheque)");
        employerAddressField.setVisible(false);

        typeCombo.setOnAction(e -> {
            String t = typeCombo.getValue();
            initialDepositField.setVisible("Investment".equals(t));
            employerField.setVisible("Cheque".equals(t));
            employerAddressField.setVisible("Cheque".equals(t));
        });

        Button createBtn = new Button("Create Account");
        createBtn.setStyle("-fx-background-color: linear-gradient(to right, #f39c12, #d68910); -fx-text-fill: white; -fx-font-weight: bold;");
        createBtn.setOnAction(e -> {
            Customer c = customerCombo.getValue();
            if (c == null) { showAlert("Error", "No customer", "Select a customer", false); return; }
            String t = typeCombo.getValue();
            boolean ok = false;
            if ("Savings".equals(t)) {
                ok = accountController.openSavingsAccount(c) != null;
            } else if ("Investment".equals(t)) {
                double amt = 0;
                try { amt = Double.parseDouble(initialDepositField.getText()); } catch (Exception ex) { showAlert("Error", "Invalid deposit", "Enter numeric initial deposit", false); return; }
                ok = accountController.openInvestmentAccount(c, amt) != null;
            } else if ("Cheque".equals(t)) {
                String em = employerField.getText();
                String emAddr = employerAddressField.getText();
                if (em == null || em.isEmpty() || emAddr == null || emAddr.isEmpty()) { showAlert("Error", "Employer info", "Provide employer name and address", false); return; }
                ok = accountController.openChequeAccount(c, em, emAddr) != null;
            }
            if (ok) showAlert("Success", "Account Created", "New account created successfully", true);
            else showAlert("Failure", "Could not create account", "Check input or constraints", false);
        });

        contentPanel.getChildren().addAll(titleLabel, customerCombo, typeCombo, initialDepositField, employerField, employerAddressField, createBtn);
        mainContainer.getChildren().addAll(headerBox, contentPanel);

        Scene scene = new Scene(mainContainer, 900, 500);
        primaryStage.setTitle("Meridian Bank - Open Account");
        primaryStage.setScene(scene);
    }

    private void showVerifyCustomerScreen() {
        if (!requireRole(Role.TELLER)) return;
        VBox mainContainer = new VBox(12);
        mainContainer.setPadding(new Insets(0));
        mainContainer.setStyle("-fx-background-color: #0a0e27;");

        HBox headerBox = new HBox(20);
        headerBox.setPadding(new Insets(25));
        headerBox.setStyle("-fx-background-color: linear-gradient(to right, #f39c12, #d68910); " +
                "-fx-border-color: #f39c12; -fx-border-width: 0 0 2 0;");
        headerBox.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label("🔍 VERIFY CUSTOMER");
        titleLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #f39c12;");

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button backButton = new Button("← BACK");
        backButton.setPrefWidth(100);
        backButton.setPrefHeight(40);
        backButton.setStyle("-fx-background-color: linear-gradient(to right, #f39c12, #d68910); " +
                "-fx-text-fill: white; -fx-font-weight: bold; -fx-border-radius: 6; -fx-cursor: hand;");
        backButton.setOnAction(e -> showTellerDashboard());

        headerBox.getChildren().addAll(titleLabel, spacer, backButton);

        VBox contentPanel = new VBox(10);
        contentPanel.setPadding(new Insets(20));
        contentPanel.setStyle("-fx-background-color: #0a0e27;");

        TextField emailField = new TextField();
        emailField.setPromptText("Search by email");

        TextField idField = new TextField();
        idField.setPromptText("Or search by Customer ID");

        Button searchBtn = new Button("Search");
        searchBtn.setStyle("-fx-background-color: linear-gradient(to right, #f39c12, #d68910); -fx-text-fill: white; -fx-font-weight: bold;");

        Label resultLabel = new Label();
        resultLabel.setStyle("-fx-text-fill: #e0e0e0; -fx-font-size: 12;");

        ListView<String> accountsList = new ListView<>();
        accountsList.setPrefHeight(150);

        searchBtn.setOnAction(e -> {
            String email = emailField.getText();
            String cid = idField.getText();
            Customer found = null;
            if (email != null && !email.isEmpty()) found = bank.getCustomerByEmail(email);
            if (found == null && cid != null && !cid.isEmpty()) found = bank.getCustomerById(cid);
            if (found == null) { resultLabel.setText("No customer found"); accountsList.getItems().clear(); return; }
            resultLabel.setText("Customer: " + found.getFirstName() + " " + found.getSurname() + " | Email: " + found.getEmail() + " | Phone: " + found.getPhoneNumber());
            accountsList.getItems().clear();
            for (Account a : found.getAccounts()) {
                accountsList.getItems().add(a.getAccountId() + " | " + a.getAccountType() + " | BWP " + String.format("%.2f", a.getBalance()));
            }
        });

        contentPanel.getChildren().addAll(titleLabel, emailField, idField, searchBtn, resultLabel, accountsList);
        mainContainer.getChildren().addAll(headerBox, contentPanel);

        Scene scene = new Scene(mainContainer, 900, 500);
        primaryStage.setTitle("Meridian Bank - Verify Customer");
        primaryStage.setScene(scene);
    }

    private void showManageAccountsScreen() {
        if (!requireRole(Role.ADMIN)) return;
        VBox mainContainer = new VBox(15);
        mainContainer.setPadding(new Insets(0));
        mainContainer.setStyle("-fx-background-color: #0a0e27;");

        HBox headerBox = new HBox(20);
        headerBox.setPadding(new Insets(25));
        headerBox.setStyle("-fx-background-color: linear-gradient(to right, #c0392b, #8b0000); " +
                "-fx-border-color: #e74c3c; -fx-border-width: 0 0 2 0;");
        headerBox.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label("💼 MANAGE ACCOUNTS");
        titleLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #e74c3c;");

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button backButton = new Button("← BACK");
        backButton.setPrefWidth(100);
        backButton.setPrefHeight(40);
        backButton.setStyle("-fx-background-color: linear-gradient(to right, #e74c3c, #c0392b); " +
                "-fx-text-fill: white; -fx-font-weight: bold; -fx-border-radius: 6; -fx-cursor: hand;");
        backButton.setOnAction(e -> showAdminDashboard());

        headerBox.getChildren().addAll(titleLabel, spacer, backButton);

        VBox contentPanel = new VBox(15);
        contentPanel.setPadding(new Insets(20));
        contentPanel.setStyle("-fx-background-color: #0a0e27;");

        TableView<Account> accountsTable = new TableView<>();
        accountsTable.setStyle("-fx-background-color: #0f1433; -fx-control-inner-background: #0f1433; " +
                "-fx-text-fill: #e0e0e0; -fx-border-color: #e74c3c; -fx-border-width: 1;");

        TableColumn<Account, String> accountNumColumn = new TableColumn<>("Account Number");
        accountNumColumn.setPrefWidth(150);
        accountNumColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getAccountNumber()));

        TableColumn<Account, String> typeColumn = new TableColumn<>("Account Type");
        typeColumn.setPrefWidth(130);
        typeColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getAccountType()));

        TableColumn<Account, String> balanceColumn = new TableColumn<>("Balance (BWP)");
        balanceColumn.setPrefWidth(120);
        balanceColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(String.format("%.2f", cellData.getValue().getBalance())));

        TableColumn<Account, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setPrefWidth(100);
        statusColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().isActive() ? "🟢 ACTIVE" : "🔴 INACTIVE"));

        TableColumn<Account, String> createdColumn = new TableColumn<>("Created");
        createdColumn.setPrefWidth(120);
        createdColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDateOpened().toString()));

        TableColumn<Account, Void> accountActionColumn = new TableColumn<>("Action");
        accountActionColumn.setPrefWidth(120);
        accountActionColumn.setCellFactory(param -> new TableCell<Account, Void>() {
            private final Button deleteBtn = new Button("Delete");
            {
                deleteBtn.setStyle("-fx-padding: 5; -fx-font-size: 11; -fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand;");
                deleteBtn.setOnAction(e -> {
                    Account acc = getTableView().getItems().get(getIndex());
                    if (acc == null) return;
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                    confirm.setTitle("Confirm Delete");
                    confirm.setHeaderText("Delete Account: " + acc.getAccountId());
                    confirm.setContentText("Are you sure you want to permanently delete this account?");
                    java.util.Optional<javafx.scene.control.ButtonType> result = confirm.showAndWait();
                    if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {
                        boolean ok = bank.deleteAccount(acc.getAccountId());
                        if (ok) {
                            getTableView().getItems().remove(acc);
                            showAlert("Success", "Account Deleted", "Account has been deleted.", true);
                            try {
                                String actorId = currentUser != null ? currentUser.getUserId() : null;
                                String actorEmail = currentUser != null ? currentUser.getUsername() : null;
                                bank.logAction(actorId, actorEmail, "DELETE_ACCOUNT", "ACCOUNT", acc.getAccountId(), "Deleted account", "OK");
                            } catch (Exception ex) {}
                        } else {
                            showAlert("Error", "Delete Failed", "Could not delete account.", false);
                        }
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() < 0 || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteBtn);
                }
            }
        });

        accountsTable.getColumns().addAll(accountNumColumn, typeColumn, balanceColumn, statusColumn, createdColumn, accountActionColumn);

        List<Account> allAccounts = bank.getAllAccounts();
        accountsTable.getItems().addAll(allAccounts);

        double totalBalance = allAccounts.stream().mapToDouble(Account::getBalance).sum();
        long activeAccounts = allAccounts.stream().filter(Account::isActive).count();

        HBox statsBox = new HBox(30);
        statsBox.setPadding(new Insets(15));
        statsBox.setStyle("-fx-background-color: #0f1433; -fx-border-color: #e74c3c; -fx-border-width: 1; -fx-border-radius: 4;");

        Label totalAccountsLabel = new Label("Total Accounts: " + allAccounts.size());
        totalAccountsLabel.setStyle("-fx-font-size: 12; -fx-font-weight: bold; -fx-text-fill: #e74c3c;");

        Label activeAccountsLabel = new Label("Active Accounts: " + activeAccounts);
        activeAccountsLabel.setStyle("-fx-font-size: 12; -fx-font-weight: bold; -fx-text-fill: #1abc9c;");

        Label totalBalanceLabel = new Label("Total Balance: BWP " + String.format("%.2f", totalBalance));
        totalBalanceLabel.setStyle("-fx-font-size: 12; -fx-font-weight: bold; -fx-text-fill: #f39c12;");

        statsBox.getChildren().addAll(totalAccountsLabel, activeAccountsLabel, totalBalanceLabel);

        contentPanel.getChildren().addAll(statsBox, accountsTable);
        VBox.setVgrow(accountsTable, Priority.ALWAYS);

        mainContainer.getChildren().addAll(headerBox, contentPanel);

        Scene scene = new Scene(mainContainer, 1000, 600);
        primaryStage.setTitle("Meridian Bank - Manage Accounts");
        primaryStage.setScene(scene);
    }

    private void showSystemStatisticsScreen() {
        if (!requireRole(Role.ADMIN)) return;
        VBox mainContainer = new VBox(15);
        mainContainer.setPadding(new Insets(0));
        mainContainer.setStyle("-fx-background-color: #0a0e27;");

        HBox headerBox = new HBox(20);
        headerBox.setPadding(new Insets(25));
        headerBox.setStyle("-fx-background-color: linear-gradient(to right, #c0392b, #8b0000); " +
                "-fx-border-color: #e74c3c; -fx-border-width: 0 0 2 0;");
        headerBox.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label("📊 SYSTEM STATISTICS");
        titleLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #e74c3c;");

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button backButton = new Button("← BACK");
        backButton.setPrefWidth(100);
        backButton.setPrefHeight(40);
        backButton.setStyle("-fx-background-color: linear-gradient(to right, #e74c3c, #c0392b); " +
                "-fx-text-fill: white; -fx-font-weight: bold; -fx-border-radius: 6; -fx-cursor: hand;");
        backButton.setOnAction(e -> showAdminDashboard());

        headerBox.getChildren().addAll(titleLabel, spacer, backButton);

        VBox contentPanel = new VBox(20);
        contentPanel.setPadding(new Insets(30));
        contentPanel.setStyle("-fx-background-color: #0a0e27;");

        List<Customer> allCustomers = bank.getAllCustomers();
        List<Account> allAccounts = bank.getAllAccounts();

        double totalBalance = allAccounts.stream().mapToDouble(Account::getBalance).sum();
        long activeAccounts = allAccounts.stream().filter(Account::isActive).count();
        long customerCount = allCustomers.size();

        // Create statistics cards
        VBox statsContainer = new VBox(15);

        HBox row1 = new HBox(20);
        row1.setAlignment(Pos.CENTER_LEFT);

        VBox card1 = createStatCard("👥 TOTAL CUSTOMERS", String.valueOf(customerCount), "#1abc9c");
        VBox card2 = createStatCard("💼 TOTAL ACCOUNTS", String.valueOf(allAccounts.size()), "#f39c12");
        VBox card3 = createStatCard("🟢 ACTIVE ACCOUNTS", String.valueOf(activeAccounts), "#27ae60");

        row1.getChildren().addAll(card1, card2, card3);

        HBox row2 = new HBox(20);
        row2.setAlignment(Pos.CENTER_LEFT);

        VBox card4 = createStatCard("💰 TOTAL BALANCE", "BWP " + String.format("%.2f", totalBalance), "#e74c3c");
        VBox card5 = createStatCard("📝 SYSTEM DATA", String.valueOf(allAccounts.size()), "#9b59b6");
        VBox card6 = createStatCard("⚙️ SYSTEM STATUS", "🟢 OPERATIONAL", "#1abc9c");

        row2.getChildren().addAll(card4, card5, card6);

        statsContainer.getChildren().addAll(row1, row2);

        // Account Type Breakdown
        VBox breakdownBox = new VBox(10);
        breakdownBox.setPadding(new Insets(20));
        breakdownBox.setStyle("-fx-background-color: #0f1433; -fx-border-color: #e74c3c; -fx-border-width: 1; -fx-border-radius: 4;");

        Label breakdownTitle = new Label("📈 ACCOUNT TYPE BREAKDOWN");
        breakdownTitle.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #e74c3c;");

        long savingsCount = allAccounts.stream().filter(a -> a.getAccountType().equals("Savings Account")).count();
        long investmentCount = allAccounts.stream().filter(a -> a.getAccountType().equals("Investment Account")).count();
        long chequeCount = allAccounts.stream().filter(a -> a.getAccountType().equals("Cheque Account")).count();

        double savingsBalance = allAccounts.stream()
                .filter(a -> a.getAccountType().equals("Savings Account"))
                .mapToDouble(Account::getBalance).sum();
        double investmentBalance = allAccounts.stream()
                .filter(a -> a.getAccountType().equals("Investment Account"))
                .mapToDouble(Account::getBalance).sum();
        double chequeBalance = allAccounts.stream()
                .filter(a -> a.getAccountType().equals("Cheque Account"))
                .mapToDouble(Account::getBalance).sum();

        Label savingsLabel = new Label("💾 Savings Accounts: " + savingsCount + " accounts | Balance: BWP " + String.format("%.2f", savingsBalance));
        savingsLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #1abc9c; -fx-padding: 8;");

        Label investmentLabel = new Label("📊 Investment Accounts: " + investmentCount + " accounts | Balance: BWP " + String.format("%.2f", investmentBalance));
        investmentLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #f39c12; -fx-padding: 8;");

        Label chequeLabel = new Label("✓ Cheque Accounts: " + chequeCount + " accounts | Balance: BWP " + String.format("%.2f", chequeBalance));
        chequeLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #27ae60; -fx-padding: 8;");

        breakdownBox.getChildren().addAll(breakdownTitle, savingsLabel, investmentLabel, chequeLabel);

        contentPanel.getChildren().addAll(statsContainer, breakdownBox);

        ScrollPane scrollPane = new ScrollPane(contentPanel);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: #0a0e27; -fx-control-inner-background: #0a0e27;");

        mainContainer.getChildren().addAll(headerBox, scrollPane);

        Scene scene = new Scene(mainContainer, 1000, 700);
        primaryStage.setTitle("Meridian Bank - System Statistics");
        primaryStage.setScene(scene);
    }

    private void showAuditLogScreen() {
        if (!requireRole(Role.ADMIN)) return;
        VBox mainContainer = new VBox(12);
        mainContainer.setPadding(new Insets(10));
        mainContainer.setStyle("-fx-background-color: #0a0e27;");

        HBox header = new HBox(10);
        header.setPadding(new Insets(15));
        Label title = new Label("📝 SYSTEM AUDIT LOGS");
        title.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #e74c3c;");
        HBox spacer = new HBox(); HBox.setHgrow(spacer, Priority.ALWAYS);
        Button back = new Button("← BACK");
        back.setOnAction(e -> showAdminDashboard());
        header.getChildren().addAll(title, spacer, back);

        TextField searchField = new TextField();
        searchField.setPromptText("Filter by actor, action or target...");
        searchField.setStyle("-fx-padding: 8; -fx-background-color: #151a35; -fx-text-fill: #e0e0e0;");

        TableView<com.banking.model.AuditLog> table = new TableView<>();
        table.setStyle("-fx-background-color: #0f1433; -fx-text-fill: #e0e0e0;");

        TableColumn<com.banking.model.AuditLog, String> tsCol = new TableColumn<>("Timestamp");
        tsCol.setPrefWidth(180);
        tsCol.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue().getTimestamp() != null ? cd.getValue().getTimestamp().toString() : ""));

        TableColumn<com.banking.model.AuditLog, String> actorCol = new TableColumn<>("Actor");
        actorCol.setPrefWidth(180);
        actorCol.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty((cd.getValue().getActorEmail() != null ? cd.getValue().getActorEmail() : "") + (cd.getValue().getActorId() != null ? " (" + cd.getValue().getActorId() + ")" : "")));

        TableColumn<com.banking.model.AuditLog, String> actionCol = new TableColumn<>("Action");
        actionCol.setPrefWidth(140);
        actionCol.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue().getActionType()));

        TableColumn<com.banking.model.AuditLog, String> targetCol = new TableColumn<>("Target");
        targetCol.setPrefWidth(140);
        targetCol.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty((cd.getValue().getTargetType() != null ? cd.getValue().getTargetType() : "") + ": " + (cd.getValue().getTargetId() != null ? cd.getValue().getTargetId() : "")));

        TableColumn<com.banking.model.AuditLog, String> statusCol = new TableColumn<>("Status");
        statusCol.setPrefWidth(100);
        statusCol.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue().getStatus()));

        TableColumn<com.banking.model.AuditLog, String> detailsCol = new TableColumn<>("Details");
        detailsCol.setPrefWidth(300);
        detailsCol.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue().getDetails()));

        table.getColumns().addAll(tsCol, actorCol, actionCol, targetCol, statusCol, detailsCol);

        List<com.banking.model.AuditLog> logs = bank.getAuditLogs();
        table.getItems().addAll(logs);

        // Simple client-side filter
        searchField.textProperty().addListener((obs, oldV, newV) -> {
            String q = newV == null ? "" : newV.toLowerCase();
            table.getItems().clear();
            for (com.banking.model.AuditLog l : logs) {
                if (l.getActorEmail() != null && l.getActorEmail().toLowerCase().contains(q) ||
                    (l.getActionType() != null && l.getActionType().toLowerCase().contains(q)) ||
                    (l.getTargetId() != null && l.getTargetId().toLowerCase().contains(q)) ||
                    (l.getDetails() != null && l.getDetails().toLowerCase().contains(q))) {
                    table.getItems().add(l);
                }
            }
        });

        VBox.setVgrow(table, Priority.ALWAYS);
        mainContainer.getChildren().addAll(header, searchField, table);

        Scene scene = new Scene(mainContainer, 1100, 600);
        primaryStage.setTitle("Meridian Bank - Audit Logs");
        primaryStage.setScene(scene);
    }

    private VBox createStatCard(String title, String value, String color) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: #0f1433; -fx-border-color: " + color + "; " +
                "-fx-border-width: 2; -fx-border-radius: 6;");
        card.setPrefWidth(200);
        card.setPrefHeight(120);
        card.setAlignment(Pos.CENTER);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 12; -fx-text-fill: " + color + "; -fx-font-weight: bold;");

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 24; -fx-text-fill: " + color + "; -fx-font-weight: bold;");

        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }

    private void showCreateTellerScreen() {
        if (!requireRole(Role.ADMIN)) return;
        VBox mainContainer = new VBox(15);
        mainContainer.setPadding(new Insets(0));
        mainContainer.setStyle("-fx-background-color: #0a0e27;");

        HBox headerBox = new HBox(20);
        headerBox.setPadding(new Insets(25));
        headerBox.setStyle("-fx-background-color: linear-gradient(to right, #2980b9, #1f618d); " +
                "-fx-border-color: #2980b9; -fx-border-width: 0 0 2 0;");
        headerBox.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label("➕ CREATE NEW TELLER ACCOUNT");
        titleLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #2980b9;");

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button backButton = new Button("← BACK");
        backButton.setPrefWidth(100);
        backButton.setPrefHeight(40);
        backButton.setStyle("-fx-background-color: linear-gradient(to right, #2980b9, #1f618d); " +
                "-fx-text-fill: white; -fx-font-weight: bold; -fx-border-radius: 6; -fx-cursor: hand;");
        backButton.setOnAction(e -> showAdminDashboard());

        headerBox.getChildren().addAll(titleLabel, spacer, backButton);

        VBox contentPanel = new VBox(12);
        contentPanel.setPadding(new Insets(30));
        contentPanel.setStyle("-fx-background-color: #0a0e27;");

        TextField firstNameField = createStyledTextField("First Name");
        TextField lastNameField = createStyledTextField("Last Name");
        TextField emailField = createStyledTextField("Email Address");
        TextField phoneField = createStyledTextField("Phone Number (7[1-5]XXXXXX)");
        PasswordField passwordField = createStyledPasswordField("Password (min 6 chars)");
        PasswordField confirmPasswordField = createStyledPasswordField("Confirm Password");

        Label messageLabel = new Label();
        messageLabel.setWrapText(true);
        messageLabel.setStyle("-fx-font-size: 11; -fx-padding: 10; -fx-background-color: rgba(0,0,0,0.3); -fx-border-radius: 4;");

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        Button createButton = new Button("CREATE TELLER");
        createButton.setPrefWidth(150);
        createButton.setPrefHeight(40);
        createButton.setStyle("-fx-background-color: linear-gradient(to right, #2980b9, #1f618d); " +
                "-fx-text-fill: white; -fx-font-weight: bold; -fx-border-radius: 6; -fx-cursor: hand;");

        Button cancelButton = new Button("CANCEL");
        cancelButton.setPrefWidth(150);
        cancelButton.setPrefHeight(40);
        cancelButton.setStyle("-fx-background-color: #1a1f3a; -fx-text-fill: #2980b9; " +
                "-fx-border-color: #2980b9; -fx-border-width: 2; -fx-border-radius: 6; -fx-cursor: hand;");

        createButton.setOnAction(e -> {
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String pwd = passwordField.getText();
            String confirmPwd = confirmPasswordField.getText();

            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                messageLabel.setText("⚠ All fields are required");
                messageLabel.setStyle("-fx-text-fill: #f39c12; -fx-padding: 10; -fx-background-color: rgba(243,156,18,0.1);");
                return;
            }

            String pwd_err = com.banking.util.PasswordUtil.getPasswordValidationError(pwd);
            if (pwd_err != null) {
                messageLabel.setText("✗ " + pwd_err);
                messageLabel.setStyle("-fx-text-fill: #e74c3c; -fx-padding: 10; -fx-background-color: rgba(231,76,60,0.1);");
                return;
            }

            if (!pwd.equals(confirmPwd)) {
                messageLabel.setText("✗ Passwords do not match");
                messageLabel.setStyle("-fx-text-fill: #e74c3c; -fx-padding: 10; -fx-background-color: rgba(231,76,60,0.1);");
                return;
            }

            if (authController.registerUser(firstName, lastName, email, phone, "Meridian Bank Botswana", Role.TELLER)) {
                messageLabel.setText("✓ Teller account created successfully!");
                messageLabel.setStyle("-fx-text-fill: #1abc9c; -fx-padding: 10; -fx-background-color: rgba(26,188,156,0.1);");
                firstNameField.clear();
                lastNameField.clear();
                emailField.clear();
                phoneField.clear();
                passwordField.clear();
                confirmPasswordField.clear();
                try { Thread.sleep(1500); } catch (InterruptedException ex) {}
                showAdminDashboard();
            } else {
                messageLabel.setText("✗ Failed to create teller. Email may already exist.");
                messageLabel.setStyle("-fx-text-fill: #e74c3c; -fx-padding: 10; -fx-background-color: rgba(231,76,60,0.1);");
            }
        });

        cancelButton.setOnAction(e -> showAdminDashboard());

        buttonBox.getChildren().addAll(createButton, cancelButton);

        contentPanel.getChildren().addAll(
            createStyledLabel("FIRST NAME"), firstNameField,
            createStyledLabel("LAST NAME"), lastNameField,
            createStyledLabel("EMAIL"), emailField,
            createStyledLabel("PHONE"), phoneField,
            createStyledLabel("PASSWORD"), passwordField,
            createStyledLabel("CONFIRM PASSWORD"), confirmPasswordField,
            messageLabel, buttonBox
        );

        mainContainer.getChildren().addAll(headerBox, contentPanel);

        Scene scene = new Scene(mainContainer, 700, 750);
        primaryStage.setTitle("Meridian Bank - Create Teller");
        primaryStage.setScene(scene);
    }

    private void showCustomerDashboard() {
        if (!requireRole(Role.CUSTOMER)) return;
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
                } else if (fromAccount.getAccountId() != null && fromAccount.getAccountId().equals(toAccount.getAccountId())) {
                    messageLabel.setText("⚠ Cannot transfer to the same account");
                    messageLabel.setStyle("-fx-text-fill: #f39c12;");
        } else if (accountController.transferFunds(
            currentUser.getUserId(),
            fromAccount.getAccountId(),
            toAccount.getAccountId(),
            amount,
            description)) {
                    messageLabel.setText("✓ Transfer successful! Returning to dashboard...");
                    messageLabel.setStyle("-fx-text-fill: #1abc9c;");
                    amountField.clear();
                    descriptionField.clear();
                    try { Thread.sleep(1500); } catch (InterruptedException ex) {}
                    showDashboard();
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
        accountTypeCombo.getItems().addAll("Cheque", "Savings", "Investment");
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
        field.setStyle("-fx-padding: 12; -fx-font-size: 12; -fx-background-color: #ffffff; " +
                "-fx-text-fill: #1a1a1a; -fx-control-inner-background: #ffffff; -fx-border-color: #cccccc; " +
                "-fx-border-width: 2; -fx-border-radius: 6;");
        field.setPrefWidth(300);
        return field;
    }

    private PasswordField createStyledPasswordField(String prompt) {
        PasswordField field = new PasswordField();
        field.setPromptText(prompt);
        field.setStyle("-fx-padding: 12; -fx-font-size: 12; -fx-background-color: #ffffff; " +
                "-fx-text-fill: #1a1a1a; -fx-control-inner-background: #ffffff; -fx-border-color: #cccccc; " +
                "-fx-border-width: 2; -fx-border-radius: 6;");
        field.setPrefWidth(300);
        return field;
    }

    private Label createStyledLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 11; -fx-text-fill: #666666; -fx-letter-spacing: 1;");
        return label;
    }

    public static void main(String[] args) {
        launch(args);
    }
}