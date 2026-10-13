package com.campuscart.controller;

import com.campuscart.dto.ListingForm;
import com.campuscart.dto.ReportForm;
import com.campuscart.entity.Category;
import com.campuscart.entity.Listing;
import com.campuscart.enums.ListingCondition;
import com.campuscart.enums.ListingStatus;
import com.campuscart.repository.CategoryRepository;
import com.campuscart.repository.ListingRepository;
import com.campuscart.service.ListingService;
import com.campuscart.service.ReportService;
import com.campuscart.service.WhatsAppLinkService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/listings")
public class ListingController {

    private final ListingRepository listingRepository;
    private final CategoryRepository categoryRepository;
    private final ListingService listingService;
    private final ReportService reportService;
    private final WhatsAppLinkService whatsAppLinkService;

    public ListingController(
        ListingRepository listingRepository,
        CategoryRepository categoryRepository,
        ListingService listingService,
        ReportService reportService,
        WhatsAppLinkService whatsAppLinkService
    ) {
        this.listingRepository = listingRepository;
        this.categoryRepository = categoryRepository;
        this.listingService = listingService;
        this.reportService = reportService;
        this.whatsAppLinkService = whatsAppLinkService;
    }

    @GetMapping
    public String browseListings(
        @RequestParam(required = false) String q,
        @RequestParam(required = false) Long categoryId,
        @RequestParam(defaultValue = "newest") String sort,
        @RequestParam(defaultValue = "0") int page,
        Model model
    ) {
        int safePage = Math.max(page, 0);
        String safeQuery = q == null ? "" : q.trim();
        String safeSort = isValidSort(sort) ? sort : "newest";

        Pageable pageable = PageRequest.of(
            safePage,
            12,
            getSortOrder(safeSort)
        );

        Page<Listing> listingPage =
            listingRepository.findAvailableListings(
                safeQuery,
                categoryId,
                ListingStatus.AVAILABLE,
                pageable
            );

        List<Category> categories =
            categoryRepository.findAllByOrderByNameAsc();

        model.addAttribute("listings", listingPage.getContent());
        model.addAttribute("categories", categories);
        model.addAttribute("searchQuery", safeQuery);
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("selectedSort", safeSort);
        model.addAttribute("currentPage", safePage);
        model.addAttribute("totalPages", listingPage.getTotalPages());
        model.addAttribute("totalItems", listingPage.getTotalElements());

        return "listings/index";
    }

    private boolean isValidSort(String sort) {
        return sort != null && List.of(
            "newest",
            "oldest",
            "priceLow",
            "priceHigh"
        ).contains(sort);
    }

    private Sort getSortOrder(String sort) {
        return switch (sort) {
            case "oldest" -> Sort.by(
                Sort.Order.asc("createdAt"),
                Sort.Order.asc("id")
            );

            case "priceLow" -> Sort.by(
                Sort.Order.asc("price"),
                Sort.Order.desc("createdAt"),
                Sort.Order.desc("id")
            );

            case "priceHigh" -> Sort.by(
                Sort.Order.desc("price"),
                Sort.Order.desc("createdAt"),
                Sort.Order.desc("id")
            );

            default -> Sort.by(
                Sort.Order.desc("createdAt"),
                Sort.Order.desc("id")
            );
        };
    }

    @GetMapping("/{id}")
    public String viewListing(
        @PathVariable Long id,
        Authentication authentication,
        Model model,
        HttpServletResponse response
    ) {
        Listing listing = listingRepository.findByIdAndStatus(
            id,
            ListingStatus.AVAILABLE
        ).orElse(null);

        if (listing == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return "error/not-found";
        }

        boolean signedIn = authentication != null
            && authentication.isAuthenticated()
            && !"anonymousUser".equals(authentication.getName());

        boolean isOwner = signedIn
            && listing.getOwner().getEmail() != null
            && listing.getOwner().getEmail()
                .equalsIgnoreCase(authentication.getName());

        boolean canReport = signedIn && !isOwner;

        String whatsAppUrl = signedIn && !isOwner
            ? whatsAppLinkService.createLink(
                listing.getContactPhone(),
                listing.getTitle()
            )
            : null;

        model.addAttribute("listing", listing);
        model.addAttribute("canReport", canReport);
        model.addAttribute("whatsAppUrl", whatsAppUrl);
        model.addAttribute("hasWhatsAppContact", whatsAppUrl != null);
        model.addAttribute("reportForm", new ReportForm());
        model.addAttribute("reportReasons", List.of(
            "Suspicious or scam",
            "Prohibited item",
            "Incorrect information",
            "Duplicate listing",
            "Other"
        ));

        return "listings/detail";
    }

    @PostMapping("/{id}/report")
    public String reportListing(
        @PathVariable Long id,
        @Valid @ModelAttribute("reportForm") ReportForm reportForm,
        BindingResult bindingResult,
        Authentication authentication,
        RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(
                "errorMessage",
                "Please provide a valid report reason and description."
            );

            return "redirect:/listings/" + id;
        }

        try {
            reportService.submitReport(
                id,
                authentication.getName(),
                reportForm
            );

            redirectAttributes.addFlashAttribute(
                "successMessage",
                "Your report has been submitted for administrator review."
            );
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute(
                "errorMessage",
                exception.getMessage()
            );
        }

        return "redirect:/listings/" + id;
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("listingForm", new ListingForm());
        prepareFormModel(model, false, null, null);
        return "listings/form";
    }

    @PostMapping
    public String createListing(
        @Valid @ModelAttribute("listingForm") ListingForm listingForm,
        BindingResult bindingResult,
        @RequestParam(value = "image", required = false) MultipartFile image,
        Authentication authentication,
        Model model,
        RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            prepareFormModel(model, false, null, null);
            return "listings/form";
        }

        try {
            Listing listing = listingService.createListing(
                authentication.getName(),
                listingForm,
                image
            );

            redirectAttributes.addFlashAttribute(
                "successMessage",
                "Your listing has been published!"
            );

            return "redirect:/listings/" + listing.getId();
        } catch (IllegalArgumentException exception) {
            model.addAttribute("formError", exception.getMessage());
            prepareFormModel(model, false, null, null);
            return "listings/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(
        @PathVariable Long id,
        Authentication authentication,
        Model model
    ) {
        Listing listing = listingService.findOwnedListing(
            id,
            authentication.getName()
        );

        if (listing.getStatus() != ListingStatus.AVAILABLE) {
            throw new IllegalArgumentException(
                "Only available listings can be edited."
            );
        }

        ListingForm form = new ListingForm();
        form.setTitle(listing.getTitle());
        form.setDescription(listing.getDescription());
        form.setPrice(listing.getPrice());
        form.setCategoryId(listing.getCategory().getId());
        form.setCondition(listing.getCondition());
        form.setBrand(listing.getBrand());
        form.setContactPhone(listing.getContactPhone());
        form.setMeetupLocation(listing.getMeetupLocation());

        model.addAttribute("listingForm", form);

        prepareFormModel(
            model,
            true,
            listing.getId(),
            listing.getImagePath()
        );

        return "listings/form";
    }

    @PostMapping("/{id}/edit")
    public String updateListing(
        @PathVariable Long id,
        @Valid @ModelAttribute("listingForm") ListingForm listingForm,
        BindingResult bindingResult,
        @RequestParam(value = "image", required = false) MultipartFile image,
        Authentication authentication,
        Model model,
        RedirectAttributes redirectAttributes
    ) {
        Listing existingListing = listingService.findOwnedListing(
            id,
            authentication.getName()
        );

        if (bindingResult.hasErrors()) {
            prepareFormModel(
                model,
                true,
                id,
                existingListing.getImagePath()
            );

            return "listings/form";
        }

        try {
            listingService.updateListing(
                id,
                authentication.getName(),
                listingForm,
                image
            );

            redirectAttributes.addFlashAttribute(
                "successMessage",
                "Your listing has been updated."
            );

            return "redirect:/dashboard";
        } catch (IllegalArgumentException exception) {
            model.addAttribute("formError", exception.getMessage());

            prepareFormModel(
                model,
                true,
                id,
                existingListing.getImagePath()
            );

            return "listings/form";
        }
    }

    @PostMapping("/{id}/sold")
    public String markListingSold(
        @PathVariable Long id,
        Authentication authentication,
        RedirectAttributes redirectAttributes
    ) {
        try {
            listingService.markAsSold(id, authentication.getName());

            redirectAttributes.addFlashAttribute(
                "successMessage",
                "Your listing has been marked as sold."
            );
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute(
                "errorMessage",
                exception.getMessage()
            );
        }

        return "redirect:/dashboard";
    }

    @PostMapping("/{id}/delete")
    public String deleteListing(
        @PathVariable Long id,
        Authentication authentication,
        RedirectAttributes redirectAttributes
    ) {
        try {
            listingService.deleteListing(id, authentication.getName());
            redirectAttributes.addFlashAttribute(
                "successMessage",
                "Your listing has been permanently deleted."
            );
        } catch (org.springframework.security.access.AccessDeniedException exception) {
            redirectAttributes.addFlashAttribute(
                "errorMessage",
                "You are not allowed to delete this listing."
            );
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute(
                "errorMessage",
                exception.getMessage()
            );
        }

        return "redirect:/dashboard";
    }

    private void prepareFormModel(
        Model model,
        boolean isEdit,
        Long listingId,
        String existingImagePath
    ) {
        model.addAttribute(
            "categories",
            categoryRepository.findAllByOrderByNameAsc()
        );

        model.addAttribute("conditions", ListingCondition.values());
        model.addAttribute("isEdit", isEdit);
        model.addAttribute("listingId", listingId);
        model.addAttribute("existingImagePath", existingImagePath);
    }
}
