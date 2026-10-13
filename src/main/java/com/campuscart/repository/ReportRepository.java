package com.campuscart.repository;

import com.campuscart.entity.Report;
import com.campuscart.enums.ReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {

    @EntityGraph(attributePaths = {
        "listing",
        "listing.owner",
        "listing.category",
        "reporter"
    })
    Page<Report> findByStatusOrderByCreatedAtAsc(
        ReportStatus status,
        Pageable pageable
    );

    List<Report> findByListingIdOrderByCreatedAtDesc(Long listingId);

    boolean existsByListingIdAndReporterIdAndStatus(
        Long listingId,
        Long reporterId,
        ReportStatus status
    );

    @Modifying
    @Query("DELETE FROM Report r WHERE r.listing.id = :listingId")
    int deleteAllForListing(@Param("listingId") Long listingId);

    long countByStatus(ReportStatus status);
}
