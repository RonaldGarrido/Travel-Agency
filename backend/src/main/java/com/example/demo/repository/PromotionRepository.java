package com.example.demo.repository;

import com.example.demo.model.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, String> {

    List<Promotion> findByTravelPackageId(String packageId);

    // Busca promociones activas para un paquete en una fecha dada
    @Query("SELECT p FROM Promotion p WHERE p.travelPackage.id = :packageId " +
            "AND :today BETWEEN p.startDate AND p.endDate")
    List<Promotion> findActivePromotionsByPackage(
            @Param("packageId") String packageId,
            @Param("today") LocalDate today);
}