
package com.campuscart.controller;

import com.campuscart.enums.ReportStatus;
import com.campuscart.repository.ReportRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    private final ReportRepository reportRepository;

    public AdminDashboardController(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @GetMapping
    public String dashboard(Model model) {
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

        return "admin/index";
    }
}
