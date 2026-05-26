package com.example.demo.controller;

import com.example.demo.dto.request.PaymentRequestDTO;
import com.example.demo.dto.response.PaymentResponseDTO;
import com.example.demo.exception.GlobalExceptionHandler;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentController paymentController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private PaymentResponseDTO responseDTO;
    private PaymentRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(paymentController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        responseDTO = new PaymentResponseDTO();
        responseDTO.setId("pay-1");
        responseDTO.setReservationId("res-1");
        responseDTO.setAmount(new BigDecimal("3000.00"));
        responseDTO.setMethod("CREDIT_CARD");
        responseDTO.setStatus("APPROVED");
        responseDTO.setPaidAt(LocalDateTime.now());

        requestDTO = new PaymentRequestDTO();
        requestDTO.setReservationId("res-1");
        requestDTO.setCardNumber("1234567890123456");
        requestDTO.setCardExpiry("12/27");
        requestDTO.setCvv("123");
    }

    // ─── GET /api/payments/{id} ───────────────────────────────────

    @Test
    void getPaymentById_returns200WhenExists() throws Exception {
        when(paymentService.getPaymentById("pay-1")).thenReturn(responseDTO);
        mockMvc.perform(get("/api/payments/pay-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("pay-1"));
    }

    @Test
    void getPaymentById_returns404WhenNotFound() throws Exception {
        when(paymentService.getPaymentById("bad-id"))
                .thenThrow(new ResourceNotFoundException("Payment", "bad-id"));
        mockMvc.perform(get("/api/payments/bad-id"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getPaymentById_returnsCorrectAmount() throws Exception {
        when(paymentService.getPaymentById("pay-1")).thenReturn(responseDTO);
        mockMvc.perform(get("/api/payments/pay-1"))
                .andExpect(jsonPath("$.amount").value(3000.00));
    }

    @Test
    void getPaymentById_returnsCorrectStatus() throws Exception {
        when(paymentService.getPaymentById("pay-1")).thenReturn(responseDTO);
        mockMvc.perform(get("/api/payments/pay-1"))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void getPaymentById_callsServiceWithCorrectId() throws Exception {
        when(paymentService.getPaymentById("pay-1")).thenReturn(responseDTO);
        mockMvc.perform(get("/api/payments/pay-1")).andExpect(status().isOk());
        verify(paymentService, times(1)).getPaymentById("pay-1");
    }

    // ─── GET /api/payments/reservation/{reservationId} ────────────

    @Test
    void getPaymentByReservation_returns200WhenExists() throws Exception {
        when(paymentService.getPaymentByReservationId("res-1")).thenReturn(responseDTO);
        mockMvc.perform(get("/api/payments/reservation/res-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reservationId").value("res-1"));
    }

    @Test
    void getPaymentByReservation_returns404WhenNotFound() throws Exception {
        when(paymentService.getPaymentByReservationId("bad-id"))
                .thenThrow(new ResourceNotFoundException("Payment", "bad-id"));
        mockMvc.perform(get("/api/payments/reservation/bad-id"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getPaymentByReservation_returnsCorrectMethod() throws Exception {
        when(paymentService.getPaymentByReservationId("res-1")).thenReturn(responseDTO);
        mockMvc.perform(get("/api/payments/reservation/res-1"))
                .andExpect(jsonPath("$.method").value("CREDIT_CARD"));
    }

    @Test
    void getPaymentByReservation_callsServiceWithCorrectId() throws Exception {
        when(paymentService.getPaymentByReservationId("res-1")).thenReturn(responseDTO);
        mockMvc.perform(get("/api/payments/reservation/res-1")).andExpect(status().isOk());
        verify(paymentService, times(1)).getPaymentByReservationId("res-1");
    }

    @Test
    void getPaymentByReservation_returnsCorrectAmount() throws Exception {
        when(paymentService.getPaymentByReservationId("res-1")).thenReturn(responseDTO);
        mockMvc.perform(get("/api/payments/reservation/res-1"))
                .andExpect(jsonPath("$.amount").value(3000.00));
    }

    // ─── POST /api/payments ───────────────────────────────────────

    @Test
    void processPayment_returns201WhenProcessed() throws Exception {
        when(paymentService.processPayment(any())).thenReturn(responseDTO);
        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    void processPayment_returnsApprovedStatus() throws Exception {
        when(paymentService.processPayment(any())).thenReturn(responseDTO);
        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void processPayment_returns400WhenMissingReservationId() throws Exception {
        requestDTO.setReservationId(null);
        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void processPayment_returns400WhenInvalidCardNumber() throws Exception {
        requestDTO.setCardNumber("123");
        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void processPayment_returns400WhenInvalidCvv() throws Exception {
        requestDTO.setCvv("12");
        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void processPayment_returns400WhenInvalidExpiryFormat() throws Exception {
        requestDTO.setCardExpiry("1227");
        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void processPayment_callsServiceOnce() throws Exception {
        when(paymentService.processPayment(any())).thenReturn(responseDTO);
        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated());
        verify(paymentService, times(1)).processPayment(any(PaymentRequestDTO.class));
    }
}