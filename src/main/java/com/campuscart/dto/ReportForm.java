package com.campuscart.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ReportForm {

    @NotBlank(message = "Please select a reason.")
    @Size(max = 60, message = "The reason must be at most 60 characters.")
    private String reason;

    @Size(max = 1000, message = "The description must be at most 1000 characters.")
    private String description;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
