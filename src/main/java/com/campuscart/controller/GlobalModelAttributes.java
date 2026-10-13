package com.campuscart.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributes {

    @ModelAttribute("isAuthenticated")
    public boolean isAuthenticated() {
        Authentication authentication =
            SecurityContextHolder.getContext().getAuthentication();

        return authentication != null
            && authentication.isAuthenticated()
            && !(authentication instanceof AnonymousAuthenticationToken);
    }

    @ModelAttribute("currentEmail")
    public String currentEmail() {
        Authentication authentication =
            SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
            || !authentication.isAuthenticated()
            || authentication instanceof AnonymousAuthenticationToken) {
            return "";
        }

        return authentication.getName();
    }

    @ModelAttribute("isAdmin")
    public boolean isAdmin() {
        Authentication authentication =
            SecurityContextHolder.getContext().getAuthentication();

        return authentication != null
            && authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }
}
