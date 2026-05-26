package com.example.demo.controller;

import com.example.demo.dto.request.TravelPackageRequestDTO;
import com.example.demo.dto.response.TravelPackageResponseDTO;
import com.example.demo.service.TravelPackageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/packages")
public class TravelPackageController {

    private final TravelPackageService travelPackageService;

    public TravelPackageController(TravelPackageService travelPackageService) {
        this.travelPackageService = travelPackageService;
    }

    @GetMapping
    public ResponseEntity<List<TravelPackageResponseDTO>> getAllPackages() {
        return ResponseEntity.ok(travelPackageService.getAllPackages());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TravelPackageResponseDTO> getPackageById(@PathVariable String id) {
        return ResponseEntity.ok(travelPackageService.getPackageById(id));
    }

    @GetMapping("/available")
    public ResponseEntity<List<TravelPackageResponseDTO>> getAvailablePackages(
            @RequestParam(required = false) String destination,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String minPrice,
            @RequestParam(required = false) String maxPrice,
            @RequestParam(required = false) String type) {
        return ResponseEntity.ok(travelPackageService.getAvailablePackages(
                destination, startDate, endDate, minPrice, maxPrice, type));
    }

    @PostMapping
    public ResponseEntity<TravelPackageResponseDTO> createPackage(
            @Valid @RequestBody TravelPackageRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(travelPackageService.createPackage(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TravelPackageResponseDTO> updatePackage(
            @PathVariable String id,
            @Valid @RequestBody TravelPackageRequestDTO request) {
        return ResponseEntity.ok(travelPackageService.updatePackage(id, request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> changePackageStatus(@PathVariable String id,
                                                    @RequestParam String status) {
        travelPackageService.changePackageStatus(id, status);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePackage(@PathVariable String id) {
        travelPackageService.deletePackage(id);
        return ResponseEntity.noContent().build();
    }
}