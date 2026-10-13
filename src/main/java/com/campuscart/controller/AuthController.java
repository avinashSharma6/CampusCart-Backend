package com.campuscart.controller;

import com.campuscart.dto.RegisterRequest;
import com.campuscart.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new RegisterRequest());
        }

        return "register";
    }

    @PostMapping("/register")
    public String register(
        @Valid @ModelAttribute("form") RegisterRequest form,
        BindingResult bindingResult,
        Model model,
        RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            return "register";
        }

        try {
            userService.register(form);
        } catch (IllegalArgumentException exception) {
            bindingResult.rejectValue(
                "email",
                "email.exists",
                exception.getMessage()
            );
            return "register";
        }

        redirectAttributes.addFlashAttribute(
            "registrationSuccess",
            "Account created successfully! You can now log in."
        );

        return "redirect:/login?registered";
    }

    @GetMapping("/login")
    public String showLoginForm(
        @RequestParam(name = "error", required = false) String error,
        @RequestParam(name = "registered", required = false) String registered,
        Model model
    ) {
        if (error != null) {
            model.addAttribute(
                "loginError",
                "Email or password is incorrect, or your account is disabled."
            );
        }

        if (registered != null) {
            model.addAttribute(
                "registrationSuccess",
                "Account created successfully! You can now log in."
            );
        }

        return "login";
    }
}
