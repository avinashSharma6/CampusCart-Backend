package com.campuscart.controller;

import com.campuscart.service.WishlistService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @GetMapping("/wishlist")
    public String wishlist(
        Authentication authentication,
        Model model,
        RedirectAttributes redirectAttributes
    ) {
        try {
            model.addAttribute(
                "wishlistItems",
                wishlistService.getItems(authentication.getName())
            );
            return "wishlist/index";
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute(
                "errorMessage",
                exception.getMessage()
            );
            return "redirect:/login";
        }
    }

    @PostMapping("/wishlist/{listingId}/add")
    public String add(
        @PathVariable Long listingId,
        Authentication authentication,
        RedirectAttributes redirectAttributes
    ) {
        try {
            boolean added = wishlistService.add(
                authentication.getName(),
                listingId
            );

            redirectAttributes.addFlashAttribute(
                added ? "successMessage" : "infoMessage",
                added
                    ? "Listing saved to your wishlist."
                    : "This listing is already in your wishlist."
            );
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute(
                "errorMessage",
                exception.getMessage()
            );
        }

        return "redirect:/listings/" + listingId;
    }

    @PostMapping("/wishlist/{listingId}/remove")
    public String remove(
        @PathVariable Long listingId,
        Authentication authentication,
        RedirectAttributes redirectAttributes
    ) {
        try {
            wishlistService.remove(
                authentication.getName(),
                listingId
            );

            redirectAttributes.addFlashAttribute(
                "successMessage",
                "Listing removed from your wishlist."
            );
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute(
                "errorMessage",
                exception.getMessage()
            );
        }

        return "redirect:/wishlist";
    }
}
