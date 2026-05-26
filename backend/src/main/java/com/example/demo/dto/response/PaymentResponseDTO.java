package com.example.demo.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentResponseDTO {

    private String id;
    private String reservationId;
    private BigDecimal amount;
    private String method;
    private String status;
    private LocalDateTime paidAt;

    public PaymentResponseDTO() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getReservationId() { return reservationId; }
    public void setReservationId(String reservationId) { this.reservationId = reservationId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }
}