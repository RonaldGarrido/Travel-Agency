package com.example.demo.dto.response;

import java.math.BigDecimal;

public class PackageRankingDTO {

    private String packageId;
    private String packageName;
    private String destination;
    private Long totalReservations;
    private Long totalPassengers;
    private BigDecimal totalRevenue;

    public PackageRankingDTO() {}

    public String getPackageId() { return packageId; }
    public void setPackageId(String packageId) { this.packageId = packageId; }

    public String getPackageName() { return packageName; }
    public void setPackageName(String packageName) { this.packageName = packageName; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public Long getTotalReservations() { return totalReservations; }
    public void setTotalReservations(Long totalReservations) { this.totalReservations = totalReservations; }

    public Long getTotalPassengers() { return totalPassengers; }
    public void setTotalPassengers(Long totalPassengers) { this.totalPassengers = totalPassengers; }

    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }
}