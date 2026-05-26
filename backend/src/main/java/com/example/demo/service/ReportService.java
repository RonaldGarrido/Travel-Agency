package com.example.demo.service;

import com.example.demo.dto.response.PackageRankingDTO;
import com.example.demo.dto.response.SalesReportDTO;

import java.util.List;

public interface ReportService {

    List<SalesReportDTO> getSalesReport(String startDate, String endDate);

    List<PackageRankingDTO> getPackageRanking(String startDate, String endDate);
}