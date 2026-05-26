package com.example.demo.controller;

import com.example.demo.dto.request.ReservationRequestDTO;
import com.example.demo.dto.response.ReservationResponseDTO;
import com.example.demo.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")

public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    public ResponseEntity<List<ReservationResponseDTO>> getAllReservations() {
        return ResponseEntity.ok(reservationService.getAllReservations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponseDTO> getReservationById(@PathVariable String id) {
        return ResponseEntity.ok(reservationService.getReservationById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReservationResponseDTO>> getReservationsByUser(
            @PathVariable String userId) {
        return ResponseEntity.ok(reservationService.getReservationsByUser(userId));
    }

    @PostMapping
    public ResponseEntity<ReservationResponseDTO> createReservation(
            @Valid @RequestBody ReservationRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reservationService.createReservation(request));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelReservation(@PathVariable String id) {
        reservationService.cancelReservation(id);
        return ResponseEntity.noContent().build();
    }
}