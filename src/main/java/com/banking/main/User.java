package com.banking.main;

public class User {
    private int userId;
    private String username;
    private String password;
    private String email;
    private String phone;
    private Role role;

    public User(int userId, String username, String password, String email, String phone, Role role) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.role = role;
    }

    public User(int userId, String username, String password, String email, String phone) {
        this(userId, username, password, email, phone, Role.CUSTOMER);
    }

    public User(String username, String password, String email, String phone) {
        this(-1, username, password, email, phone, Role.CUSTOMER);
    }

    public User(String username, String password, String email, String phone, Role role) {
        this(-1, username, password, email, phone, role);
    }

    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public Role getRole() { return role; }
    public void setUserId(int userId) { this.userId = userId; }

    @Override
    public String toString() {
        return "User{userId=" + userId + ", username='" + username + "'}";
    }
}
