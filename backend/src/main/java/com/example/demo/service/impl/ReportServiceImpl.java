package com.example.demo.service.impl;

import com.example.demo.dto.response.PackageRankingDTO;
import com.example.demo.dto.response.SalesReportDTO;
import com.example.demo.model.Reservation;
import com.example.demo.repository.ReservationRepository;
import com.example.demo.service.ReportService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    private final ReservationRepository reservationRepository;

    public ReportServiceImpl(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @Override
    public List<SalesReportDTO> getSalesReport(String startDate, String endDate) {
        LocalDateTime start = LocalDate.parse(startDate).atStartOfDay();
        LocalDateTime end = LocalDate.parse(endDate).atTime(LocalTime.MAX);

        return reservationRepository.findByDateRangeExcludingCancelled(start, end)
                .stream()
                .map(this::mapToSalesReportDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PackageRankingDTO> getPackageRanking(String startDate, String endDate) {
        LocalDateTime start = LocalDate.parse(startDate).atStartOfDay();
        LocalDateTime end = LocalDate.parse(endDate).atTime(LocalTime.MAX);

        List<Reservation> reservations = reservationRepository
                .findByDateRangeExcludingCancelled(start, end);

        Map<String, List<Reservation>> groupedByPackage = reservations.stream()
                .collect(Collectors.groupingBy(r -> r.getTravelPackage().getId()));

        return groupedByPackage.entrySet().stream()
                .map(entry -> {
                    List<Reservation> pkgReservations = entry.getValue();
                    Reservation first = pkgReservations.get(0);

                    PackageRankingDTO dto = new PackageRankingDTO();
                    dto.setPackageId(entry.getKey());
                    dto.setPackageName(first.getTravelPackage().getName());
                    dto.setDestination(first.getTravelPackage().getDestination());
                    dto.setTotalReservations((long) pkgReservations.size());
                    dto.setTotalPassengers(pkgReservations.stream()
                            .mapToLong(Reservation::getPassengerCount).sum());
                    dto.setTotalRevenue(pkgReservations.stream()
                            .map(Reservation::getFinalAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add));
                    return dto;
                })
                .sorted(Comparator.comparingLong(PackageRankingDTO::getTotalReservations)
                        .reversed()
                        .thenComparing(PackageRankingDTO::getPackageName))
                .collect(Collectors.toList());
    }

    private SalesReportDTO mapToSalesReportDTO(Reservation reservation) {
        SalesReportDTO dto = new SalesReportDTO();
        dto.setReservationId(reservation.getId());
        dto.setClientName(reservation.getUser().getFullName());
        dto.setPackageName(reservation.getTravelPackage().getName());
        dto.setPassengerCount(reservation.getPassengerCount());
        dto.setTotalAmount(reservation.getFinalAmount());
        dto.setPaidAmount(reservation.getStatus() == Reservation.ReservationStatus.CONFIRMED
                ? reservation.getFinalAmount() : BigDecimal.ZERO);
        dto.setStatus(reservation.getStatus().name());
        dto.setDate(reservation.getCreatedAt());
        return dto;
    }
}
