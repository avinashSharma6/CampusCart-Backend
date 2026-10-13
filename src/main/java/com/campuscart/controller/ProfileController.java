package com.campuscart.controller;

import com.campuscart.dto.ProfileForm;
import com.campuscart.entity.User;
import com.campuscart.repository.UserRepository;
import jakarta.validation.Valid;
import java.util.Locale;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProfileController {

    private final UserRepository userRepository;

    public ProfileController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/profile")
    public String showProfile(
        Authentication authentication,
        Model model
    ) {
        User user = findCurrentUser(authentication.getName());

        ProfileForm form = new ProfileForm();
        form.setFullName(user.getFullName());
        form.setPhoneNumber(
            user.getPhoneNumber() == null ? "" : user.getPhoneNumber()
        );

        model.addAttribute("user", user);
        model.addAttribute("profileForm", form);
        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile(
        Authentication authentication,
        @Valid @ModelAttribute("profileForm") ProfileForm form,
        BindingResult bindingResult,
        Model model,
        RedirectAttributes redirectAttributes
    ) {
        User user = findCurrentUser(authentication.getName());

        if (bindingResult.hasErrors()) {
            model.addAttribute("user", user);
            return "profile";
        }

        user.setFullName(form.getFullName().trim());

        String phone = form.getPhoneNumber();
        user.setPhoneNumber(
            phone == null || phone.isBlank() ? null : phone.trim()
        );

        userRepository.save(user);

        redirectAttributes.addFlashAttribute(
            "successMessage",
            "Your profile has been updated."
        );

        return "redirect:/profile";
    }

    private User findCurrentUser(String email) {
        return userRepository.findByEmailIgnoreCase(
            email.trim().toLowerCase(Locale.ROOT)
        ).orElseThrow(() -> new IllegalArgumentException(
            "Your account could not be found."
        ));
    }
}
