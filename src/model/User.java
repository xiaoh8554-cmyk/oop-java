package model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Abstract base class representing a generic user in APU Medical Centre.
 * Demonstrates Object-Oriented principles: Encapsulation, Abstraction, and Inheritance.
 */
public abstract class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String phoneNumber;
    private UserRole role;

    public User(String id, String username, String password, String fullName, String email, String phoneNumber, UserRole role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;
    }

    // Getters and Setters (Encapsulation)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }


    /**
     * Polymorphic method to get role-specific summary details.
     */
    public abstract String getRoleSpecificInfo();

    /**
     * Converts base user data into text format for parent table (users.txt).
     * Format: ROLE|ID|USERNAME|PASSWORD|FULL_NAME|EMAIL|PHONE|CREATED_AT
     */
    public String toBaseFileString() {
        return String.join("|",
                role.name(),
                id,
                username,
                password,
                fullName,
                email,
                phoneNumber
        );
    }

    /**
     * Converts role-specific child data into text format for child tables.
     * Format: ID|...childSpecificFields
     */
    public abstract String toChildFileString();

    /**
     * Converts user data into text format for monolithic file persistence (backwards compatibility).
     * Format: ROLE|id|username|password|fullName|email|phoneNumber|createdAt|...extraFields
     */
    public abstract String toFileString();

    @Override
    public String toString() {
        return String.format("[%s] %s (%s)", role.getDisplayName(), fullName, username);
    }
}
