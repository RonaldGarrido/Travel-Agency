package com.example.demo.service;

import com.example.demo.dto.request.ReservationRequestDTO;
import com.example.demo.dto.response.ReservationResponseDTO;

import java.util.List;

public interface ReservationService {

    List<ReservationResponseDTO> getAllReservations();

    ReservationResponseDTO getReservationById(String id);

    List<ReservationResponseDTO> getReservationsByUser(String userId);

    ReservationResponseDTO createReservation(ReservationRequestDTO request);

    void cancelReservation(String id);
}