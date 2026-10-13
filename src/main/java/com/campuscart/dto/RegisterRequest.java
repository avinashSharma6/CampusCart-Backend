package com.campuscart.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

    @NotBlank(message = "Please enter your full name.")
    @Size(max = 100, message = "Name must be 100 characters or fewer.")
    private String fullName;

    @NotBlank(message = "Please enter your university email.")
    @Pattern(
        regexp = "^[0-9]{2}EG1(?:0[1-9]|1[0-2])[A-Z][0-9]{2}@anurag\\.edu\\.in$",
        flags = Pattern.Flag.CASE_INSENSITIVE,
        message = "Enter a valid Anurag University student email, such as 24EG101K01@anurag.edu.in."
    )
    private String email;

    @NotBlank(message = "Please enter a password.")
    @Size(min = 8, max = 72, message = "Password must be between 8 and 72 characters.")
    private String password;

    @NotBlank(message = "Please confirm your password.")
    private String confirmPassword;

    @Size(max = 20, message = "Phone number must be 20 characters or fewer.")
    private String phoneNumber;

    @AssertTrue(message = "The passwords do not match.")
    public boolean isPasswordMatching() {
        return password != null
            && confirmPassword != null
            && password.equals(confirmPassword);
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
