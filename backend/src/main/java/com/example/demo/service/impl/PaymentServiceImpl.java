package com.example.demo.service.impl;

import com.example.demo.dto.request.PaymentRequestDTO;
import com.example.demo.dto.response.PaymentResponseDTO;
import com.example.demo.exception.BusinessException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Payment;
import com.example.demo.model.Reservation;
import com.example.demo.repository.PaymentRepository;
import com.example.demo.repository.ReservationRepository;
import com.example.demo.service.PaymentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              ReservationRepository reservationRepository) {
        this.paymentRepository = paymentRepository;
        this.reservationRepository = reservationRepository;
    }

    @Override
    public PaymentResponseDTO getPaymentById(String id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", id));
        return mapToResponseDTO(payment);
    }

    @Override
    public PaymentResponseDTO getPaymentByReservationId(String reservationId) {
        Payment payment = paymentRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", reservationId));
        return mapToResponseDTO(payment);
    }

    @Override
    @Transactional
    public PaymentResponseDTO processPayment(PaymentRequestDTO request) {
        Reservation reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", request.getReservationId()));

        if (reservation.getStatus() == Reservation.ReservationStatus.CANCELLED) {
            throw new BusinessException("Cannot pay a cancelled reservation");
        }
        if (reservation.getStatus() == Reservation.ReservationStatus.CONFIRMED) {
            throw new BusinessException("Reservation is already paid");
        }
        if (paymentRepository.existsByReservationId(request.getReservationId())) {
            throw new BusinessException("Payment already exists for this reservation");
        }

        Payment payment = new Payment();
        payment.setReservation(reservation);
        payment.setAmount(reservation.getFinalAmount());
        payment.setMethod(Payment.PaymentMethod.CREDIT_CARD);
        payment.setCardNumber(request.getCardNumber());
        payment.setCardExpiry(request.getCardExpiry());
        payment.setCvv(request.getCvv());
        payment.setStatus(Payment.PaymentStatus.APPROVED);

        reservation.setStatus(Reservation.ReservationStatus.CONFIRMED);
        reservationRepository.save(reservation);

        return mapToResponseDTO(paymentRepository.save(payment));
    }

    private PaymentResponseDTO mapToResponseDTO(Payment payment) {
        PaymentResponseDTO dto = new PaymentResponseDTO();
        dto.setId(payment.getId());
        dto.setReservationId(payment.getReservation().getId());
        dto.setAmount(payment.getAmount());
        dto.setMethod(payment.getMethod().name());
        dto.setStatus(payment.getStatus().name());
        dto.setPaidAt(payment.getPaidAt());
        return dto;
    }
}
