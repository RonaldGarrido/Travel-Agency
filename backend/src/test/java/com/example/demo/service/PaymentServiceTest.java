package com.example.demo.service;

import com.example.demo.dto.request.PaymentRequestDTO;
import com.example.demo.dto.response.PaymentResponseDTO;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Payment;
import com.example.demo.model.Reservation;
import com.example.demo.model.TravelPackage;
import com.example.demo.model.User;
import com.example.demo.repository.PaymentRepository;
import com.example.demo.repository.ReservationRepository;
import com.example.demo.service.impl.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock private PaymentRepository paymentRepository;
    @Mock private ReservationRepository reservationRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Reservation reservation;
    private Payment payment;
    private PaymentRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setId("user-1");
        user.setFullName("Juan Pérez");

        TravelPackage pkg = new TravelPackage();
        pkg.setId("pkg-1");
        pkg.setName("Paquete Cancún");
        pkg.setPrice(new BigDecimal("1500.00"));
        pkg.setAvailableSlots(20);
        pkg.setTotalSlots(20);
        pkg.setStartDate(LocalDate.of(2026, 6, 1));
        pkg.setEndDate(LocalDate.of(2026, 6, 10));
        pkg.setStatus(TravelPackage.PackageStatus.AVAILABLE);

        reservation = new Reservation();
        reservation.setId("res-1");
        reservation.setUser(user);
        reservation.setTravelPackage(pkg);
        reservation.setPassengerCount(2);
        reservation.setBaseAmount(new BigDecimal("3000.00"));
        reservation.setDiscountAmount(BigDecimal.ZERO);
        reservation.setFinalAmount(new BigDecimal("3000.00"));
        reservation.setStatus(Reservation.ReservationStatus.PENDING_PAYMENT);
        reservation.setCreatedAt(LocalDateTime.now());
        reservation.setExpiresAt(LocalDateTime.now().plusHours(24));

        payment = new Payment();
        payment.setId("pay-1");
        payment.setReservation(reservation);
        payment.setAmount(new BigDecimal("3000.00"));
        payment.setMethod(Payment.PaymentMethod.CREDIT_CARD);
        payment.setCardNumber("1234567890123456");
        payment.setCardExpiry("12/27");
        payment.setCvv("123");
        payment.setStatus(Payment.PaymentStatus.APPROVED);
        payment.setPaidAt(LocalDateTime.now());

        requestDTO = new PaymentRequestDTO();
        requestDTO.setReservationId("res-1");
        requestDTO.setCardNumber("1234567890123456");
        requestDTO.setCardExpiry("12/27");
        requestDTO.setCvv("123");
    }

    // ─── getPaymentById ───────────────────────────────────────────

    @Test
    void getPaymentById_returnsPaymentWhenExists() {
        when(paymentRepository.findById("pay-1")).thenReturn(Optional.of(payment));
        PaymentResponseDTO result = paymentService.getPaymentById("pay-1");
        assertEquals("pay-1", result.getId());
    }

    @Test
    void getPaymentById_throwsNotFoundWhenDoesNotExist() {
        when(paymentRepository.findById("bad-id")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> paymentService.getPaymentById("bad-id"));
    }

    @Test
    void getPaymentById_mapsAmountCorrectly() {
        when(paymentRepository.findById("pay-1")).thenReturn(Optional.of(payment));
        assertEquals(new BigDecimal("3000.00"),
                paymentService.getPaymentById("pay-1").getAmount());
    }

    @Test
    void getPaymentById_mapsStatusCorrectly() {
        when(paymentRepository.findById("pay-1")).thenReturn(Optional.of(payment));
        assertEquals("APPROVED", paymentService.getPaymentById("pay-1").getStatus());
    }

    @Test
    void getPaymentById_mapsMethodCorrectly() {
        when(paymentRepository.findById("pay-1")).thenReturn(Optional.of(payment));
        assertEquals("CREDIT_CARD", paymentService.getPaymentById("pay-1").getMethod());
    }

    // ─── getPaymentByReservationId ────────────────────────────────

    @Test
    void getPaymentByReservationId_returnsPaymentWhenExists() {
        when(paymentRepository.findByReservationId("res-1")).thenReturn(Optional.of(payment));
        PaymentResponseDTO result = paymentService.getPaymentByReservationId("res-1");
        assertEquals("res-1", result.getReservationId());
    }

    @Test
    void getPaymentByReservationId_throwsNotFoundWhenDoesNotExist() {
        when(paymentRepository.findByReservationId("bad-id")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> paymentService.getPaymentByReservationId("bad-id"));
    }

    @Test
    void getPaymentByReservationId_mapsAmountCorrectly() {
        when(paymentRepository.findByReservationId("res-1")).thenReturn(Optional.of(payment));
        assertEquals(new BigDecimal("3000.00"),
                paymentService.getPaymentByReservationId("res-1").getAmount());
    }

    @Test
    void getPaymentByReservationId_mapsStatusAsApproved() {
        when(paymentRepository.findByReservationId("res-1")).thenReturn(Optional.of(payment));
        assertEquals("APPROVED",
                paymentService.getPaymentByReservationId("res-1").getStatus());
    }

    @Test
    void getPaymentByReservationId_callsRepositoryWithCorrectId() {
        when(paymentRepository.findByReservationId("res-1")).thenReturn(Optional.of(payment));
        paymentService.getPaymentByReservationId("res-1");
        verify(paymentRepository, times(1)).findByReservationId("res-1");
    }

    // ─── processPayment ───────────────────────────────────────────

    @Test
    void processPayment_processesAndReturnsPayment() {
        when(reservationRepository.findById("res-1")).thenReturn(Optional.of(reservation));
        when(paymentRepository.existsByReservationId("res-1")).thenReturn(false);
        when(reservationRepository.save(any())).thenReturn(reservation);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        PaymentResponseDTO result = paymentService.processPayment(requestDTO);
        assertNotNull(result);
        assertEquals("APPROVED", result.getStatus());
    }

    @Test
    void processPayment_throwsNotFoundWhenReservationDoesNotExist() {
        when(reservationRepository.findById("res-1")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> paymentService.processPayment(requestDTO));
    }

    @Test
    void processPayment_throwsExceptionWhenReservationCancelled() {
        reservation.setStatus(Reservation.ReservationStatus.CANCELLED);
        when(reservationRepository.findById("res-1")).thenReturn(Optional.of(reservation));
        assertThrows(RuntimeException.class,
                () -> paymentService.processPayment(requestDTO));
    }

    @Test
    void processPayment_throwsExceptionWhenAlreadyPaid() {
        reservation.setStatus(Reservation.ReservationStatus.CONFIRMED);
        when(reservationRepository.findById("res-1")).thenReturn(Optional.of(reservation));
        assertThrows(RuntimeException.class,
                () -> paymentService.processPayment(requestDTO));
    }

    @Test
    void processPayment_throwsExceptionWhenPaymentAlreadyExists() {
        when(reservationRepository.findById("res-1")).thenReturn(Optional.of(reservation));
        when(paymentRepository.existsByReservationId("res-1")).thenReturn(true);
        assertThrows(RuntimeException.class,
                () -> paymentService.processPayment(requestDTO));
    }

    @Test
    void processPayment_confirmsReservationAfterPayment() {
        when(reservationRepository.findById("res-1")).thenReturn(Optional.of(reservation));
        when(paymentRepository.existsByReservationId("res-1")).thenReturn(false);
        when(reservationRepository.save(any())).thenReturn(reservation);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        paymentService.processPayment(requestDTO);
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    void processPayment_savesPaymentOnce() {
        when(reservationRepository.findById("res-1")).thenReturn(Optional.of(reservation));
        when(paymentRepository.existsByReservationId("res-1")).thenReturn(false);
        when(reservationRepository.save(any())).thenReturn(reservation);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        paymentService.processPayment(requestDTO);
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }
}