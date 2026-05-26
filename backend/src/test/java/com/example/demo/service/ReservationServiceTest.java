package com.example.demo.service;

import com.example.demo.dto.request.ReservationRequestDTO;
import com.example.demo.dto.response.ReservationResponseDTO;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Reservation;
import com.example.demo.model.TravelPackage;
import com.example.demo.model.User;
import com.example.demo.repository.PromotionRepository;
import com.example.demo.repository.ReservationRepository;
import com.example.demo.repository.TravelPackageRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.impl.ReservationServiceImpl;
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
class ReservationServiceTest {

    @Mock private ReservationRepository reservationRepository;
    @Mock private UserRepository userRepository;
    @Mock private TravelPackageRepository travelPackageRepository;
    @Mock private PromotionRepository promotionRepository;

    @InjectMocks
    private ReservationServiceImpl reservationService;

    private User user;
    private TravelPackage pkg;
    private Reservation reservation;
    private ReservationRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId("user-1");
        user.setFullName("Juan Pérez");
        user.setEmail("juan@test.com");
        user.setRole(User.Role.CLIENT);
        user.setStatus(User.Status.ACTIVE);

        pkg = new TravelPackage();
        pkg.setId("pkg-1");
        pkg.setName("Paquete Cancún");
        pkg.setDestination("Cancún, México");
        pkg.setPrice(new BigDecimal("1500.00"));
        pkg.setTotalSlots(20);
        pkg.setAvailableSlots(20);
        pkg.setStatus(TravelPackage.PackageStatus.AVAILABLE);
        pkg.setStartDate(LocalDate.of(2026, 6, 1));
        pkg.setEndDate(LocalDate.of(2026, 6, 10));

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

        requestDTO = new ReservationRequestDTO();
        requestDTO.setUserId("user-1");
        requestDTO.setPackageId("pkg-1");
        requestDTO.setPassengerCount(2);
    }

    // ─── getAllReservations ────────────────────────────────────────

    @Test
    void getAllReservations_returnsListOfReservations() {
        when(reservationRepository.findAll()).thenReturn(List.of(reservation));
        List<ReservationResponseDTO> result = reservationService.getAllReservations();
        assertEquals(1, result.size());
    }

    @Test
    void getAllReservations_returnsEmptyList() {
        when(reservationRepository.findAll()).thenReturn(List.of());
        assertTrue(reservationService.getAllReservations().isEmpty());
    }

    @Test
    void getAllReservations_mapsPassengerCountCorrectly() {
        when(reservationRepository.findAll()).thenReturn(List.of(reservation));
        assertEquals(2, reservationService.getAllReservations().get(0).getPassengerCount());
    }

    @Test
    void getAllReservations_mapsFinalAmountCorrectly() {
        when(reservationRepository.findAll()).thenReturn(List.of(reservation));
        assertEquals(new BigDecimal("3000.00"),
                reservationService.getAllReservations().get(0).getFinalAmount());
    }

    @Test
    void getAllReservations_callsRepositoryOnce() {
        when(reservationRepository.findAll()).thenReturn(List.of());
        reservationService.getAllReservations();
        verify(reservationRepository, times(1)).findAll();
    }

    // ─── getReservationById ───────────────────────────────────────

    @Test
    void getReservationById_returnsReservationWhenExists() {
        when(reservationRepository.findById("res-1")).thenReturn(Optional.of(reservation));
        ReservationResponseDTO result = reservationService.getReservationById("res-1");
        assertEquals("res-1", result.getId());
    }

    @Test
    void getReservationById_throwsNotFoundWhenDoesNotExist() {
        when(reservationRepository.findById("bad-id")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> reservationService.getReservationById("bad-id"));
    }

    @Test
    void getReservationById_mapsUserNameCorrectly() {
        when(reservationRepository.findById("res-1")).thenReturn(Optional.of(reservation));
        assertEquals("Juan Pérez",
                reservationService.getReservationById("res-1").getUserName());
    }

    @Test
    void getReservationById_mapsPackageNameCorrectly() {
        when(reservationRepository.findById("res-1")).thenReturn(Optional.of(reservation));
        assertEquals("Paquete Cancún",
                reservationService.getReservationById("res-1").getPackageName());
    }

    @Test
    void getReservationById_mapsStatusCorrectly() {
        when(reservationRepository.findById("res-1")).thenReturn(Optional.of(reservation));
        assertEquals("PENDING_PAYMENT",
                reservationService.getReservationById("res-1").getStatus());
    }

    // ─── getReservationsByUser ────────────────────────────────────

    @Test
    void getReservationsByUser_returnsUserReservations() {
        when(reservationRepository.findByUserId("user-1")).thenReturn(List.of(reservation));
        List<ReservationResponseDTO> result =
                reservationService.getReservationsByUser("user-1");
        assertEquals(1, result.size());
    }

    @Test
    void getReservationsByUser_returnsEmptyWhenNoReservations() {
        when(reservationRepository.findByUserId("user-1")).thenReturn(List.of());
        assertTrue(reservationService.getReservationsByUser("user-1").isEmpty());
    }

    @Test
    void getReservationsByUser_callsRepositoryWithCorrectId() {
        when(reservationRepository.findByUserId("user-1")).thenReturn(List.of());
        reservationService.getReservationsByUser("user-1");
        verify(reservationRepository, times(1)).findByUserId("user-1");
    }

    @Test
    void getReservationsByUser_mapsAllReservations() {
        Reservation res2 = new Reservation();
        res2.setId("res-2");
        res2.setUser(user);
        res2.setTravelPackage(pkg);
        res2.setPassengerCount(1);
        res2.setBaseAmount(new BigDecimal("1500.00"));
        res2.setDiscountAmount(BigDecimal.ZERO);
        res2.setFinalAmount(new BigDecimal("1500.00"));
        res2.setStatus(Reservation.ReservationStatus.CONFIRMED);
        res2.setCreatedAt(LocalDateTime.now());
        res2.setExpiresAt(LocalDateTime.now().plusHours(24));
        when(reservationRepository.findByUserId("user-1"))
                .thenReturn(List.of(reservation, res2));
        assertEquals(2, reservationService.getReservationsByUser("user-1").size());
    }

    @Test
    void getReservationsByUser_mapsStatusCorrectly() {
        when(reservationRepository.findByUserId("user-1")).thenReturn(List.of(reservation));
        assertEquals("PENDING_PAYMENT",
                reservationService.getReservationsByUser("user-1").get(0).getStatus());
    }

    // ─── createReservation ────────────────────────────────────────

    @Test
    void createReservation_createsAndReturnsReservation() {
        when(userRepository.findById("user-1")).thenReturn(Optional.of(user));
        when(travelPackageRepository.findById("pkg-1")).thenReturn(Optional.of(pkg));
        when(reservationRepository.countConfirmedReservationsByUser(anyString())).thenReturn(0L);
        when(promotionRepository.findActivePromotionsByPackage(anyString(), any())).thenReturn(List.of());
        when(travelPackageRepository.save(any())).thenReturn(pkg);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);
        ReservationResponseDTO result = reservationService.createReservation(requestDTO);
        assertNotNull(result);
    }

    @Test
    void createReservation_throwsNotFoundWhenUserDoesNotExist() {
        when(userRepository.findById("user-1")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> reservationService.createReservation(requestDTO));
    }

    @Test
    void createReservation_throwsNotFoundWhenPackageDoesNotExist() {
        when(userRepository.findById("user-1")).thenReturn(Optional.of(user));
        when(travelPackageRepository.findById("pkg-1")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> reservationService.createReservation(requestDTO));
    }

    @Test
    void createReservation_throwsExceptionWhenPackageNotAvailable() {
        pkg.setStatus(TravelPackage.PackageStatus.SOLD_OUT);
        when(userRepository.findById("user-1")).thenReturn(Optional.of(user));
        when(travelPackageRepository.findById("pkg-1")).thenReturn(Optional.of(pkg));
        assertThrows(RuntimeException.class,
                () -> reservationService.createReservation(requestDTO));
    }

    @Test
    void createReservation_throwsExceptionWhenNotEnoughSlots() {
        pkg.setAvailableSlots(1);
        requestDTO.setPassengerCount(5);
        when(userRepository.findById("user-1")).thenReturn(Optional.of(user));
        when(travelPackageRepository.findById("pkg-1")).thenReturn(Optional.of(pkg));
        assertThrows(RuntimeException.class,
                () -> reservationService.createReservation(requestDTO));
    }

    @Test
    void createReservation_appliesGroupDiscountWhen4OrMorePassengers() {
        requestDTO.setPassengerCount(4);
        pkg.setAvailableSlots(10);
        Reservation discountedRes = new Reservation();
        discountedRes.setId("res-disc");
        discountedRes.setUser(user);
        discountedRes.setTravelPackage(pkg);
        discountedRes.setPassengerCount(4);
        discountedRes.setBaseAmount(new BigDecimal("6000.00"));
        discountedRes.setDiscountAmount(new BigDecimal("300.00"));
        discountedRes.setFinalAmount(new BigDecimal("5700.00"));
        discountedRes.setStatus(Reservation.ReservationStatus.PENDING_PAYMENT);
        discountedRes.setCreatedAt(LocalDateTime.now());
        discountedRes.setExpiresAt(LocalDateTime.now().plusHours(24));
        when(userRepository.findById("user-1")).thenReturn(Optional.of(user));
        when(travelPackageRepository.findById("pkg-1")).thenReturn(Optional.of(pkg));
        when(reservationRepository.countConfirmedReservationsByUser(anyString())).thenReturn(0L);
        when(promotionRepository.findActivePromotionsByPackage(anyString(), any())).thenReturn(List.of());
        when(travelPackageRepository.save(any())).thenReturn(pkg);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(discountedRes);
        ReservationResponseDTO result = reservationService.createReservation(requestDTO);
        assertTrue(result.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void createReservation_decreasesAvailableSlots() {
        when(userRepository.findById("user-1")).thenReturn(Optional.of(user));
        when(travelPackageRepository.findById("pkg-1")).thenReturn(Optional.of(pkg));
        when(reservationRepository.countConfirmedReservationsByUser(anyString())).thenReturn(0L);
        when(promotionRepository.findActivePromotionsByPackage(anyString(), any())).thenReturn(List.of());
        when(travelPackageRepository.save(any())).thenReturn(pkg);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);
        reservationService.createReservation(requestDTO);
        verify(travelPackageRepository, times(1)).save(any(TravelPackage.class));
    }

    // ─── cancelReservation ────────────────────────────────────────

    @Test
    void cancelReservation_cancelsSuccessfully() {
        when(reservationRepository.findById("res-1")).thenReturn(Optional.of(reservation));
        when(travelPackageRepository.save(any())).thenReturn(pkg);
        when(reservationRepository.save(any())).thenReturn(reservation);
        assertDoesNotThrow(() -> reservationService.cancelReservation("res-1"));
    }

    @Test
    void cancelReservation_throwsNotFoundWhenDoesNotExist() {
        when(reservationRepository.findById("bad-id")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> reservationService.cancelReservation("bad-id"));
    }

    @Test
    void cancelReservation_throwsExceptionWhenAlreadyCancelled() {
        reservation.setStatus(Reservation.ReservationStatus.CANCELLED);
        when(reservationRepository.findById("res-1")).thenReturn(Optional.of(reservation));
        assertThrows(RuntimeException.class,
                () -> reservationService.cancelReservation("res-1"));
    }

    @Test
    void cancelReservation_restoresSlotsOnCancel() {
        when(reservationRepository.findById("res-1")).thenReturn(Optional.of(reservation));
        when(travelPackageRepository.save(any())).thenReturn(pkg);
        when(reservationRepository.save(any())).thenReturn(reservation);
        reservationService.cancelReservation("res-1");
        verify(travelPackageRepository, times(1)).save(any(TravelPackage.class));
    }

    @Test
    void cancelReservation_savesReservationWithCancelledStatus() {
        when(reservationRepository.findById("res-1")).thenReturn(Optional.of(reservation));
        when(travelPackageRepository.save(any())).thenReturn(pkg);
        when(reservationRepository.save(any())).thenReturn(reservation);
        reservationService.cancelReservation("res-1");
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }
}