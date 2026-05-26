package com.example.demo.service.impl;

import com.example.demo.dto.request.ReservationRequestDTO;
import com.example.demo.dto.response.ReservationResponseDTO;
import com.example.demo.exception.BusinessException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Reservation;
import com.example.demo.model.TravelPackage;
import com.example.demo.model.User;
import com.example.demo.repository.PromotionRepository;
import com.example.demo.repository.ReservationRepository;
import com.example.demo.repository.TravelPackageRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.ReservationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationServiceImpl implements ReservationService {

    private static final int FREQUENT_CLIENT_THRESHOLD = 3;
    private static final BigDecimal FREQUENT_CLIENT_DISCOUNT = new BigDecimal("0.10");
    private static final int GROUP_THRESHOLD = 4;
    private static final BigDecimal GROUP_DISCOUNT = new BigDecimal("0.05");
    private static final BigDecimal MAX_DISCOUNT = new BigDecimal("0.20");

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final TravelPackageRepository travelPackageRepository;
    private final PromotionRepository promotionRepository;

    public ReservationServiceImpl(ReservationRepository reservationRepository,
                                  UserRepository userRepository,
                                  TravelPackageRepository travelPackageRepository,
                                  PromotionRepository promotionRepository) {
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
        this.travelPackageRepository = travelPackageRepository;
        this.promotionRepository = promotionRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationResponseDTO> getAllReservations() {
        return reservationRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ReservationResponseDTO getReservationById(String id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + id));
        return mapToResponseDTO(reservation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationResponseDTO> getReservationsByUser(String userId) {
        User user = userRepository.findByEmail(userId)
		.orElseGet(() -> userRepository.findById(userId)
			.orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId)));
        return reservationRepository.findByUserId(user.getId())
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ReservationResponseDTO createReservation(ReservationRequestDTO request) {
       User user = userRepository.findById(request.getUserId())
        .orElseGet(() -> userRepository.findByEmail(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId())));

        TravelPackage pkg = travelPackageRepository.findById(request.getPackageId())
                .orElseThrow(() -> new ResourceNotFoundException("Package not found with id: " + request.getPackageId()));

        if (pkg.getStatus() != TravelPackage.PackageStatus.AVAILABLE) {
            throw new BusinessException("Package is not available for reservation");
        }
        if (pkg.getAvailableSlots() < request.getPassengerCount()) {
            throw new BusinessException("Not enough available slots");
        }

        BigDecimal baseAmount = pkg.getPrice()
                .multiply(new BigDecimal(request.getPassengerCount()));

        BigDecimal totalDiscountRate = calculateDiscountRate(
                user, request.getPassengerCount(), pkg.getId());

        BigDecimal discountAmount = baseAmount
                .multiply(totalDiscountRate)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal finalAmount = baseAmount.subtract(discountAmount);

        pkg.setAvailableSlots(pkg.getAvailableSlots() - request.getPassengerCount());
        if (pkg.getAvailableSlots() == 0) {
            pkg.setStatus(TravelPackage.PackageStatus.SOLD_OUT);
        }
        travelPackageRepository.save(pkg);

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setTravelPackage(pkg);
        reservation.setPassengerCount(request.getPassengerCount());
        reservation.setBaseAmount(baseAmount);
        reservation.setDiscountAmount(discountAmount);
        reservation.setFinalAmount(finalAmount);
        reservation.setDiscountDetail(buildDiscountDetail(
                user, request.getPassengerCount(), pkg.getId(), totalDiscountRate));
        reservation.setStatus(Reservation.ReservationStatus.PENDING_PAYMENT);

        return mapToResponseDTO(reservationRepository.save(reservation));
    }

    @Override
    @Transactional
    public void cancelReservation(String id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + id));

        if (reservation.getStatus() == Reservation.ReservationStatus.CANCELLED) {
            throw new BusinessException("Reservation is already cancelled");
        }

        TravelPackage pkg = reservation.getTravelPackage();
        pkg.setAvailableSlots(pkg.getAvailableSlots() + reservation.getPassengerCount());
        if (pkg.getStatus() == TravelPackage.PackageStatus.SOLD_OUT) {
            pkg.setStatus(TravelPackage.PackageStatus.AVAILABLE);
        }
        travelPackageRepository.save(pkg);

        reservation.setStatus(Reservation.ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);
    }

    private BigDecimal calculateDiscountRate(User user, int passengerCount, String packageId) {
        BigDecimal totalDiscount = BigDecimal.ZERO;

        if (passengerCount >= GROUP_THRESHOLD) {
            totalDiscount = totalDiscount.add(GROUP_DISCOUNT);
        }

        long confirmedReservations = reservationRepository
                .countConfirmedReservationsByUser(user.getId());
        if (confirmedReservations >= FREQUENT_CLIENT_THRESHOLD) {
            totalDiscount = totalDiscount.add(FREQUENT_CLIENT_DISCOUNT);
        }

        var activePromotions = promotionRepository
                .findActivePromotionsByPackage(packageId, LocalDate.now());
        if (!activePromotions.isEmpty()) {
            BigDecimal promoDiscount = activePromotions.get(0)
                    .getDiscountPercentage()
                    .divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
            totalDiscount = totalDiscount.add(promoDiscount);
        }

        return totalDiscount.min(MAX_DISCOUNT);
    }

    private String buildDiscountDetail(User user, int passengerCount,
                                       String packageId, BigDecimal totalRate) {
        StringBuilder detail = new StringBuilder();

        if (passengerCount >= GROUP_THRESHOLD) {
            detail.append("Group discount (5%). ");
        }

        long confirmedReservations = reservationRepository
                .countConfirmedReservationsByUser(user.getId());
        if (confirmedReservations >= FREQUENT_CLIENT_THRESHOLD) {
            detail.append("Frequent client discount (10%). ");
        }

        var activePromotions = promotionRepository
                .findActivePromotionsByPackage(packageId, LocalDate.now());
        if (!activePromotions.isEmpty()) {
            detail.append("Promotion discount (")
                    .append(activePromotions.get(0).getDiscountPercentage())
                    .append("%). ");
        }

        if (totalRate.compareTo(MAX_DISCOUNT) == 0) {
            detail.append("Maximum discount cap applied (20%).");
        }

        return detail.toString().trim();
    }

    private ReservationResponseDTO mapToResponseDTO(Reservation reservation) {
        ReservationResponseDTO dto = new ReservationResponseDTO();
        dto.setId(reservation.getId());
        dto.setUserId(reservation.getUser().getId());
        dto.setUserName(reservation.getUser().getFullName());
        dto.setPackageId(reservation.getTravelPackage().getId());
        dto.setPackageName(reservation.getTravelPackage().getName());
        dto.setPassengerCount(reservation.getPassengerCount());
        dto.setBaseAmount(reservation.getBaseAmount());
        dto.setDiscountAmount(reservation.getDiscountAmount());
        dto.setFinalAmount(reservation.getFinalAmount());
        dto.setDiscountDetail(reservation.getDiscountDetail());
        dto.setStatus(reservation.getStatus().name());
        dto.setCreatedAt(reservation.getCreatedAt());
        dto.setExpiresAt(reservation.getExpiresAt());
        return dto;
    }
}
