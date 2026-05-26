package com.example.demo.service.impl;

import com.example.demo.dto.request.UserRequestDTO;
import com.example.demo.dto.response.UserResponseDTO;
import com.example.demo.exception.BusinessException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String keycloakIssuerUri;

    @Value("${keycloak.client-secret}")
    private String keycloakClientSecret;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ── Obtiene un token de admin para llamar a la Admin API de Keycloak ──
    private String getAdminToken() {
        String masterTokenUrl = keycloakIssuerUri
                .replaceAll("/realms/.*", "/realms/master/protocol/openid-connect/token");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("client_id", "admin-cli");
        body.add("username", "admin");
        body.add("password", "admin");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(masterTokenUrl, request, Map.class);
            if (response != null && response.containsKey("access_token")) {
                return (String) response.get("access_token");
            }
        } catch (Exception e) {
            System.err.println("Warning: Could not get admin token for Keycloak: " + e.getMessage());
        }
        return null;
    }

    // ── Crea el usuario en Keycloak ──
    private void createUserInKeycloak(UserRequestDTO request, String adminToken) {
        if (adminToken == null) return;

        String adminUrl = keycloakIssuerUri
                .replaceAll("/realms/.*", "/admin/realms/Tingeso_Db/users");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(adminToken);

        String firstName = request.getFullName().contains(" ")
                ? request.getFullName().split(" ")[0]
                : request.getFullName();
        String lastName = request.getFullName().contains(" ")
                ? request.getFullName().substring(request.getFullName().indexOf(" ") + 1)
                : "";

        Map<String, Object> keycloakUser = Map.of(
                "username", request.getEmail(),
                "email", request.getEmail(),
                "firstName", firstName,
                "lastName", lastName,
                "enabled", true,
                "emailVerified", true,
                "requiredActions", List.of(),
                "credentials", List.of(Map.of(
                        "type", "password",
                        "value", request.getPassword(),
                        "temporary", false
                ))
        );

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(keycloakUser, headers);

        try {
            restTemplate.postForEntity(adminUrl, requestEntity, Void.class);
        } catch (Exception e) {
            System.err.println("Warning: Could not create user in Keycloak: " + e.getMessage());
        }
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDTO getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        return mapToResponseDTO(user);
    }

    @Override
    public UserResponseDTO createUser(UserRequestDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already exists: " + request.getEmail());
        }

        // 1. Crear en PostgreSQL
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setPhone(request.getPhone());
        user.setDocumentId(request.getDocumentId());
        user.setNationality(request.getNationality());
        user.setRole(User.Role.valueOf(request.getRole().toUpperCase()));
        user.setStatus(User.Status.ACTIVE);
        UserResponseDTO saved = mapToResponseDTO(userRepository.save(user));

        // 2. Crear en Keycloak (no bloquea si falla)
        String adminToken = getAdminToken();
        createUserInKeycloak(request, adminToken);

        return saved;
    }

    @Override
    public UserResponseDTO updateUser(String id, UserRequestDTO request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        if (!user.getEmail().equals(request.getEmail()) &&
                userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already exists: " + request.getEmail());
        }
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setDocumentId(request.getDocumentId());
        user.setNationality(request.getNationality());
        return mapToResponseDTO(userRepository.save(user));
    }

    @Override
    public void changeUserStatus(String id, String status) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        user.setStatus(User.Status.valueOf(status.toUpperCase()));
        userRepository.save(user);
    }

    private UserResponseDTO mapToResponseDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setDocumentId(user.getDocumentId());
        dto.setNationality(user.getNationality());
        dto.setRole(user.getRole().name());
        dto.setStatus(user.getStatus().name());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}
