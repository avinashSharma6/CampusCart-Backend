package com.campuscart.service;

import com.campuscart.dto.ListingForm;
import com.campuscart.entity.Category;
import com.campuscart.entity.Listing;
import com.campuscart.entity.User;
import com.campuscart.enums.ListingStatus;
import com.campuscart.repository.CategoryRepository;
import com.campuscart.repository.ListingRepository;
import com.campuscart.repository.ReportRepository;
import com.campuscart.repository.WishlistRepository;
import com.campuscart.repository.UserRepository;
import com.campuscart.storage.ImageStorageService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Locale;

@Service
public class ListingService {

    private final ListingRepository listingRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ImageStorageService imageStorageService;
    private final ReportRepository reportRepository;
    private final WishlistRepository wishlistRepository;

    public ListingService(
        ListingRepository listingRepository,
        CategoryRepository categoryRepository,
        UserRepository userRepository,
        ImageStorageService imageStorageService,
        ReportRepository reportRepository,
        WishlistRepository wishlistRepository
    ) {
        this.listingRepository = listingRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.imageStorageService = imageStorageService;
        this.reportRepository = reportRepository;
        this.wishlistRepository = wishlistRepository;
    }

    @Transactional
    public Listing createListing(
        String email,
        ListingForm form,
        MultipartFile image
    ) {
        User owner = findUser(email);
        Category category = findCategory(form.getCategoryId());

        Listing listing = new Listing();
        listing.setOwner(owner);
        listing.setCategory(category);
        applyForm(listing, form);

        listing.setStatus(ListingStatus.AVAILABLE);

        String imagePath = imageStorageService.store(image);
        if (imagePath != null) {
            listing.setImagePath(imagePath);
        }

        return listingRepository.save(listing);
    }

    @Transactional
    public Listing updateListing(
        Long listingId,
        String email,
        ListingForm form,
        MultipartFile image
    ) {
        Listing listing = findOwnedListing(listingId, email);

        if (listing.getStatus() != ListingStatus.AVAILABLE) {
            throw new IllegalArgumentException(
                "Only available listings can be edited."
            );
        }

        Category category = findCategory(form.getCategoryId());
        applyForm(listing, form);
        listing.setCategory(category);

        String imagePath = imageStorageService.store(image);
        if (imagePath != null) {
            listing.setImagePath(imagePath);
        }

        return listingRepository.save(listing);
    }

    @Transactional
    public Listing markAsSold(Long listingId, String email) {
        Listing listing = findOwnedListing(listingId, email);

        if (listing.getStatus() != ListingStatus.AVAILABLE) {
            throw new IllegalArgumentException(
                "This listing is no longer available."
            );
        }

        listing.setStatus(ListingStatus.SOLD);
        return listingRepository.save(listing);
    }

    @Transactional
    public void deleteListing(Long listingId, String email) {
        Listing listing = findOwnedListing(listingId, email);

        reportRepository.deleteAllForListing(listingId);
        wishlistRepository.deleteAllForListing(listingId);
        listingRepository.delete(listing);
    }

    @Transactional(readOnly = true)
    public Listing findOwnedListing(Long listingId, String email) {
        Listing listing = listingRepository.findById(listingId)
            .orElseThrow(() -> new IllegalArgumentException(
                "The requested listing could not be found."
            ));

        String ownerEmail = listing.getOwner().getEmail();

        if (ownerEmail == null ||
            !ownerEmail.equalsIgnoreCase(email)) {
            throw new AccessDeniedException(
                "You are not allowed to modify this listing."
            );
        }

        return listing;
    }

    private User findUser(String email) {
        return userRepository.findByEmailIgnoreCase(
            email.trim().toLowerCase(Locale.ROOT)
        ).orElseThrow(() -> new IllegalArgumentException(
            "Your user account could not be found."
        ));
    }

    private Category findCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
            .orElseThrow(() -> new IllegalArgumentException(
                "Please choose a valid category."
            ));
    }

    private void applyForm(Listing listing, ListingForm form) {
        listing.setTitle(form.getTitle().trim());
        listing.setDescription(form.getDescription().trim());
        listing.setPrice(form.getPrice());
        listing.setCondition(form.getCondition());
        listing.setBrand(
            form.getBrand() == null || form.getBrand().isBlank()
                ? null
                : form.getBrand().trim()
        );
        listing.setContactPhone(
            form.getContactPhone() == null || form.getContactPhone().isBlank()
                ? null
                : form.getContactPhone().trim()
        );
        listing.setMeetupLocation(form.getMeetupLocation().trim());
    }
}
