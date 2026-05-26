package com.example.demo.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "travel_packages")
public class TravelPackage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "destination", nullable = false)
    private String destination;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "total_slots", nullable = false)
    private Integer totalSlots;

    @Column(name = "available_slots", nullable = false)
    private Integer availableSlots;

    @Column(name = "included_services", columnDefinition = "TEXT")
    private String includedServices;

    @Column(name = "conditions", columnDefinition = "TEXT")
    private String conditions;

    @Column(name = "restrictions", columnDefinition = "TEXT")
    private String restrictions;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private PackageType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PackageStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) this.status = PackageStatus.AVAILABLE;
        if (this.availableSlots == null) this.availableSlots = this.totalSlots;
    }

    public enum PackageType {
        NATIONAL, INTERNATIONAL
    }

    public enum PackageStatus {
        AVAILABLE, SOLD_OUT, NOT_VALID, CANCELLED
    }

    // Constructor vacío
    public TravelPackage() {}

    // Constructor completo
    public TravelPackage(String name, String destination, String description,
                         LocalDate startDate, LocalDate endDate, BigDecimal price,
                         Integer totalSlots, String includedServices,
                         String conditions, String restrictions, PackageType type) {
        this.name = name;
        this.destination = destination;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.price = price;
        this.totalSlots = totalSlots;
        this.availableSlots = totalSlots;
        this.includedServices = includedServices;
        this.conditions = conditions;
        this.restrictions = restrictions;
        this.type = type;
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Integer getTotalSlots() { return totalSlots; }
    public void setTotalSlots(Integer totalSlots) { this.totalSlots = totalSlots; }

    public Integer getAvailableSlots() { return availableSlots; }
    public void setAvailableSlots(Integer availableSlots) { this.availableSlots = availableSlots; }

    public String getIncludedServices() { return includedServices; }
    public void setIncludedServices(String includedServices) { this.includedServices = includedServices; }

    public String getConditions() { return conditions; }
    public void setConditions(String conditions) { this.conditions = conditions; }

    public String getRestrictions() { return restrictions; }
    public void setRestrictions(String restrictions) { this.restrictions = restrictions; }

    public PackageType getType() { return type; }
    public void setType(PackageType type) { this.type = type; }

    public PackageStatus getStatus() { return status; }
    public void setStatus(PackageStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}