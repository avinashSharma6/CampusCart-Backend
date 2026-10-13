package com.campuscart.repository;

import com.campuscart.entity.Listing;
import com.campuscart.enums.ListingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ListingRepository extends JpaRepository<Listing, Long> {

    @EntityGraph(attributePaths = {"category", "owner"})
    Page<Listing> findByStatusOrderByCreatedAtDesc(
        ListingStatus status,
        Pageable pageable
    );

    @EntityGraph(attributePaths = {"category", "owner"})
    Optional<Listing> findByIdAndStatus(
        Long id,
        ListingStatus status
    );

    @EntityGraph(attributePaths = {"category", "owner"})
    Page<Listing> findByStatusAndCategory_IdOrderByCreatedAtDesc(
        ListingStatus status,
        Long categoryId,
        Pageable pageable
    );

    @EntityGraph(attributePaths = {"category", "owner"})
    @Query("""
        SELECT l FROM Listing l
        WHERE l.status = :status
          AND (:categoryId IS NULL OR l.category.id = :categoryId)
          AND (
              :query = ''
              OR LOWER(l.title) LIKE LOWER(CONCAT('%', :query, '%'))
              OR LOWER(l.description) LIKE LOWER(CONCAT('%', :query, '%'))
              OR LOWER(COALESCE(l.brand, '')) LIKE LOWER(CONCAT('%', :query, '%'))
              OR LOWER(l.category.name) LIKE LOWER(CONCAT('%', :query, '%'))
          )
        """)
    Page<Listing> findAvailableListings(
        @Param("query") String query,
        @Param("categoryId") Long categoryId,
        @Param("status") ListingStatus status,
        Pageable pageable
    );

    @EntityGraph(attributePaths = {"category", "owner"})
    @Query("""
        SELECT l FROM Listing l
        WHERE l.status = :status
          AND (
              LOWER(l.title) LIKE LOWER(CONCAT('%', :query, '%'))
              OR LOWER(l.description) LIKE LOWER(CONCAT('%', :query, '%'))
              OR LOWER(COALESCE(l.brand, '')) LIKE LOWER(CONCAT('%', :query, '%'))
              OR LOWER(l.category.name) LIKE LOWER(CONCAT('%', :query, '%'))
          )
        """)
    Page<Listing> searchByText(
        @Param("query") String query,
        @Param("status") ListingStatus status,
        Pageable pageable
    );

    @EntityGraph(attributePaths = {"category", "owner"})
    Page<Listing> findByOwner_IdAndStatusOrderByCreatedAtDesc(
        Long ownerId,
        ListingStatus status,
        Pageable pageable
    );

    @EntityGraph(attributePaths = {"category", "owner"})
    List<Listing> findByOwner_IdOrderByCreatedAtDesc(Long ownerId);

    long countByOwner_IdAndStatus(Long ownerId, ListingStatus status);
}
