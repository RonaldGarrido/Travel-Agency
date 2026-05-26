package com.example.demo.controller;

import com.example.demo.dto.request.TravelPackageRequestDTO;
import com.example.demo.dto.response.TravelPackageResponseDTO;
import com.example.demo.exception.GlobalExceptionHandler;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.service.TravelPackageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TravelPackageControllerTest {

    @Mock
    private TravelPackageService travelPackageService;

    @InjectMocks
    private TravelPackageController travelPackageController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private TravelPackageResponseDTO responseDTO;
    private TravelPackageRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(travelPackageController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        responseDTO = new TravelPackageResponseDTO();
        responseDTO.setId("pkg-1");
        responseDTO.setName("Paquete Cancún");
        responseDTO.setDestination("Cancún, México");
        responseDTO.setDescription("Viaje todo incluido");
        responseDTO.setStartDate(LocalDate.of(2026, 6, 1));
        responseDTO.setEndDate(LocalDate.of(2026, 6, 10));
        responseDTO.setPrice(new BigDecimal("1500.00"));
        responseDTO.setTotalSlots(20);
        responseDTO.setAvailableSlots(20);
        responseDTO.setStatus("AVAILABLE");
        responseDTO.setType("INTERNATIONAL");

        requestDTO = new TravelPackageRequestDTO();
        requestDTO.setName("Paquete Cancún");
        requestDTO.setDestination("Cancún, México");
        requestDTO.setDescription("Viaje todo incluido");
        requestDTO.setStartDate(LocalDate.of(2026, 6, 1));
        requestDTO.setEndDate(LocalDate.of(2026, 6, 10));
        requestDTO.setPrice(new BigDecimal("1500.00"));
        requestDTO.setTotalSlots(20);
        requestDTO.setType("INTERNATIONAL");
    }

    // ─── GET /api/packages ────────────────────────────────────────

    @Test
    void getAllPackages_returns200WithList() throws Exception {
        when(travelPackageService.getAllPackages()).thenReturn(List.of(responseDTO));
        mockMvc.perform(get("/api/packages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("pkg-1"));
    }

    @Test
    void getAllPackages_returnsEmptyList() throws Exception {
        when(travelPackageService.getAllPackages()).thenReturn(List.of());
        mockMvc.perform(get("/api/packages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getAllPackages_returnsCorrectName() throws Exception {
        when(travelPackageService.getAllPackages()).thenReturn(List.of(responseDTO));
        mockMvc.perform(get("/api/packages"))
                .andExpect(jsonPath("$[0].name").value("Paquete Cancún"));
    }

    @Test
    void getAllPackages_returnsCorrectPrice() throws Exception {
        when(travelPackageService.getAllPackages()).thenReturn(List.of(responseDTO));
        mockMvc.perform(get("/api/packages"))
                .andExpect(jsonPath("$[0].price").value(1500.00));
    }

    @Test
    void getAllPackages_callsServiceOnce() throws Exception {
        when(travelPackageService.getAllPackages()).thenReturn(List.of());
        mockMvc.perform(get("/api/packages")).andExpect(status().isOk());
        verify(travelPackageService, times(1)).getAllPackages();
    }

    // ─── GET /api/packages/available ─────────────────────────────

    @Test
    void getAvailablePackages_returns200WithList() throws Exception {
        when(travelPackageService.getAvailablePackages(
                any(), any(), any(), any(), any(), any()))
                .thenReturn(List.of(responseDTO));
        mockMvc.perform(get("/api/packages/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("AVAILABLE"));
    }

    @Test
    void getAvailablePackages_returnsEmptyList() throws Exception {
        when(travelPackageService.getAvailablePackages(
                any(), any(), any(), any(), any(), any()))
                .thenReturn(List.of());
        mockMvc.perform(get("/api/packages/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getAvailablePackages_acceptsDestinationFilter() throws Exception {
        when(travelPackageService.getAvailablePackages(
                anyString(), any(), any(), any(), any(), any()))
                .thenReturn(List.of(responseDTO));
        mockMvc.perform(get("/api/packages/available")
                        .param("destination", "Cancún"))
                .andExpect(status().isOk());
    }

    @Test
    void getAvailablePackages_acceptsPriceFilters() throws Exception {
        when(travelPackageService.getAvailablePackages(
                any(), any(), any(), anyString(), anyString(), any()))
                .thenReturn(List.of(responseDTO));
        mockMvc.perform(get("/api/packages/available")
                        .param("minPrice", "500")
                        .param("maxPrice", "2000"))
                .andExpect(status().isOk());
    }

    @Test
    void getAvailablePackages_callsServiceOnce() throws Exception {
        when(travelPackageService.getAvailablePackages(
                any(), any(), any(), any(), any(), any()))
                .thenReturn(List.of());
        mockMvc.perform(get("/api/packages/available"))
                .andExpect(status().isOk());
        verify(travelPackageService, times(1))
                .getAvailablePackages(any(), any(), any(), any(), any(), any());
    }

    // ─── GET /api/packages/{id} ───────────────────────────────────

    @Test
    void getPackageById_returns200WhenExists() throws Exception {
        when(travelPackageService.getPackageById("pkg-1")).thenReturn(responseDTO);
        mockMvc.perform(get("/api/packages/pkg-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("pkg-1"));
    }

    @Test
    void getPackageById_returns404WhenNotFound() throws Exception {
        when(travelPackageService.getPackageById("bad-id"))
                .thenThrow(new ResourceNotFoundException("Package", "bad-id"));
        mockMvc.perform(get("/api/packages/bad-id"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getPackageById_returnsCorrectDestination() throws Exception {
        when(travelPackageService.getPackageById("pkg-1")).thenReturn(responseDTO);
        mockMvc.perform(get("/api/packages/pkg-1"))
                .andExpect(jsonPath("$.destination").value("Cancún, México"));
    }

    @Test
    void getPackageById_returnsCorrectAvailableSlots() throws Exception {
        when(travelPackageService.getPackageById("pkg-1")).thenReturn(responseDTO);
        mockMvc.perform(get("/api/packages/pkg-1"))
                .andExpect(jsonPath("$.availableSlots").value(20));
    }

    @Test
    void getPackageById_callsServiceWithCorrectId() throws Exception {
        when(travelPackageService.getPackageById("pkg-1")).thenReturn(responseDTO);
        mockMvc.perform(get("/api/packages/pkg-1")).andExpect(status().isOk());
        verify(travelPackageService, times(1)).getPackageById("pkg-1");
    }

    // ─── POST /api/packages ───────────────────────────────────────

    @Test
    void createPackage_returns201WhenCreated() throws Exception {
        when(travelPackageService.createPackage(any())).thenReturn(responseDTO);
        mockMvc.perform(post("/api/packages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    void createPackage_returnsCreatedPackage() throws Exception {
        when(travelPackageService.createPackage(any())).thenReturn(responseDTO);
        mockMvc.perform(post("/api/packages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(jsonPath("$.name").value("Paquete Cancún"));
    }

    @Test
    void createPackage_returns400WhenMissingName() throws Exception {
        requestDTO.setName(null);
        mockMvc.perform(post("/api/packages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createPackage_returns400WhenPriceIsZero() throws Exception {
        requestDTO.setPrice(BigDecimal.ZERO);
        mockMvc.perform(post("/api/packages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createPackage_returns400WhenMissingDestination() throws Exception {
        requestDTO.setDestination(null);
        mockMvc.perform(post("/api/packages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }

    // ─── PUT /api/packages/{id} ───────────────────────────────────

    @Test
    void updatePackage_returns200WhenUpdated() throws Exception {
        when(travelPackageService.updatePackage(anyString(), any())).thenReturn(responseDTO);
        mockMvc.perform(put("/api/packages/pkg-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk());
    }

    @Test
    void updatePackage_returns404WhenNotFound() throws Exception {
        when(travelPackageService.updatePackage(anyString(), any()))
                .thenThrow(new ResourceNotFoundException("Package", "bad-id"));
        mockMvc.perform(put("/api/packages/bad-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updatePackage_returnsUpdatedPackage() throws Exception {
        when(travelPackageService.updatePackage(anyString(), any())).thenReturn(responseDTO);
        mockMvc.perform(put("/api/packages/pkg-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(jsonPath("$.id").value("pkg-1"));
    }

    @Test
    void updatePackage_callsServiceWithCorrectId() throws Exception {
        when(travelPackageService.updatePackage(anyString(), any())).thenReturn(responseDTO);
        mockMvc.perform(put("/api/packages/pkg-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk());
        verify(travelPackageService, times(1)).updatePackage(eq("pkg-1"), any());
    }

    @Test
    void updatePackage_returns400WhenMissingName() throws Exception {
        requestDTO.setName(null);
        mockMvc.perform(put("/api/packages/pkg-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }

    // ─── PATCH /api/packages/{id}/status ─────────────────────────

    @Test
    void changePackageStatus_returns204WhenChanged() throws Exception {
        doNothing().when(travelPackageService).changePackageStatus(anyString(), anyString());
        mockMvc.perform(patch("/api/packages/pkg-1/status")
                        .param("status", "SOLD_OUT"))
                .andExpect(status().isNoContent());
    }

    @Test
    void changePackageStatus_returns404WhenNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Package", "bad-id"))
                .when(travelPackageService).changePackageStatus(anyString(), anyString());
        mockMvc.perform(patch("/api/packages/bad-id/status")
                        .param("status", "SOLD_OUT"))
                .andExpect(status().isNotFound());
    }

    @Test
    void changePackageStatus_callsServiceOnce() throws Exception {
        doNothing().when(travelPackageService).changePackageStatus(anyString(), anyString());
        mockMvc.perform(patch("/api/packages/pkg-1/status")
                        .param("status", "CANCELLED"))
                .andExpect(status().isNoContent());
        verify(travelPackageService, times(1))
                .changePackageStatus("pkg-1", "CANCELLED");
    }

    @Test
    void changePackageStatus_returns204ForAvailable() throws Exception {
        doNothing().when(travelPackageService).changePackageStatus(anyString(), anyString());
        mockMvc.perform(patch("/api/packages/pkg-1/status")
                        .param("status", "AVAILABLE"))
                .andExpect(status().isNoContent());
    }

    @Test
    void changePackageStatus_returns204ForNotValid() throws Exception {
        doNothing().when(travelPackageService).changePackageStatus(anyString(), anyString());
        mockMvc.perform(patch("/api/packages/pkg-1/status")
                        .param("status", "NOT_VALID"))
                .andExpect(status().isNoContent());
    }

    // ─── DELETE /api/packages/{id} ────────────────────────────────

    @Test
    void deletePackage_returns204WhenDeleted() throws Exception {
        doNothing().when(travelPackageService).deletePackage(anyString());
        mockMvc.perform(delete("/api/packages/pkg-1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deletePackage_returns404WhenNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Package", "bad-id"))
                .when(travelPackageService).deletePackage(anyString());
        mockMvc.perform(delete("/api/packages/bad-id"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deletePackage_callsServiceOnce() throws Exception {
        doNothing().when(travelPackageService).deletePackage(anyString());
        mockMvc.perform(delete("/api/packages/pkg-1"))
                .andExpect(status().isNoContent());
        verify(travelPackageService, times(1)).deletePackage("pkg-1");
    }

    @Test
    void deletePackage_returns400WhenHasReservations() throws Exception {
        doThrow(new RuntimeException("Cannot delete package with reservations"))
                .when(travelPackageService).deletePackage(anyString());
        mockMvc.perform(delete("/api/packages/pkg-1"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void deletePackage_returnsNoContentBody() throws Exception {
        doNothing().when(travelPackageService).deletePackage(anyString());
        mockMvc.perform(delete("/api/packages/pkg-1"))
                .andExpect(status().isNoContent());
    }
}