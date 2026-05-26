package com.example.demo.controller;

import com.example.demo.dto.response.SalesReportDTO;
import com.example.demo.dto.response.PackageRankingDTO;
import com.example.demo.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/sales")
    public ResponseEntity<List<SalesReportDTO>> getSalesReport(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        return ResponseEntity.ok(reportService.getSalesReport(startDate, endDate));
    }

    @GetMapping("/packages/ranking")
    public ResponseEntity<List<PackageRankingDTO>> getPackageRanking(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        return ResponseEntity.ok(reportService.getPackageRanking(startDate, endDate));
    }
}
