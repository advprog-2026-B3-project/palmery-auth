package id.ac.ui.cs.advprog.authservice.model;

import java.util.UUID;

public class User {
    private String id;
    private String name;
    private String email;
    private String passwordHash;
    private String role;

    public User() {
        this.id = UUID.randomUUID().toString();
    }

    public User(String id, String name, String email, String passwordHash, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public User(String name, String email, String passwordHash, String role) {
        this(UUID.randomUUID().toString(), name, email, passwordHash, role);
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
