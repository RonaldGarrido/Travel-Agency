package com.example.demo.service;

import com.example.demo.dto.request.TravelPackageRequestDTO;
import com.example.demo.dto.response.TravelPackageResponseDTO;

import java.util.List;

public interface TravelPackageService {

    List<TravelPackageResponseDTO> getAllPackages();

    TravelPackageResponseDTO getPackageById(String id);

    List<TravelPackageResponseDTO> getAvailablePackages(String destination, String startDate,
                                                        String endDate, String minPrice,
                                                        String maxPrice, String type);

    TravelPackageResponseDTO createPackage(TravelPackageRequestDTO request);

    TravelPackageResponseDTO updatePackage(String id, TravelPackageRequestDTO request);

    void changePackageStatus(String id, String status);

    void deletePackage(String id);
}