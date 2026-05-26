package com.example.demo.service.impl;

import com.example.demo.dto.request.TravelPackageRequestDTO;
import com.example.demo.dto.response.TravelPackageResponseDTO;
import com.example.demo.exception.BusinessException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.TravelPackage;
import com.example.demo.repository.TravelPackageRepository;
import com.example.demo.service.TravelPackageService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TravelPackageServiceImpl implements TravelPackageService {

    private final TravelPackageRepository travelPackageRepository;

    public TravelPackageServiceImpl(TravelPackageRepository travelPackageRepository) {
        this.travelPackageRepository = travelPackageRepository;
    }

    @Override
    public List<TravelPackageResponseDTO> getAllPackages() {
        return travelPackageRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TravelPackageResponseDTO getPackageById(String id) {
        TravelPackage pkg = travelPackageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Package", id));
        return mapToResponseDTO(pkg);
    }

    @Override
    public List<TravelPackageResponseDTO> getAvailablePackages(String destination, String startDate,
                                                               String endDate, String minPrice,
                                                               String maxPrice, String type) {
        LocalDate parsedStartDate = startDate != null ? LocalDate.parse(startDate) : null;
        LocalDate parsedEndDate = endDate != null ? LocalDate.parse(endDate) : null;
        BigDecimal parsedMinPrice = minPrice != null ? new BigDecimal(minPrice) : null;
        BigDecimal parsedMaxPrice = maxPrice != null ? new BigDecimal(maxPrice) : null;
        TravelPackage.PackageType parsedType = type != null ?
                TravelPackage.PackageType.valueOf(type.toUpperCase()) : null;

        return travelPackageRepository.findAvailableWithFilters(
                        destination, parsedStartDate, parsedEndDate,
                        parsedMinPrice, parsedMaxPrice, parsedType)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TravelPackageResponseDTO createPackage(TravelPackageRequestDTO request) {
        if (request.getEndDate().isBefore(request.getStartDate()) ||
                request.getEndDate().isEqual(request.getStartDate())) {
            throw new BusinessException("End date must be after start date");
        }

        TravelPackage pkg = new TravelPackage();
        pkg.setName(request.getName());
        pkg.setDestination(request.getDestination());
        pkg.setDescription(request.getDescription());
        pkg.setStartDate(request.getStartDate());
        pkg.setEndDate(request.getEndDate());
        pkg.setPrice(request.getPrice());
        pkg.setTotalSlots(request.getTotalSlots());
        pkg.setAvailableSlots(request.getTotalSlots());
        pkg.setIncludedServices(request.getIncludedServices());
        pkg.setConditions(request.getConditions());
        pkg.setRestrictions(request.getRestrictions());
        if (request.getType() != null) {
            pkg.setType(TravelPackage.PackageType.valueOf(request.getType().toUpperCase()));
        }
        pkg.setStatus(TravelPackage.PackageStatus.AVAILABLE);

        return mapToResponseDTO(travelPackageRepository.save(pkg));
    }

    @Override
    public TravelPackageResponseDTO updatePackage(String id, TravelPackageRequestDTO request) {
        TravelPackage pkg = travelPackageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Package", id));

        if (request.getEndDate().isBefore(request.getStartDate()) ||
                request.getEndDate().isEqual(request.getStartDate())) {
            throw new BusinessException("End date must be after start date");
        }

        pkg.setName(request.getName());
        pkg.setDestination(request.getDestination());
        pkg.setDescription(request.getDescription());
        pkg.setStartDate(request.getStartDate());
        pkg.setEndDate(request.getEndDate());
        pkg.setPrice(request.getPrice());
        pkg.setIncludedServices(request.getIncludedServices());
        pkg.setConditions(request.getConditions());
        pkg.setRestrictions(request.getRestrictions());
        if (request.getType() != null) {
            pkg.setType(TravelPackage.PackageType.valueOf(request.getType().toUpperCase()));
        }

        return mapToResponseDTO(travelPackageRepository.save(pkg));
    }

    @Override
    public void changePackageStatus(String id, String status) {
        TravelPackage pkg = travelPackageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Package", id));
        pkg.setStatus(TravelPackage.PackageStatus.valueOf(status.toUpperCase()));
        travelPackageRepository.save(pkg);
    }

    @Override
    public void deletePackage(String id) {
        TravelPackage pkg = travelPackageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Package", id));

        boolean hasReservations = travelPackageRepository
                .existsByIdAndStatusNot(id, TravelPackage.PackageStatus.CANCELLED);

        if (hasReservations) {
            throw new BusinessException("Cannot delete package with existing reservations");
        }

        travelPackageRepository.delete(pkg);
    }

    private TravelPackageResponseDTO mapToResponseDTO(TravelPackage pkg) {
        TravelPackageResponseDTO dto = new TravelPackageResponseDTO();
        dto.setId(pkg.getId());
        dto.setName(pkg.getName());
        dto.setDestination(pkg.getDestination());
        dto.setDescription(pkg.getDescription());
        dto.setStartDate(pkg.getStartDate());
        dto.setEndDate(pkg.getEndDate());
        dto.setPrice(pkg.getPrice());
        dto.setTotalSlots(pkg.getTotalSlots());
        dto.setAvailableSlots(pkg.getAvailableSlots());
        dto.setIncludedServices(pkg.getIncludedServices());
        dto.setConditions(pkg.getConditions());
        dto.setRestrictions(pkg.getRestrictions());
        dto.setType(pkg.getType() != null ? pkg.getType().name() : null);
        dto.setStatus(pkg.getStatus().name());
        dto.setCreatedAt(pkg.getCreatedAt());
        return dto;
    }
}
