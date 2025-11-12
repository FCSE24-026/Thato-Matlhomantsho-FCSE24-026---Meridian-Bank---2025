package com.banking.main;

public class User {
    private int userId;
    private String username;
    private String password;
    private String email;
    private String phone;

    public User(int userId, String username, String password, String email, String phone) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
    }

    public User(String username, String password, String email, String phone) {
        this(-1, username, password, email, phone);
    }

    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public void setUserId(int userId) { this.userId = userId; }

    @Override
    public String toString() {
        return "User{userId=" + userId + ", username='" + username + "'}";
    }
}
