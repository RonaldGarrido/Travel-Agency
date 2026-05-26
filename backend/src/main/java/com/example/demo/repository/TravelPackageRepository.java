package com.example.demo.repository;

import com.example.demo.model.TravelPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface TravelPackageRepository extends JpaRepository<TravelPackage, String> {

    List<TravelPackage> findByStatus(TravelPackage.PackageStatus status);

    @Query("SELECT p FROM TravelPackage p WHERE p.status = 'AVAILABLE' " +
            "AND (:destination IS NULL OR LOWER(p.destination) LIKE LOWER(CONCAT('%', CAST(:destination AS string), '%'))) " +
            "AND (:startDate IS NULL OR p.startDate >= :startDate) " +
            "AND (:endDate IS NULL OR p.endDate <= :endDate) " +
            "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR p.price <= :maxPrice) " +
            "AND (:type IS NULL OR p.type = :type)")
    List<TravelPackage> findAvailableWithFilters(
            @Param("destination") String destination,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("type") TravelPackage.PackageType type);

    boolean existsByIdAndStatusNot(String id, TravelPackage.PackageStatus status);
}