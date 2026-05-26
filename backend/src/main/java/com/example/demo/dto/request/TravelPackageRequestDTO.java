package com.example.demo.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TravelPackageRequestDTO {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Destination is required")
    private String destination;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than zero")
    private BigDecimal price;

    @NotNull(message = "Total slots is required")
    @Min(value = 1, message = "Total slots must be greater than zero")
    private Integer totalSlots;

    private String includedServices;
    private String conditions;
    private String restrictions;
    private String type;

    public TravelPackageRequestDTO() {}

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

    public String getIncludedServices() { return includedServices; }
    public void setIncludedServices(String includedServices) { this.includedServices = includedServices; }

    public String getConditions() { return conditions; }
    public void setConditions(String conditions) { this.conditions = conditions; }

    public String getRestrictions() { return restrictions; }
    public void setRestrictions(String restrictions) { this.restrictions = restrictions; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}