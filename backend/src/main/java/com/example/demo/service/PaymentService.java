package com.example.demo.service;

import com.example.demo.dto.request.PaymentRequestDTO;
import com.example.demo.dto.response.PaymentResponseDTO;

public interface PaymentService {

    PaymentResponseDTO getPaymentById(String id);

    PaymentResponseDTO getPaymentByReservationId(String reservationId);

    PaymentResponseDTO processPayment(PaymentRequestDTO request);
}