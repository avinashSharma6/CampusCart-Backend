package com.campuscart.dto;

import com.campuscart.enums.ListingCondition;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class ListingForm {

    @NotBlank(message = "Please enter a listing title.")
    @Size(max = 120, message = "The title must be 120 characters or fewer.")
    private String title;

    @NotBlank(message = "Please describe the item.")
    @Size(max = 3000, message = "The description must be 3000 characters or fewer.")
    private String description;

    @NotNull(message = "Please enter a price.")
    @DecimalMin(value = "0.01", message = "The price must be greater than zero.")
    @DecimalMax(
        value = "99999999.99",
        message = "The price cannot exceed 99,999,999.99."
    )
    private BigDecimal price;

    @NotNull(message = "Please choose a category.")
    private Long categoryId;

    @NotNull(message = "Please choose the item's condition.")
    private ListingCondition condition;

    @Size(max = 80, message = "The brand must be 80 characters or fewer.")
    private String brand;

    @Size(max = 30, message = "The phone number must be 30 characters or fewer.")
    @Pattern(
        regexp = "^$|^(?=.{7,30}$)(?=.*[0-9])[+()0-9 .-]+$",
        message = "Enter a valid phone number using digits and common phone-number symbols."
    )
    private String contactPhone;

    @NotBlank(message = "Please enter a campus meetup location.")
    @Size(max = 120, message = "The meetup location must be 120 characters or fewer.")
    private String meetupLocation;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public ListingCondition getCondition() {
        return condition;
    }

    public void setCondition(ListingCondition condition) {
        this.condition = condition;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getMeetupLocation() {
        return meetupLocation;
    }

    public void setMeetupLocation(String meetupLocation) {
        this.meetupLocation = meetupLocation;
    }
}
