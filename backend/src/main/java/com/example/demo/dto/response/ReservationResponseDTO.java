package com.example.demo.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ReservationResponseDTO {

    private String id;
    private String userId;
    private String userName;
    private String packageId;
    private String packageName;
    private Integer passengerCount;
    private BigDecimal baseAmount;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private String discountDetail;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    public ReservationResponseDTO() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getPackageId() { return packageId; }
    public void setPackageId(String packageId) { this.packageId = packageId; }

    public String getPackageName() { return packageName; }
    public void setPackageName(String packageName) { this.packageName = packageName; }

    public Integer getPassengerCount() { return passengerCount; }
    public void setPassengerCount(Integer passengerCount) { this.passengerCount = passengerCount; }

    public BigDecimal getBaseAmount() { return baseAmount; }
    public void setBaseAmount(BigDecimal baseAmount) { this.baseAmount = baseAmount; }

    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }

    public BigDecimal getFinalAmount() { return finalAmount; }
    public void setFinalAmount(BigDecimal finalAmount) { this.finalAmount = finalAmount; }

    public String getDiscountDetail() { return discountDetail; }
    public void setDiscountDetail(String discountDetail) { this.discountDetail = discountDetail; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
}