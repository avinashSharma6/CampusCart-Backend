package com.campuscart.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ProfileForm {

    @NotBlank(message = "Please enter your full name.")
    @Size(max = 100, message = "Name must be 100 characters or fewer.")
    private String fullName;

    @Size(max = 20, message = "Phone number must be 20 characters or fewer.")
    @Pattern(
        regexp = "^$|^[+()0-9 .-]{7,20}$",
        message = "Enter a valid phone number."
    )
    private String phoneNumber = "";

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
