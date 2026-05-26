package com.example.demo.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SalesReportDTO {

    private String reservationId;
    private String clientName;
    private String packageName;
    private Integer passengerCount;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private String status;
    private LocalDateTime date;

    public SalesReportDTO() {}

    public String getReservationId() { return reservationId; }
    public void setReservationId(String reservationId) { this.reservationId = reservationId; }

    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }

    public String getPackageName() { return packageName; }
    public void setPackageName(String packageName) { this.packageName = packageName; }

    public Integer getPassengerCount() { return passengerCount; }
    public void setPassengerCount(Integer passengerCount) { this.passengerCount = passengerCount; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public BigDecimal getPaidAmount() { return paidAmount; }
    public void setPaidAmount(BigDecimal paidAmount) { this.paidAmount = paidAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }
}