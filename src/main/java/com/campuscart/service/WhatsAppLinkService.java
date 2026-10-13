
package com.campuscart.service;

import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class WhatsAppLinkService {

    public String createLink(String phoneNumber, String listingTitle) {
        String normalizedNumber = normalizePhoneNumber(phoneNumber);

        if (normalizedNumber == null
                || listingTitle == null
                || listingTitle.isBlank()) {
            return null;
        }

        String message = "Hi, is the " + listingTitle
            + " listed on CampusCart still available?";

        String encodedMessage = URLEncoder.encode(
            message,
            StandardCharsets.UTF_8
        );

        return "https://wa.me/" + normalizedNumber
            + "?text=" + encodedMessage;
    }

    private String normalizePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isBlank()) {
            return null;
        }

        String input = phoneNumber.trim();
        boolean international = input.startsWith("+")
            || input.startsWith("00");

        String digits = input.replaceAll("\\D", "");

        if (digits.startsWith("00")) {
            digits = digits.substring(2);
        }

        if (international) {
            return isValidInternationalNumber(digits) ? digits : null;
        }

        if (digits.length() == 10
                && digits.matches("[6-9]\\d{9}")) {
            return "91" + digits;
        }

        if (digits.length() == 11
                && digits.startsWith("0")
                && digits.substring(1).matches("[6-9]\\d{9}")) {
            return "91" + digits.substring(1);
        }

        if (digits.length() == 12
                && digits.startsWith("91")
                && digits.substring(2).matches("[6-9]\\d{9}")) {
            return digits;
        }

        return null;
    }

    private boolean isValidInternationalNumber(String digits) {
        return digits != null
            && digits.matches("[1-9]\\d{7,14}");
    }
}

