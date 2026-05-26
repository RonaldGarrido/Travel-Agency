package com.example.demo.repository;

import com.example.demo.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, String> {

    List<Reservation> findByUserId(String userId);

    List<Reservation> findByTravelPackageId(String packageId);

    List<Reservation> findByStatus(Reservation.ReservationStatus status);

    // Cuenta reservas pagadas de un usuario (para detectar cliente frecuente)
    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.user.id = :userId " +
            "AND r.status = 'CONFIRMED'")
    long countConfirmedReservationsByUser(@Param("userId") String userId);

    // Reservas dentro de un rango de fechas para reportes
    @Query("SELECT r FROM Reservation r WHERE r.createdAt BETWEEN :startDate AND :endDate " +
            "AND r.status != 'CANCELLED'")
    List<Reservation> findByDateRangeExcludingCancelled(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // Reservas expiradas que aún están en PENDING_PAYMENT
    @Query("SELECT r FROM Reservation r WHERE r.status = 'PENDING_PAYMENT' " +
            "AND r.expiresAt < :now")
    List<Reservation> findExpiredReservations(@Param("now") LocalDateTime now);
}