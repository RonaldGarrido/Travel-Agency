package com.example.demo.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_id", nullable = false)
    private TravelPackage travelPackage;

    @Column(name = "passenger_count", nullable = false)
    private Integer passengerCount;

    @Column(name = "base_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal baseAmount;

    @Column(name = "discount_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal discountAmount;

    @Column(name = "final_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal finalAmount;

    @Column(name = "discount_detail", columnDefinition = "TEXT")
    private String discountDetail;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReservationStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) this.status = ReservationStatus.PENDING_PAYMENT;
        if (this.discountAmount == null) this.discountAmount = BigDecimal.ZERO;
        // Expira en 24 horas si no se paga
        this.expiresAt = this.createdAt.plusHours(24);
    }

    public enum ReservationStatus {
        PENDING_PAYMENT, CONFIRMED, CANCELLED, EXPIRED
    }

    // Constructor vacío
    public Reservation() {}

    // Constructor completo
    public Reservation(User user, TravelPackage travelPackage, Integer passengerCount,
                       BigDecimal baseAmount, BigDecimal discountAmount,
                       BigDecimal finalAmount, String discountDetail) {
        this.user = user;
        this.travelPackage = travelPackage;
        this.passengerCount = passengerCount;
        this.baseAmount = baseAmount;
        this.discountAmount = discountAmount;
        this.finalAmount = finalAmount;
        this.discountDetail = discountDetail;
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public TravelPackage getTravelPackage() { return travelPackage; }
    public void setTravelPackage(TravelPackage travelPackage) { this.travelPackage = travelPackage; }

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

    public ReservationStatus getStatus() { return status; }
    public void setStatus(ReservationStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
}