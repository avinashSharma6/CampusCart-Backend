package com.campuscart.controller;

import com.campuscart.entity.Listing;
import com.campuscart.entity.User;
import com.campuscart.enums.ListingStatus;
import com.campuscart.repository.ListingRepository;
import com.campuscart.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Locale;

@Controller
public class DashboardController {

    private final UserRepository userRepository;
    private final ListingRepository listingRepository;

    public DashboardController(
        UserRepository userRepository,
        ListingRepository listingRepository
    ) {
        this.userRepository = userRepository;
        this.listingRepository = listingRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(
        Authentication authentication,
        Model model
    ) {
        User user = userRepository.findByEmailIgnoreCase(
            authentication.getName().trim().toLowerCase(Locale.ROOT)
        ).orElseThrow(() -> new IllegalArgumentException(
            "Your user account could not be found."
        ));

        List<Listing> myListings =
            listingRepository.findByOwner_IdOrderByCreatedAtDesc(user.getId());

        long availableCount = myListings.stream()
            .filter(listing -> listing.getStatus() == ListingStatus.AVAILABLE)
            .count();

        long soldCount = myListings.stream()
            .filter(listing -> listing.getStatus() == ListingStatus.SOLD)
            .count();

        model.addAttribute("user", user);
        model.addAttribute("myListings", myListings);
        model.addAttribute("availableCount", availableCount);
        model.addAttribute("soldCount", soldCount);

        return "dashboard/index";
    }
}
