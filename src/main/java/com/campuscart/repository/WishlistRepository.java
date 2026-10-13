package com.campuscart.repository;

import com.campuscart.entity.WishlistItem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WishlistRepository extends JpaRepository<WishlistItem, Long> {

    @EntityGraph(attributePaths = {
        "listing",
        "listing.category",
        "listing.owner"
    })
    List<WishlistItem> findByUser_IdOrderByCreatedAtDesc(Long userId);

    boolean existsByUser_IdAndListing_Id(Long userId, Long listingId);

    Optional<WishlistItem> findByUser_IdAndListing_Id(
        Long userId,
        Long listingId
    );

    long countByUser_Id(Long userId);

    void deleteByUser_IdAndListing_Id(Long userId, Long listingId);

    @Modifying
    @Query("DELETE FROM WishlistItem w WHERE w.listing.id = :listingId")
    int deleteAllForListing(@Param("listingId") Long listingId);

}
