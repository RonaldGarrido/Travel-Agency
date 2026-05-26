package com.example.demo.service;

import com.example.demo.dto.request.UserRequestDTO;
import com.example.demo.dto.response.UserResponseDTO;

import java.util.List;

public interface UserService {

    List<UserResponseDTO> getAllUsers();

    UserResponseDTO getUserById(String id);

    UserResponseDTO createUser(UserRequestDTO request);

    UserResponseDTO updateUser(String id, UserRequestDTO request);

    void changeUserStatus(String id, String status);
}