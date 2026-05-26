package com.example.demo.controller;

import com.example.demo.dto.request.UserRequestDTO;
import com.example.demo.dto.response.UserResponseDTO;
import com.example.demo.exception.GlobalExceptionHandler;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private UserResponseDTO responseDTO;
    private UserRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        responseDTO = new UserResponseDTO();
        responseDTO.setId("user-1");
        responseDTO.setFullName("Juan Pérez");
        responseDTO.setEmail("juan@test.com");
        responseDTO.setRole("CLIENT");
        responseDTO.setStatus("ACTIVE");
        responseDTO.setCreatedAt(LocalDateTime.now());

        requestDTO = new UserRequestDTO();
        requestDTO.setFullName("Juan Pérez");
        requestDTO.setEmail("juan@test.com");
        requestDTO.setPassword("12345678");
        requestDTO.setRole("CLIENT");
    }

    // ─── GET /api/users ───────────────────────────────────────────

    @Test
    void getAllUsers_returns200WithList() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(responseDTO));
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("user-1"));
    }

    @Test
    void getAllUsers_returnsEmptyList() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of());
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getAllUsers_returnsCorrectFullName() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(responseDTO));
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fullName").value("Juan Pérez"));
    }

    @Test
    void getAllUsers_callsServiceOnce() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of());
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk());
        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void getAllUsers_returnsCorrectRole() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(responseDTO));
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].role").value("CLIENT"));
    }

    // ─── GET /api/users/{id} ──────────────────────────────────────

    @Test
    void getUserById_returns200WhenExists() throws Exception {
        when(userService.getUserById("user-1")).thenReturn(responseDTO);
        mockMvc.perform(get("/api/users/user-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("user-1"));
    }

    @Test
    void getUserById_returns404WhenNotFound() throws Exception {
        when(userService.getUserById("bad-id"))
                .thenThrow(new ResourceNotFoundException("User", "bad-id"));
        mockMvc.perform(get("/api/users/bad-id"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getUserById_returnsCorrectEmail() throws Exception {
        when(userService.getUserById("user-1")).thenReturn(responseDTO);
        mockMvc.perform(get("/api/users/user-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("juan@test.com"));
    }

    @Test
    void getUserById_returnsCorrectStatus() throws Exception {
        when(userService.getUserById("user-1")).thenReturn(responseDTO);
        mockMvc.perform(get("/api/users/user-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void getUserById_callsServiceWithCorrectId() throws Exception {
        when(userService.getUserById("user-1")).thenReturn(responseDTO);
        mockMvc.perform(get("/api/users/user-1"))
                .andExpect(status().isOk());
        verify(userService, times(1)).getUserById("user-1");
    }

    // ─── POST /api/users ──────────────────────────────────────────

    @Test
    void createUser_returns201WhenCreated() throws Exception {
        when(userService.createUser(any(UserRequestDTO.class))).thenReturn(responseDTO);
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    void createUser_returnsCreatedUser() throws Exception {
        when(userService.createUser(any(UserRequestDTO.class))).thenReturn(responseDTO);
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(jsonPath("$.id").value("user-1"));
    }

    @Test
    void createUser_returns400WhenMissingFullName() throws Exception {
        requestDTO.setFullName(null);
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_returns400WhenInvalidEmail() throws Exception {
        requestDTO.setEmail("not-an-email");
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_returns400WhenPasswordTooShort() throws Exception {
        requestDTO.setPassword("123");
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }

    // ─── PUT /api/users/{id} ──────────────────────────────────────

    @Test
    void updateUser_returns200WhenUpdated() throws Exception {
        when(userService.updateUser(anyString(), any(UserRequestDTO.class)))
                .thenReturn(responseDTO);
        mockMvc.perform(put("/api/users/user-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk());
    }

    @Test
    void updateUser_returns404WhenNotFound() throws Exception {
        when(userService.updateUser(anyString(), any(UserRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException("User", "bad-id"));
        mockMvc.perform(put("/api/users/bad-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateUser_returnsUpdatedUser() throws Exception {
        when(userService.updateUser(anyString(), any(UserRequestDTO.class)))
                .thenReturn(responseDTO);
        mockMvc.perform(put("/api/users/user-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(jsonPath("$.fullName").value("Juan Pérez"));
    }

    @Test
    void updateUser_callsServiceWithCorrectId() throws Exception {
        when(userService.updateUser(anyString(), any(UserRequestDTO.class)))
                .thenReturn(responseDTO);
        mockMvc.perform(put("/api/users/user-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk());
        verify(userService, times(1)).updateUser(eq("user-1"), any());
    }

    @Test
    void updateUser_returns400WhenInvalidEmail() throws Exception {
        requestDTO.setEmail("bad-email");
        mockMvc.perform(put("/api/users/user-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }

    // ─── PATCH /api/users/{id}/status ─────────────────────────────

    @Test
    void changeUserStatus_returns204WhenChanged() throws Exception {
        doNothing().when(userService).changeUserStatus(anyString(), anyString());
        mockMvc.perform(patch("/api/users/user-1/status")
                        .param("status", "INACTIVE"))
                .andExpect(status().isNoContent());
    }

    @Test
    void changeUserStatus_returns404WhenNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("User", "bad-id"))
                .when(userService).changeUserStatus(anyString(), anyString());
        mockMvc.perform(patch("/api/users/bad-id/status")
                        .param("status", "INACTIVE"))
                .andExpect(status().isNotFound());
    }

    @Test
    void changeUserStatus_callsServiceOnce() throws Exception {
        doNothing().when(userService).changeUserStatus(anyString(), anyString());
        mockMvc.perform(patch("/api/users/user-1/status")
                        .param("status", "BLOCKED"))
                .andExpect(status().isNoContent());
        verify(userService, times(1)).changeUserStatus("user-1", "BLOCKED");
    }

    @Test
    void changeUserStatus_returns204ForActiveStatus() throws Exception {
        doNothing().when(userService).changeUserStatus(anyString(), anyString());
        mockMvc.perform(patch("/api/users/user-1/status")
                        .param("status", "ACTIVE"))
                .andExpect(status().isNoContent());
    }

    @Test
    void changeUserStatus_returns204ForBlockedStatus() throws Exception {
        doNothing().when(userService).changeUserStatus(anyString(), anyString());
        mockMvc.perform(patch("/api/users/user-1/status")
                        .param("status", "BLOCKED"))
                .andExpect(status().isNoContent());
    }
}