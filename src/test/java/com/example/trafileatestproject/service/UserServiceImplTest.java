package com.example.trafileatestproject.service;

import com.example.trafileatestproject.exceptions.EntityNotFoundException;
import com.example.trafileatestproject.exceptions.ValidationException;
import com.example.trafileatestproject.model.api.UserDTO;
import com.example.trafileatestproject.model.entity.User;
import com.example.trafileatestproject.repository.UserRepository;
import com.example.trafileatestproject.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void createUser_ValidUserDTO_ReturnsUserDTO() {
        UserDTO userDTO = UserDTO.builder()
                .name("Coffee machine")
                .phoneNumber("351444444")
                .age(20)
                .build();

        when(userRepository.save(any(User.class))).thenReturn(new User());

        UserDTO createdUser = userService.createUser(userDTO);

        assertEquals(userDTO.getName(), createdUser.getName());
        assertEquals(userDTO.getPhoneNumber(), createdUser.getPhoneNumber());
        assertEquals(userDTO.getAge(), createdUser.getAge());
    }

    @Test
    void createUser_InvalidUserDTO_ThrowsValidationException() {
        UserDTO userDTO = UserDTO.builder().build();

        ValidationException exception = assertThrows(ValidationException.class, () -> userService.createUser(userDTO));
        assertEquals("MissingRequiredParameters", exception.getCode());
        assertEquals("Name, phone number and age higher than 18 are required", exception.getMessage());

    }

    @Test
    void getUserById_ExistingUserId_ReturnsUserDTO() {
        UUID productId = UUID.randomUUID();
        when(userRepository.findById(productId))
                .thenReturn(Optional.of(new User()));

        UserDTO userDTO = userService.getUserById(productId.toString());

        assertNotNull(userDTO);
    }

    @Test
    void getUserById_NonExistingUserId_ThrowsEntityNotFoundException() {
        String nonExistingId = UUID.randomUUID().toString();
        when(userRepository.findById(UUID.fromString(nonExistingId))).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getUserById(nonExistingId));
    }

    @Test
    void updateUser_InvalidUserDTO_ThrowsValidationException() {
        UUID productId = UUID.randomUUID();
        UserDTO userDTO = UserDTO.builder()
                .name("Updated User Name")
                .age(18)
                .build();

        User existingUser = new User();

        EntityNotFoundException exception = assertThrows
                (EntityNotFoundException.class, () -> userService.updateUser(productId.toString(), userDTO));

        assertEquals("User not found", exception.getCode());
        assertEquals("Could not find user.", exception.getMessage());
    }

    @Test
    void updateUser_ValidUserDTO_ReturnsUpdatedUserDTO() {
        UUID productId = UUID.randomUUID();
        UserDTO userDTO = UserDTO.builder()
                .name("Updated User Name")
                .age(20)
                .phoneNumber("351222222")
                .build();

        User existingUser = new User();
        when(userRepository.findById(productId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        UserDTO updatedUser = userService.updateUser(productId.toString(), userDTO);

        assertEquals(userDTO.getName(), updatedUser.getName());
        assertEquals(userDTO.getAge(), updatedUser.getAge());
        assertEquals(userDTO.getPhoneNumber(), updatedUser.getPhoneNumber());
    }

    @Test
    void deleteUser_ExistingUserId_DeletesUser() {
        UUID userId = UUID.randomUUID();

        userService.deleteUser(userId.toString());

        verify(userRepository, times(1)).deleteById(userId);
    }
}
