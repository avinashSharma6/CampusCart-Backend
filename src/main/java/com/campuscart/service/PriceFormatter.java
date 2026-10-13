package com.campuscart.service;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

@Component("priceFormatter")
public class PriceFormatter {

    public String format(BigDecimal price) {
        if (price == null) {
            return "₹0.00";
        }

        NumberFormat formatter =
            NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN"));

        formatter.setCurrency(Currency.getInstance("INR"));
        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);

        return formatter.format(price);
    }
}
