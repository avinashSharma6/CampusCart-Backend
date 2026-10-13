package com.campuscart.controller;

import com.campuscart.entity.Listing;
import com.campuscart.enums.ListingStatus;
import com.campuscart.repository.CategoryRepository;
import com.campuscart.repository.ListingRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final ListingRepository listingRepository;
    private final CategoryRepository categoryRepository;

    public HomeController(
        ListingRepository listingRepository,
        CategoryRepository categoryRepository
    ) {
        this.listingRepository = listingRepository;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute(
            "categories",
            categoryRepository.findAllByOrderByNameAsc()
        );

        model.addAttribute(
            "latestListings",
            listingRepository.findByStatusOrderByCreatedAtDesc(
                ListingStatus.AVAILABLE,
                PageRequest.of(
                    0,
                    8,
                    Sort.by(Sort.Direction.DESC, "createdAt")
                )
            ).getContent()
        );

        return "home";
    }
}
