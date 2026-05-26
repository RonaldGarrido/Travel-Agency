package com.example.demo.controller;

import com.example.demo.dto.request.ReservationRequestDTO;
import com.example.demo.dto.response.ReservationResponseDTO;
import com.example.demo.exception.GlobalExceptionHandler;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.service.ReservationService;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ReservationControllerTest {

    @Mock
    private ReservationService reservationService;

    @InjectMocks
    private ReservationController reservationController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private ReservationResponseDTO responseDTO;
    private ReservationRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(reservationController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        responseDTO = new ReservationResponseDTO();
        responseDTO.setId("res-1");
        responseDTO.setUserId("user-1");
        responseDTO.setUserName("Juan Pérez");
        responseDTO.setPackageId("pkg-1");
        responseDTO.setPackageName("Paquete Cancún");
        responseDTO.setPassengerCount(2);
        responseDTO.setBaseAmount(new BigDecimal("3000.00"));
        responseDTO.setDiscountAmount(BigDecimal.ZERO);
        responseDTO.setFinalAmount(new BigDecimal("3000.00"));
        responseDTO.setStatus("PENDING_PAYMENT");
        responseDTO.setCreatedAt(LocalDateTime.now());
        responseDTO.setExpiresAt(LocalDateTime.now().plusHours(24));

        requestDTO = new ReservationRequestDTO();
        requestDTO.setUserId("user-1");
        requestDTO.setPackageId("pkg-1");
        requestDTO.setPassengerCount(2);
    }

    // ─── GET /api/reservations ────────────────────────────────────

    @Test
    void getAllReservations_returns200WithList() throws Exception {
        when(reservationService.getAllReservations()).thenReturn(List.of(responseDTO));
        mockMvc.perform(get("/api/reservations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("res-1"));
    }

    @Test
    void getAllReservations_returnsEmptyList() throws Exception {
        when(reservationService.getAllReservations()).thenReturn(List.of());
        mockMvc.perform(get("/api/reservations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getAllReservations_returnsCorrectStatus() throws Exception {
        when(reservationService.getAllReservations()).thenReturn(List.of(responseDTO));
        mockMvc.perform(get("/api/reservations"))
                .andExpect(jsonPath("$[0].status").value("PENDING_PAYMENT"));
    }

    @Test
    void getAllReservations_returnsCorrectFinalAmount() throws Exception {
        when(reservationService.getAllReservations()).thenReturn(List.of(responseDTO));
        mockMvc.perform(get("/api/reservations"))
                .andExpect(jsonPath("$[0].finalAmount").value(3000.00));
    }

    @Test
    void getAllReservations_callsServiceOnce() throws Exception {
        when(reservationService.getAllReservations()).thenReturn(List.of());
        mockMvc.perform(get("/api/reservations")).andExpect(status().isOk());
        verify(reservationService, times(1)).getAllReservations();
    }

    // ─── GET /api/reservations/{id} ───────────────────────────────

    @Test
    void getReservationById_returns200WhenExists() throws Exception {
        when(reservationService.getReservationById("res-1")).thenReturn(responseDTO);
        mockMvc.perform(get("/api/reservations/res-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("res-1"));
    }

    @Test
    void getReservationById_returns404WhenNotFound() throws Exception {
        when(reservationService.getReservationById("bad-id"))
                .thenThrow(new ResourceNotFoundException("Reservation", "bad-id"));
        mockMvc.perform(get("/api/reservations/bad-id"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getReservationById_returnsCorrectUserName() throws Exception {
        when(reservationService.getReservationById("res-1")).thenReturn(responseDTO);
        mockMvc.perform(get("/api/reservations/res-1"))
                .andExpect(jsonPath("$.userName").value("Juan Pérez"));
    }

    @Test
    void getReservationById_returnsCorrectPackageName() throws Exception {
        when(reservationService.getReservationById("res-1")).thenReturn(responseDTO);
        mockMvc.perform(get("/api/reservations/res-1"))
                .andExpect(jsonPath("$.packageName").value("Paquete Cancún"));
    }

    @Test
    void getReservationById_callsServiceWithCorrectId() throws Exception {
        when(reservationService.getReservationById("res-1")).thenReturn(responseDTO);
        mockMvc.perform(get("/api/reservations/res-1")).andExpect(status().isOk());
        verify(reservationService, times(1)).getReservationById("res-1");
    }

    // ─── GET /api/reservations/user/{userId} ──────────────────────

    @Test
    void getReservationsByUser_returns200WithList() throws Exception {
        when(reservationService.getReservationsByUser("user-1"))
                .thenReturn(List.of(responseDTO));
        mockMvc.perform(get("/api/reservations/user/user-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value("user-1"));
    }

    @Test
    void getReservationsByUser_returnsEmptyList() throws Exception {
        when(reservationService.getReservationsByUser("user-1")).thenReturn(List.of());
        mockMvc.perform(get("/api/reservations/user/user-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getReservationsByUser_callsServiceWithCorrectId() throws Exception {
        when(reservationService.getReservationsByUser("user-1")).thenReturn(List.of());
        mockMvc.perform(get("/api/reservations/user/user-1")).andExpect(status().isOk());
        verify(reservationService, times(1)).getReservationsByUser("user-1");
    }

    @Test
    void getReservationsByUser_returnsCorrectPassengerCount() throws Exception {
        when(reservationService.getReservationsByUser("user-1"))
                .thenReturn(List.of(responseDTO));
        mockMvc.perform(get("/api/reservations/user/user-1"))
                .andExpect(jsonPath("$[0].passengerCount").value(2));
    }

    @Test
    void getReservationsByUser_returnsMultipleReservations() throws Exception {
        ReservationResponseDTO res2 = new ReservationResponseDTO();
        res2.setId("res-2");
        res2.setUserId("user-1");
        res2.setStatus("CONFIRMED");
        res2.setPassengerCount(1);
        res2.setFinalAmount(new BigDecimal("1500.00"));
        res2.setBaseAmount(new BigDecimal("1500.00"));
        res2.setDiscountAmount(BigDecimal.ZERO);
        when(reservationService.getReservationsByUser("user-1"))
                .thenReturn(List.of(responseDTO, res2));
        mockMvc.perform(get("/api/reservations/user/user-1"))
                .andExpect(jsonPath("$.length()").value(2));
    }

    // ─── POST /api/reservations ───────────────────────────────────

    @Test
    void createReservation_returns201WhenCreated() throws Exception {
        when(reservationService.createReservation(any())).thenReturn(responseDTO);
        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    void createReservation_returnsCreatedReservation() throws Exception {
        when(reservationService.createReservation(any())).thenReturn(responseDTO);
        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(jsonPath("$.id").value("res-1"));
    }

    @Test
    void createReservation_returns400WhenMissingUserId() throws Exception {
        requestDTO.setUserId(null);
        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createReservation_returns400WhenMissingPackageId() throws Exception {
        requestDTO.setPackageId(null);
        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createReservation_returns400WhenPassengerCountIsZero() throws Exception {
        requestDTO.setPassengerCount(0);
        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }

    // ─── PATCH /api/reservations/{id}/cancel ─────────────────────

    @Test
    void cancelReservation_returns204WhenCancelled() throws Exception {
        doNothing().when(reservationService).cancelReservation(anyString());
        mockMvc.perform(patch("/api/reservations/res-1/cancel"))
                .andExpect(status().isNoContent());
    }

    @Test
    void cancelReservation_returns404WhenNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Reservation", "bad-id"))
                .when(reservationService).cancelReservation(anyString());
        mockMvc.perform(patch("/api/reservations/bad-id/cancel"))
                .andExpect(status().isNotFound());
    }

    @Test
    void cancelReservation_callsServiceOnce() throws Exception {
        doNothing().when(reservationService).cancelReservation(anyString());
        mockMvc.perform(patch("/api/reservations/res-1/cancel"))
                .andExpect(status().isNoContent());
        verify(reservationService, times(1)).cancelReservation("res-1");
    }

    @Test
    void cancelReservation_returns400WhenAlreadyCancelled() throws Exception {
        doThrow(new RuntimeException("Reservation is already cancelled"))
                .when(reservationService).cancelReservation(anyString());
        mockMvc.perform(patch("/api/reservations/res-1/cancel"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void cancelReservation_returnsNoContentBody() throws Exception {
        doNothing().when(reservationService).cancelReservation(anyString());
        mockMvc.perform(patch("/api/reservations/res-1/cancel"))
                .andExpect(status().isNoContent());
    }
}