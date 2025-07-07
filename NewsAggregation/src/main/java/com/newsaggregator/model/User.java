package com.newsaggregator.model;

public class User {
    private int user_id;
    private String username;
    private String email;
    private String password_hash;
    private String role;

    // Getters and Setters
    public int getId() { return user_id; }
    public void setId(int id) { this.user_id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password_hash; }
    public void setPassword(String password) { this.password_hash = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
