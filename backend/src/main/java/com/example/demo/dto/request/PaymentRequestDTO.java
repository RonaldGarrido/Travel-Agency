package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class PaymentRequestDTO {

    @NotBlank(message = "Reservation id is required")
    private String reservationId;

    @NotBlank(message = "Card number is required")
    @Size(min = 16, max = 16, message = "Card number must be 16 digits")
    @Pattern(regexp = "\\d{16}", message = "Card number must contain only digits")
    private String cardNumber;

    @NotBlank(message = "Card expiry is required")
    @Pattern(regexp = "^(0[1-9]|1[0-2])/\\d{2}$", message = "Expiry format must be MM/YY")
    private String cardExpiry;

    @NotBlank(message = "CVV is required")
    @Size(min = 3, max = 4, message = "CVV must be 3 or 4 digits")
    @Pattern(regexp = "\\d{3,4}", message = "CVV must contain only digits")
    private String cvv;

    public PaymentRequestDTO() {}

    public String getReservationId() { return reservationId; }
    public void setReservationId(String reservationId) { this.reservationId = reservationId; }

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    public String getCardExpiry() { return cardExpiry; }
    public void setCardExpiry(String cardExpiry) { this.cardExpiry = cardExpiry; }

    public String getCvv() { return cvv; }
    public void setCvv(String cvv) { this.cvv = cvv; }
}