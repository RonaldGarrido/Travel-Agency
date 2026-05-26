package com.example.demo.service;

import com.example.demo.dto.response.PackageRankingDTO;
import com.example.demo.dto.response.SalesReportDTO;
import com.example.demo.model.Reservation;
import com.example.demo.model.TravelPackage;
import com.example.demo.model.User;
import com.example.demo.repository.ReservationRepository;
import com.example.demo.service.impl.ReportServiceImpl;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private ReportServiceImpl reportService;

    private Reservation reservation1;
    private Reservation reservation2;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setId("user-1");
        user.setFullName("Juan Pérez");

        TravelPackage pkg1 = new TravelPackage();
        pkg1.setId("pkg-1");
        pkg1.setName("Paquete Cancún");
        pkg1.setDestination("Cancún, México");
        pkg1.setPrice(new BigDecimal("1500.00"));
        pkg1.setTotalSlots(20);
        pkg1.setAvailableSlots(18);
        pkg1.setStartDate(LocalDate.of(2026, 6, 1));
        pkg1.setEndDate(LocalDate.of(2026, 6, 10));
        pkg1.setStatus(TravelPackage.PackageStatus.AVAILABLE);

        TravelPackage pkg2 = new TravelPackage();
        pkg2.setId("pkg-2");
        pkg2.setName("Paquete París");
        pkg2.setDestination("París, Francia");
        pkg2.setPrice(new BigDecimal("3000.00"));
        pkg2.setTotalSlots(10);
        pkg2.setAvailableSlots(9);
        pkg2.setStartDate(LocalDate.of(2026, 7, 1));
        pkg2.setEndDate(LocalDate.of(2026, 7, 15));
        pkg2.setStatus(TravelPackage.PackageStatus.AVAILABLE);

        reservation1 = new Reservation();
        reservation1.setId("res-1");
        reservation1.setUser(user);
        reservation1.setTravelPackage(pkg1);
        reservation1.setPassengerCount(2);
        reservation1.setBaseAmount(new BigDecimal("3000.00"));
        reservation1.setDiscountAmount(BigDecimal.ZERO);
        reservation1.setFinalAmount(new BigDecimal("3000.00"));
        reservation1.setStatus(Reservation.ReservationStatus.CONFIRMED);
        reservation1.setCreatedAt(LocalDateTime.of(2026, 4, 1, 10, 0));
        reservation1.setExpiresAt(LocalDateTime.now().plusHours(24));

        reservation2 = new Reservation();
        reservation2.setId("res-2");
        reservation2.setUser(user);
        reservation2.setTravelPackage(pkg2);
        reservation2.setPassengerCount(1);
        reservation2.setBaseAmount(new BigDecimal("3000.00"));
        reservation2.setDiscountAmount(BigDecimal.ZERO);
        reservation2.setFinalAmount(new BigDecimal("3000.00"));
        reservation2.setStatus(Reservation.ReservationStatus.CONFIRMED);
        reservation2.setCreatedAt(LocalDateTime.of(2026, 4, 2, 10, 0));
        reservation2.setExpiresAt(LocalDateTime.now().plusHours(24));
    }

    // ─── getSalesReport ───────────────────────────────────────────

    @Test
    void getSalesReport_returnsListOfSales() {
        when(reservationRepository.findByDateRangeExcludingCancelled(any(), any()))
                .thenReturn(List.of(reservation1, reservation2));
        List<SalesReportDTO> result =
                reportService.getSalesReport("2026-04-01", "2026-04-30");
        assertEquals(2, result.size());
    }

    @Test
    void getSalesReport_returnsEmptyListWhenNoData() {
        when(reservationRepository.findByDateRangeExcludingCancelled(any(), any()))
                .thenReturn(List.of());
        List<SalesReportDTO> result =
                reportService.getSalesReport("2026-04-01", "2026-04-30");
        assertTrue(result.isEmpty());
    }

    @Test
    void getSalesReport_mapsClientNameCorrectly() {
        when(reservationRepository.findByDateRangeExcludingCancelled(any(), any()))
                .thenReturn(List.of(reservation1));
        SalesReportDTO dto = reportService.getSalesReport("2026-04-01", "2026-04-30").get(0);
        assertEquals("Juan Pérez", dto.getClientName());
    }

    @Test
    void getSalesReport_mapsPackageNameCorrectly() {
        when(reservationRepository.findByDateRangeExcludingCancelled(any(), any()))
                .thenReturn(List.of(reservation1));
        SalesReportDTO dto = reportService.getSalesReport("2026-04-01", "2026-04-30").get(0);
        assertEquals("Paquete Cancún", dto.getPackageName());
    }

    @Test
    void getSalesReport_mapsPaidAmountForConfirmedReservation() {
        when(reservationRepository.findByDateRangeExcludingCancelled(any(), any()))
                .thenReturn(List.of(reservation1));
        SalesReportDTO dto = reportService.getSalesReport("2026-04-01", "2026-04-30").get(0);
        assertEquals(new BigDecimal("3000.00"), dto.getPaidAmount());
    }

    @Test
    void getSalesReport_mapsPaidAmountAsZeroForPendingReservation() {
        reservation1.setStatus(Reservation.ReservationStatus.PENDING_PAYMENT);
        when(reservationRepository.findByDateRangeExcludingCancelled(any(), any()))
                .thenReturn(List.of(reservation1));
        SalesReportDTO dto = reportService.getSalesReport("2026-04-01", "2026-04-30").get(0);
        assertEquals(BigDecimal.ZERO, dto.getPaidAmount());
    }

    @Test
    void getSalesReport_callsRepositoryOnce() {
        when(reservationRepository.findByDateRangeExcludingCancelled(any(), any()))
                .thenReturn(List.of());
        reportService.getSalesReport("2026-04-01", "2026-04-30");
        verify(reservationRepository, times(1))
                .findByDateRangeExcludingCancelled(any(), any());
    }

    // ─── getPackageRanking ────────────────────────────────────────

    @Test
    void getPackageRanking_returnsRankedList() {
        when(reservationRepository.findByDateRangeExcludingCancelled(any(), any()))
                .thenReturn(List.of(reservation1, reservation2));
        List<PackageRankingDTO> result =
                reportService.getPackageRanking("2026-04-01", "2026-04-30");
        assertEquals(2, result.size());
    }

    @Test
    void getPackageRanking_returnsEmptyListWhenNoData() {
        when(reservationRepository.findByDateRangeExcludingCancelled(any(), any()))
                .thenReturn(List.of());
        assertTrue(reportService.getPackageRanking("2026-04-01", "2026-04-30").isEmpty());
    }

    @Test
    void getPackageRanking_groupsByPackageCorrectly() {
        Reservation extra = new Reservation();
        extra.setId("res-3");
        extra.setUser(reservation1.getUser());
        extra.setTravelPackage(reservation1.getTravelPackage());
        extra.setPassengerCount(3);
        extra.setFinalAmount(new BigDecimal("4500.00"));
        extra.setBaseAmount(new BigDecimal("4500.00"));
        extra.setDiscountAmount(BigDecimal.ZERO);
        extra.setStatus(Reservation.ReservationStatus.CONFIRMED);
        extra.setCreatedAt(LocalDateTime.of(2026, 4, 3, 10, 0));
        extra.setExpiresAt(LocalDateTime.now().plusHours(24));
        when(reservationRepository.findByDateRangeExcludingCancelled(any(), any()))
                .thenReturn(List.of(reservation1, reservation2, extra));
        List<PackageRankingDTO> result =
                reportService.getPackageRanking("2026-04-01", "2026-04-30");
        PackageRankingDTO top = result.get(0);
        assertEquals("pkg-1", top.getPackageId());
        assertEquals(2L, top.getTotalReservations());
    }

    @Test
    void getPackageRanking_ordersDescendingByReservations() {
        Reservation extra = new Reservation();
        extra.setId("res-3");
        extra.setUser(reservation1.getUser());
        extra.setTravelPackage(reservation1.getTravelPackage());
        extra.setPassengerCount(1);
        extra.setFinalAmount(new BigDecimal("1500.00"));
        extra.setBaseAmount(new BigDecimal("1500.00"));
        extra.setDiscountAmount(BigDecimal.ZERO);
        extra.setStatus(Reservation.ReservationStatus.CONFIRMED);
        extra.setCreatedAt(LocalDateTime.of(2026, 4, 4, 10, 0));
        extra.setExpiresAt(LocalDateTime.now().plusHours(24));
        when(reservationRepository.findByDateRangeExcludingCancelled(any(), any()))
                .thenReturn(List.of(reservation1, reservation2, extra));
        List<PackageRankingDTO> result =
                reportService.getPackageRanking("2026-04-01", "2026-04-30");
        assertTrue(result.get(0).getTotalReservations() >=
                result.get(1).getTotalReservations());
    }

    @Test
    void getPackageRanking_sumsTotalPassengersCorrectly() {
        when(reservationRepository.findByDateRangeExcludingCancelled(any(), any()))
                .thenReturn(List.of(reservation1));
        List<PackageRankingDTO> result =
                reportService.getPackageRanking("2026-04-01", "2026-04-30");
        assertEquals(2L, result.get(0).getTotalPassengers());
    }

    @Test
    void getPackageRanking_sumsTotalRevenueCorrectly() {
        when(reservationRepository.findByDateRangeExcludingCancelled(any(), any()))
                .thenReturn(List.of(reservation1));
        List<PackageRankingDTO> result =
                reportService.getPackageRanking("2026-04-01", "2026-04-30");
        assertEquals(new BigDecimal("3000.00"), result.get(0).getTotalRevenue());
    }
}