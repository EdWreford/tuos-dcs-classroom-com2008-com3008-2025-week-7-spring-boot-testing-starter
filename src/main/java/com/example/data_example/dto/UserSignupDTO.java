package com.example.data_example.dto;

import jakarta.validation.constraints.*;

public class UserSignupDTO {

    @NotNull(message = "cannot be null")
    @Size(min = 5, max = 30, message = "must be between 5 and 30 characters in length")
    @Pattern(regexp = "^[A-Za-z0-9_]+$", message = "only letters, digits and underscores allowed")
    private String username;

    @NotNull(message = "cannot be null")
    @Size(min = 8, message = "must be at least 8 characters in length")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).*$",
            message = "Password must contain at least one digit, one lowercase, one uppercase letter, one special character, and no spaces"
    )
    private String password;

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

}
