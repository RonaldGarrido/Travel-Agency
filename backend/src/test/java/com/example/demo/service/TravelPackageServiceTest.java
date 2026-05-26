package com.example.demo.service;

import com.example.demo.dto.request.TravelPackageRequestDTO;
import com.example.demo.dto.response.TravelPackageResponseDTO;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.TravelPackage;
import com.example.demo.repository.TravelPackageRepository;
import com.example.demo.service.impl.TravelPackageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TravelPackageServiceTest {

    @Mock
    private TravelPackageRepository travelPackageRepository;

    @InjectMocks
    private TravelPackageServiceImpl travelPackageService;

    private TravelPackage pkg;
    private TravelPackageRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        pkg = new TravelPackage();
        pkg.setId("pkg-1");
        pkg.setName("Paquete Cancún");
        pkg.setDestination("Cancún, México");
        pkg.setDescription("Viaje todo incluido");
        pkg.setStartDate(LocalDate.of(2026, 6, 1));
        pkg.setEndDate(LocalDate.of(2026, 6, 10));
        pkg.setPrice(new BigDecimal("1500.00"));
        pkg.setTotalSlots(20);
        pkg.setAvailableSlots(20);
        pkg.setStatus(TravelPackage.PackageStatus.AVAILABLE);
        pkg.setType(TravelPackage.PackageType.INTERNATIONAL);
        pkg.setCreatedAt(LocalDateTime.now());

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

    // ─── getAllPackages ────────────────────────────────────────────

    @Test
    void getAllPackages_returnsListOfPackages() {
        when(travelPackageRepository.findAll()).thenReturn(List.of(pkg));
        List<TravelPackageResponseDTO> result = travelPackageService.getAllPackages();
        assertEquals(1, result.size());
        assertEquals("Paquete Cancún", result.get(0).getName());
    }

    @Test
    void getAllPackages_returnsEmptyList() {
        when(travelPackageRepository.findAll()).thenReturn(List.of());
        List<TravelPackageResponseDTO> result = travelPackageService.getAllPackages();
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllPackages_mapsAllFieldsCorrectly() {
        when(travelPackageRepository.findAll()).thenReturn(List.of(pkg));
        TravelPackageResponseDTO dto = travelPackageService.getAllPackages().get(0);
        assertEquals(pkg.getId(), dto.getId());
        assertEquals(pkg.getDestination(), dto.getDestination());
        assertEquals(pkg.getPrice(), dto.getPrice());
        assertEquals(pkg.getStatus().name(), dto.getStatus());
    }

    @Test
    void getAllPackages_returnsMultiplePackages() {
        TravelPackage pkg2 = new TravelPackage();
        pkg2.setId("pkg-2");
        pkg2.setName("Paquete París");
        pkg2.setDestination("París, Francia");
        pkg2.setDescription("Tour por Europa");
        pkg2.setStartDate(LocalDate.of(2026, 7, 1));
        pkg2.setEndDate(LocalDate.of(2026, 7, 15));
        pkg2.setPrice(new BigDecimal("3000.00"));
        pkg2.setTotalSlots(10);
        pkg2.setAvailableSlots(10);
        pkg2.setStatus(TravelPackage.PackageStatus.AVAILABLE);
        when(travelPackageRepository.findAll()).thenReturn(List.of(pkg, pkg2));
        assertEquals(2, travelPackageService.getAllPackages().size());
    }

    @Test
    void getAllPackages_callsRepositoryOnce() {
        when(travelPackageRepository.findAll()).thenReturn(List.of());
        travelPackageService.getAllPackages();
        verify(travelPackageRepository, times(1)).findAll();
    }

    // ─── getPackageById ───────────────────────────────────────────

    @Test
    void getPackageById_returnsPackageWhenExists() {
        when(travelPackageRepository.findById("pkg-1")).thenReturn(Optional.of(pkg));
        TravelPackageResponseDTO result = travelPackageService.getPackageById("pkg-1");
        assertEquals("pkg-1", result.getId());
    }

    @Test
    void getPackageById_throwsNotFoundWhenDoesNotExist() {
        when(travelPackageRepository.findById("bad-id")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> travelPackageService.getPackageById("bad-id"));
    }

    @Test
    void getPackageById_mapsNameCorrectly() {
        when(travelPackageRepository.findById("pkg-1")).thenReturn(Optional.of(pkg));
        assertEquals("Paquete Cancún", travelPackageService.getPackageById("pkg-1").getName());
    }

    @Test
    void getPackageById_mapsPriceCorrectly() {
        when(travelPackageRepository.findById("pkg-1")).thenReturn(Optional.of(pkg));
        assertEquals(new BigDecimal("1500.00"),
                travelPackageService.getPackageById("pkg-1").getPrice());
    }

    @Test
    void getPackageById_mapsAvailableSlotsCorrectly() {
        when(travelPackageRepository.findById("pkg-1")).thenReturn(Optional.of(pkg));
        assertEquals(20, travelPackageService.getPackageById("pkg-1").getAvailableSlots());
    }

    // ─── createPackage ────────────────────────────────────────────

    @Test
    void createPackage_savesAndReturnsPackage() {
        when(travelPackageRepository.save(any(TravelPackage.class))).thenReturn(pkg);
        TravelPackageResponseDTO result = travelPackageService.createPackage(requestDTO);
        assertNotNull(result);
        assertEquals("Paquete Cancún", result.getName());
    }

    @Test
    void createPackage_throwsExceptionWhenEndDateBeforeStartDate() {
        requestDTO.setEndDate(LocalDate.of(2026, 5, 1));
        assertThrows(RuntimeException.class,
                () -> travelPackageService.createPackage(requestDTO));
    }

    @Test
    void createPackage_setsAvailableSlotEqualTotalSlots() {
        when(travelPackageRepository.save(any(TravelPackage.class))).thenReturn(pkg);
        TravelPackageResponseDTO result = travelPackageService.createPackage(requestDTO);
        assertEquals(result.getTotalSlots(), result.getAvailableSlots());
    }

    @Test
    void createPackage_setsStatusAsAvailable() {
        when(travelPackageRepository.save(any(TravelPackage.class))).thenReturn(pkg);
        TravelPackageResponseDTO result = travelPackageService.createPackage(requestDTO);
        assertEquals("AVAILABLE", result.getStatus());
    }

    @Test
    void createPackage_callsSaveOnce() {
        when(travelPackageRepository.save(any(TravelPackage.class))).thenReturn(pkg);
        travelPackageService.createPackage(requestDTO);
        verify(travelPackageRepository, times(1)).save(any(TravelPackage.class));
    }

    @Test
    void createPackage_throwsExceptionWhenSameDates() {
        requestDTO.setEndDate(LocalDate.of(2026, 6, 1));
        requestDTO.setStartDate(LocalDate.of(2026, 6, 1));
        assertThrows(RuntimeException.class,
                () -> travelPackageService.createPackage(requestDTO));
    }

    // ─── updatePackage ────────────────────────────────────────────

    @Test
    void updatePackage_updatesAndReturnsPackage() {
        when(travelPackageRepository.findById("pkg-1")).thenReturn(Optional.of(pkg));
        when(travelPackageRepository.save(any(TravelPackage.class))).thenReturn(pkg);
        TravelPackageResponseDTO result = travelPackageService.updatePackage("pkg-1", requestDTO);
        assertNotNull(result);
    }

    @Test
    void updatePackage_throwsNotFoundWhenDoesNotExist() {
        when(travelPackageRepository.findById("bad-id")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> travelPackageService.updatePackage("bad-id", requestDTO));
    }

    @Test
    void updatePackage_throwsExceptionWhenEndDateBeforeStartDate() {
        when(travelPackageRepository.findById("pkg-1")).thenReturn(Optional.of(pkg));
        requestDTO.setEndDate(LocalDate.of(2026, 5, 1));
        assertThrows(RuntimeException.class,
                () -> travelPackageService.updatePackage("pkg-1", requestDTO));
    }

    @Test
    void updatePackage_callsSaveOnce() {
        when(travelPackageRepository.findById("pkg-1")).thenReturn(Optional.of(pkg));
        when(travelPackageRepository.save(any(TravelPackage.class))).thenReturn(pkg);
        travelPackageService.updatePackage("pkg-1", requestDTO);
        verify(travelPackageRepository, times(1)).save(any(TravelPackage.class));
    }

    @Test
    void updatePackage_updatesNameCorrectly() {
        when(travelPackageRepository.findById("pkg-1")).thenReturn(Optional.of(pkg));
        requestDTO.setName("Nuevo nombre");
        pkg.setName("Nuevo nombre");
        when(travelPackageRepository.save(any(TravelPackage.class))).thenReturn(pkg);
        TravelPackageResponseDTO result = travelPackageService.updatePackage("pkg-1", requestDTO);
        assertEquals("Nuevo nombre", result.getName());
    }

    // ─── changePackageStatus ──────────────────────────────────────

    @Test
    void changePackageStatus_changesToSoldOut() {
        when(travelPackageRepository.findById("pkg-1")).thenReturn(Optional.of(pkg));
        when(travelPackageRepository.save(any(TravelPackage.class))).thenReturn(pkg);
        assertDoesNotThrow(() ->
                travelPackageService.changePackageStatus("pkg-1", "SOLD_OUT"));
        verify(travelPackageRepository).save(any(TravelPackage.class));
    }

    @Test
    void changePackageStatus_changesToCancelled() {
        when(travelPackageRepository.findById("pkg-1")).thenReturn(Optional.of(pkg));
        when(travelPackageRepository.save(any(TravelPackage.class))).thenReturn(pkg);
        assertDoesNotThrow(() ->
                travelPackageService.changePackageStatus("pkg-1", "CANCELLED"));
    }

    @Test
    void changePackageStatus_throwsNotFoundWhenDoesNotExist() {
        when(travelPackageRepository.findById("bad-id")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> travelPackageService.changePackageStatus("bad-id", "CANCELLED"));
    }

    @Test
    void changePackageStatus_throwsExceptionForInvalidStatus() {
        when(travelPackageRepository.findById("pkg-1")).thenReturn(Optional.of(pkg));
        assertThrows(Exception.class,
                () -> travelPackageService.changePackageStatus("pkg-1", "INVALID"));
    }

    @Test
    void changePackageStatus_callsSaveOnce() {
        when(travelPackageRepository.findById("pkg-1")).thenReturn(Optional.of(pkg));
        when(travelPackageRepository.save(any(TravelPackage.class))).thenReturn(pkg);
        travelPackageService.changePackageStatus("pkg-1", "AVAILABLE");
        verify(travelPackageRepository, times(1)).save(any(TravelPackage.class));
    }

    // ─── deletePackage ────────────────────────────────────────────

    @Test
    void deletePackage_deletesWhenNoReservations() {
        when(travelPackageRepository.findById("pkg-1")).thenReturn(Optional.of(pkg));
        when(travelPackageRepository.existsByIdAndStatusNot(
                anyString(), any())).thenReturn(false);
        assertDoesNotThrow(() -> travelPackageService.deletePackage("pkg-1"));
        verify(travelPackageRepository, times(1)).delete(pkg);
    }

    @Test
    void deletePackage_throwsNotFoundWhenDoesNotExist() {
        when(travelPackageRepository.findById("bad-id")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> travelPackageService.deletePackage("bad-id"));
    }

    @Test
    void deletePackage_throwsExceptionWhenHasReservations() {
        when(travelPackageRepository.findById("pkg-1")).thenReturn(Optional.of(pkg));
        when(travelPackageRepository.existsByIdAndStatusNot(
                anyString(), any())).thenReturn(true);
        assertThrows(RuntimeException.class,
                () -> travelPackageService.deletePackage("pkg-1"));
    }

    @Test
    void deletePackage_doesNotDeleteWhenHasReservations() {
        when(travelPackageRepository.findById("pkg-1")).thenReturn(Optional.of(pkg));
        when(travelPackageRepository.existsByIdAndStatusNot(
                anyString(), any())).thenReturn(true);
        assertThrows(RuntimeException.class,
                () -> travelPackageService.deletePackage("pkg-1"));
        verify(travelPackageRepository, never()).delete(any());
    }

    @Test
    void deletePackage_callsDeleteOnce() {
        when(travelPackageRepository.findById("pkg-1")).thenReturn(Optional.of(pkg));
        when(travelPackageRepository.existsByIdAndStatusNot(
                anyString(), any())).thenReturn(false);
        travelPackageService.deletePackage("pkg-1");
        verify(travelPackageRepository, times(1)).delete(pkg);
    }
}