package com.campuscart.controller;

import com.campuscart.entity.Report;
import com.campuscart.enums.ReportStatus;
import com.campuscart.repository.ReportRepository;
import com.campuscart.service.ReportService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/reports")
public class AdminReportController {

    private final ReportRepository reportRepository;
    private final ReportService reportService;

    public AdminReportController(
        ReportRepository reportRepository,
        ReportService reportService
    ) {
        this.reportRepository = reportRepository;
        this.reportService = reportService;
    }

    @GetMapping
    public String reports(
        @RequestParam(defaultValue = "OPEN") ReportStatus status,
        @RequestParam(defaultValue = "0") int page,
        Model model
    ) {
        int safePage = Math.max(page, 0);
        Pageable pageable = PageRequest.of(
            safePage,
            20,
            Sort.by(Sort.Direction.ASC, "createdAt")
        );

        Page<Report> reportPage =
            reportRepository.findByStatusOrderByCreatedAtAsc(
                status,
                pageable
            );

        model.addAttribute("reports", reportPage.getContent());
        model.addAttribute("selectedStatus", status);
        model.addAttribute("statuses", ReportStatus.values());
        model.addAttribute("currentPage", safePage);
        model.addAttribute("totalPages", reportPage.getTotalPages());
        model.addAttribute(
            "openCount",
            reportRepository.countByStatus(ReportStatus.OPEN)
        );
        model.addAttribute(
            "reviewedCount",
            reportRepository.countByStatus(ReportStatus.REVIEWED)
        );
        model.addAttribute(
            "resolvedCount",
            reportRepository.countByStatus(ReportStatus.RESOLVED)
        );

        return "admin/reports";
    }

    @PostMapping("/{id}/review")
    public String markReviewed(
        @PathVariable Long id,
        @RequestParam(required = false) String notes,
        RedirectAttributes redirectAttributes
    ) {
        try {
            reportService.markReviewed(id, notes);
            redirectAttributes.addFlashAttribute(
                "successMessage",
                "Report marked as reviewed."
            );
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute(
                "errorMessage",
                exception.getMessage()
            );
        }

        return "redirect:/admin/reports";
    }

    @PostMapping("/{id}/resolve")
    public String resolve(
        @PathVariable Long id,
        @RequestParam(required = false) String notes,
        RedirectAttributes redirectAttributes
    ) {
        try {
            reportService.resolveReport(id, notes);
            redirectAttributes.addFlashAttribute(
                "successMessage",
                "Report resolved."
            );
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute(
                "errorMessage",
                exception.getMessage()
            );
        }

        return "redirect:/admin/reports";
    }

    @PostMapping("/{id}/remove-listing")
    public String removeListing(
        @PathVariable Long id,
        @RequestParam(required = false) String notes,
        RedirectAttributes redirectAttributes
    ) {
        try {
            reportService.removeReportedListing(id, notes);
            redirectAttributes.addFlashAttribute(
                "successMessage",
                "The listing was removed from the public marketplace and the report was resolved."
            );
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute(
                "errorMessage",
                exception.getMessage()
            );
        }

        return "redirect:/admin/reports";
    }
}
