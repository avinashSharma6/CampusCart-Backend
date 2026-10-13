package com.campuscart.service;

import com.campuscart.entity.Listing;
import com.campuscart.entity.User;
import com.campuscart.entity.WishlistItem;
import com.campuscart.enums.ListingStatus;
import com.campuscart.repository.ListingRepository;
import com.campuscart.repository.UserRepository;
import com.campuscart.repository.WishlistRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final ListingRepository listingRepository;

    public WishlistService(
        WishlistRepository wishlistRepository,
        UserRepository userRepository,
        ListingRepository listingRepository
    ) {
        this.wishlistRepository = wishlistRepository;
        this.userRepository = userRepository;
        this.listingRepository = listingRepository;
    }

    @Transactional
    public boolean add(String email, Long listingId) {
        User user = findUser(email);

        Listing listing = listingRepository.findByIdAndStatus(
            listingId,
            ListingStatus.AVAILABLE
        ).orElseThrow(() -> new IllegalArgumentException(
            "This listing is no longer available."
        ));

        if (listing.getOwner().getId().equals(user.getId())) {
            throw new IllegalArgumentException(
                "You cannot save your own listing to your wishlist."
            );
        }

        if (wishlistRepository.existsByUser_IdAndListing_Id(
            user.getId(),
            listingId
        )) {
            return false;
        }

        WishlistItem item = new WishlistItem();
        item.setUser(user);
        item.setListing(listing);
        wishlistRepository.save(item);

        return true;
    }

    @Transactional(readOnly = true)
    public List<WishlistItem> getItems(String email) {
        User user = findUser(email);
        return wishlistRepository.findByUser_IdOrderByCreatedAtDesc(
            user.getId()
        );
    }

    @Transactional(readOnly = true)
    public boolean contains(String email, Long listingId) {
        User user = findUser(email);
        return wishlistRepository.existsByUser_IdAndListing_Id(
            user.getId(),
            listingId
        );
    }

    @Transactional(readOnly = true)
    public long count(String email) {
        User user = findUser(email);
        return wishlistRepository.countByUser_Id(user.getId());
    }

    @Transactional
    public void remove(String email, Long listingId) {
        User user = findUser(email);
        wishlistRepository.deleteByUser_IdAndListing_Id(
            user.getId(),
            listingId
        );
    }

    private User findUser(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                "Please log in to use your wishlist."
            );
        }

        return userRepository.findByEmailIgnoreCase(email.trim())
            .orElseThrow(() -> new IllegalArgumentException(
                "Your account could not be found."
            ));
    }
}
