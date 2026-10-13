package com.campuscart.service;

import com.campuscart.dto.ReportForm;
import com.campuscart.entity.Listing;
import com.campuscart.entity.Report;
import com.campuscart.entity.User;
import com.campuscart.enums.ListingStatus;
import com.campuscart.enums.ReportStatus;
import com.campuscart.repository.ListingRepository;
import com.campuscart.repository.ReportRepository;
import com.campuscart.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Set;

@Service
public class ReportService {

    private static final Set<String> ALLOWED_REASONS = Set.of(
        "Suspicious or scam",
        "Prohibited item",
        "Incorrect information",
        "Duplicate listing",
        "Other"
    );

    private final ReportRepository reportRepository;
    private final ListingRepository listingRepository;
    private final UserRepository userRepository;

    public ReportService(
        ReportRepository reportRepository,
        ListingRepository listingRepository,
        UserRepository userRepository
    ) {
        this.reportRepository = reportRepository;
        this.listingRepository = listingRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Report submitReport(
        Long listingId,
        String email,
        ReportForm form
    ) {
        User reporter = findUser(email);

        Listing listing = listingRepository.findByIdAndStatus(
            listingId,
            ListingStatus.AVAILABLE
        ).orElseThrow(() -> new IllegalArgumentException(
            "This listing is no longer available."
        ));

        if (listing.getOwner().getId().equals(reporter.getId())) {
            throw new AccessDeniedException(
                "You cannot report your own listing."
            );
        }

        String reason = form.getReason() == null
            ? ""
            : form.getReason().trim();

        if (!ALLOWED_REASONS.contains(reason)) {
            throw new IllegalArgumentException(
                "Please select a valid report reason."
            );
        }

        if (reportRepository.existsByListingIdAndReporterIdAndStatus(
            listingId,
            reporter.getId(),
            ReportStatus.OPEN
        )) {
            throw new IllegalArgumentException(
                "You already have an open report for this listing."
            );
        }

        Report report = new Report();
        report.setListing(listing);
        report.setReporter(reporter);
        report.setReason(reason);

        String description = form.getDescription();
        report.setDescription(
            description == null || description.isBlank()
                ? null
                : description.trim()
        );

        report.setStatus(ReportStatus.OPEN);

        return reportRepository.save(report);
    }

    @Transactional
    public void markReviewed(Long reportId, String notes) {
        Report report = findReport(reportId);

        if (report.getStatus() == ReportStatus.RESOLVED) {
            throw new IllegalArgumentException(
                "This report has already been resolved."
            );
        }

        report.setStatus(ReportStatus.REVIEWED);
        report.setResolutionNotes(normalizeNotes(notes));
        reportRepository.save(report);
    }

    @Transactional
    public void resolveReport(Long reportId, String notes) {
        Report report = findReport(reportId);
        report.setStatus(ReportStatus.RESOLVED);
        report.setResolutionNotes(normalizeNotes(notes));
        reportRepository.save(report);
    }

    @Transactional
    public void removeReportedListing(Long reportId, String notes) {
        Report report = findReport(reportId);
        Listing listing = report.getListing();

        listing.setStatus(ListingStatus.REMOVED);
        listingRepository.save(listing);

        report.setStatus(ReportStatus.RESOLVED);

        String normalizedNotes = normalizeNotes(notes);
        report.setResolutionNotes(
            normalizedNotes == null
                ? "Listing removed by an administrator."
                : normalizedNotes
        );

        reportRepository.save(report);
    }

    private Report findReport(Long reportId) {
        return reportRepository.findById(reportId)
            .orElseThrow(() -> new IllegalArgumentException(
                "The requested report could not be found."
            ));
    }

    private User findUser(String email) {
        return userRepository.findByEmailIgnoreCase(
            email.trim().toLowerCase(Locale.ROOT)
        ).orElseThrow(() -> new IllegalArgumentException(
            "Your user account could not be found."
        ));
    }

    private String normalizeNotes(String notes) {
        if (notes == null || notes.isBlank()) {
            return null;
        }

        String trimmed = notes.trim();

        if (trimmed.length() > 1000) {
            throw new IllegalArgumentException(
                "Resolution notes must be at most 1000 characters."
            );
        }

        return trimmed;
    }
}

