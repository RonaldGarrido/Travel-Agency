package com.example.demo.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ReservationRequestDTO {

    @NotBlank(message = "User id is required")
    private String userId;

    @NotBlank(message = "Package id is required")
    private String packageId;

    @NotNull(message = "Passenger count is required")
    @Min(value = 1, message = "Passenger count must be greater than zero")
    private Integer passengerCount;

    public ReservationRequestDTO() {}

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getPackageId() { return packageId; }
    public void setPackageId(String packageId) { this.packageId = packageId; }

    public Integer getPassengerCount() { return passengerCount; }
    public void setPassengerCount(Integer passengerCount) { this.passengerCount = passengerCount; }
}