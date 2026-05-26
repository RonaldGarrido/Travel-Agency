package com.example.demo.service;

import com.example.demo.dto.request.UserRequestDTO;
import com.example.demo.dto.response.UserResponseDTO;
import com.example.demo.exception.BusinessException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId("user-1");
        user.setFullName("Juan Pérez");
        user.setEmail("juan@test.com");
        user.setPassword("12345678");
        user.setPhone("912345678");
        user.setRole(User.Role.CLIENT);
        user.setStatus(User.Status.ACTIVE);
        user.setCreatedAt(LocalDateTime.now());

        requestDTO = new UserRequestDTO();
        requestDTO.setFullName("Juan Pérez");
        requestDTO.setEmail("juan@test.com");
        requestDTO.setPassword("12345678");
        requestDTO.setPhone("912345678");
        requestDTO.setRole("CLIENT");
    }

    // ─── getAllUsers ───────────────────────────────────────────────

    @Test
    void getAllUsers_returnsListOfUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        List<UserResponseDTO> result = userService.getAllUsers();
        assertEquals(1, result.size());
        assertEquals("Juan Pérez", result.get(0).getFullName());
    }

    @Test
    void getAllUsers_returnsEmptyListWhenNoUsers() {
        when(userRepository.findAll()).thenReturn(List.of());
        List<UserResponseDTO> result = userService.getAllUsers();
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllUsers_mapsAllFieldsCorrectly() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        List<UserResponseDTO> result = userService.getAllUsers();
        UserResponseDTO dto = result.get(0);
        assertEquals(user.getId(), dto.getId());
        assertEquals(user.getEmail(), dto.getEmail());
        assertEquals(user.getRole().name(), dto.getRole());
        assertEquals(user.getStatus().name(), dto.getStatus());
    }

    @Test
    void getAllUsers_returnsMultipleUsers() {
        User user2 = new User();
        user2.setId("user-2");
        user2.setFullName("María González");
        user2.setEmail("maria@test.com");
        user2.setPassword("12345678");
        user2.setRole(User.Role.ADMIN);
        user2.setStatus(User.Status.ACTIVE);
        when(userRepository.findAll()).thenReturn(List.of(user, user2));
        List<UserResponseDTO> result = userService.getAllUsers();
        assertEquals(2, result.size());
    }

    @Test
    void getAllUsers_callsRepositoryOnce() {
        when(userRepository.findAll()).thenReturn(List.of());
        userService.getAllUsers();
        verify(userRepository, times(1)).findAll();
    }

    // ─── getUserById ──────────────────────────────────────────────

    @Test
    void getUserById_returnsUserWhenExists() {
        when(userRepository.findById("user-1")).thenReturn(Optional.of(user));
        UserResponseDTO result = userService.getUserById("user-1");
        assertEquals("user-1", result.getId());
        assertEquals("Juan Pérez", result.getFullName());
    }

    @Test
    void getUserById_throwsNotFoundWhenUserDoesNotExist() {
        when(userRepository.findById("bad-id")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> userService.getUserById("bad-id"));
    }

    @Test
    void getUserById_mapsEmailCorrectly() {
        when(userRepository.findById("user-1")).thenReturn(Optional.of(user));
        UserResponseDTO result = userService.getUserById("user-1");
        assertEquals("juan@test.com", result.getEmail());
    }

    @Test
    void getUserById_mapsRoleCorrectly() {
        when(userRepository.findById("user-1")).thenReturn(Optional.of(user));
        UserResponseDTO result = userService.getUserById("user-1");
        assertEquals("CLIENT", result.getRole());
    }

    @Test
    void getUserById_mapsStatusCorrectly() {
        when(userRepository.findById("user-1")).thenReturn(Optional.of(user));
        UserResponseDTO result = userService.getUserById("user-1");
        assertEquals("ACTIVE", result.getStatus());
    }

    // ─── createUser ───────────────────────────────────────────────

    @Test
    void createUser_savesAndReturnsUser() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);
        UserResponseDTO result = userService.createUser(requestDTO);
        assertNotNull(result);
        assertEquals("Juan Pérez", result.getFullName());
    }

    @Test
    void createUser_throwsExceptionWhenEmailExists() {
        when(userRepository.existsByEmail("juan@test.com")).thenReturn(true);
        assertThrows(BusinessException.class,
                () -> userService.createUser(requestDTO));
    }

    @Test
    void createUser_callsSaveOnce() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);
        userService.createUser(requestDTO);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createUser_setsActiveStatusByDefault() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);
        UserResponseDTO result = userService.createUser(requestDTO);
        assertEquals("ACTIVE", result.getStatus());
    }

    @Test
    void createUser_assignsClientRoleCorrectly() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);
        UserResponseDTO result = userService.createUser(requestDTO);
        assertEquals("CLIENT", result.getRole());
    }

    @Test
    void createUser_doesNotSaveWhenEmailExists() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);
        assertThrows(BusinessException.class,
                () -> userService.createUser(requestDTO));
        verify(userRepository, never()).save(any());
    }

    // ─── updateUser ───────────────────────────────────────────────

    @Test
    void updateUser_updatesAndReturnsUser() {
        when(userRepository.findById("user-1")).thenReturn(Optional.of(user));
        //when(userRepository.save(any(User.class))).thenReturn(user);
        UserResponseDTO result = userService.updateUser("user-1", requestDTO);
        assertNotNull(result);
    }

    @Test
    void updateUser_throwsNotFoundWhenUserDoesNotExist() {
        when(userRepository.findById("bad-id")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> userService.updateUser("bad-id", requestDTO));
    }

    @Test
    void updateUser_throwsExceptionWhenEmailTakenByOther() {
        User other = new User();
        other.setId("user-2");
        other.setEmail("otro@test.com");
        when(userRepository.findById("user-1")).thenReturn(Optional.of(user));
        requestDTO.setEmail("otro@test.com");
        when(userRepository.existsByEmail("otro@test.com")).thenReturn(true);
        assertThrows(BusinessException.class,
                () -> userService.updateUser("user-1", requestDTO));
    }

    @Test
    void updateUser_allowsSameEmailForSameUser() {
        when(userRepository.findById("user-1")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        assertDoesNotThrow(() -> userService.updateUser("user-1", requestDTO));
    }

    @Test
    void updateUser_callsSaveOnce() {
        when(userRepository.findById("user-1")).thenReturn(Optional.of(user));
        //when(userRepository.save(any(User.class))).thenReturn(user);
        //userService.updateUser("user-1", requestDTO);
        verify(userRepository, times(1)).save(any(User.class));
    }

    // ─── changeUserStatus ─────────────────────────────────────────

    @Test
    void changeUserStatus_changesStatusToInactive() {
        when(userRepository.findById("user-1")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        assertDoesNotThrow(() -> userService.changeUserStatus("user-1", "INACTIVE"));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void changeUserStatus_changesStatusToBlocked() {
        when(userRepository.findById("user-1")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        assertDoesNotThrow(() -> userService.changeUserStatus("user-1", "BLOCKED"));
    }

    @Test
    void changeUserStatus_throwsNotFoundWhenUserDoesNotExist() {
        when(userRepository.findById("bad-id")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> userService.changeUserStatus("bad-id", "INACTIVE"));
    }

    @Test
    void changeUserStatus_throwsExceptionForInvalidStatus() {
        when(userRepository.findById("user-1")).thenReturn(Optional.of(user));
        assertThrows(Exception.class,
                () -> userService.changeUserStatus("user-1", "INVALID_STATUS"));
    }

    @Test
    void changeUserStatus_callsSaveOnce() {
        when(userRepository.findById("user-1")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        userService.changeUserStatus("user-1", "ACTIVE");
        verify(userRepository, times(1)).save(any(User.class));
    }
}