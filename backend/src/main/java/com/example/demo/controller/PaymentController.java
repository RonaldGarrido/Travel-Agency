package com.example.demo.controller;

import com.example.demo.dto.request.PaymentRequestDTO;
import com.example.demo.dto.response.PaymentResponseDTO;
import com.example.demo.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponseDTO> getPaymentById(@PathVariable String id) {
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }

    @GetMapping("/reservation/{reservationId}")
    public ResponseEntity<PaymentResponseDTO> getPaymentByReservation(
            @PathVariable String reservationId) {
        return ResponseEntity.ok(paymentService.getPaymentByReservationId(reservationId));
    }

    @PostMapping
    public ResponseEntity<PaymentResponseDTO> processPayment(
            @Valid @RequestBody PaymentRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentService.processPayment(request));
    }
}