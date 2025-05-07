package org.example.bankcardmanagement.service;

import org.example.bankcardmanagement.dto.UserDtoRequest;
import org.example.bankcardmanagement.dto.UserDtoResponse;
import org.example.bankcardmanagement.model.Role;
import org.example.bankcardmanagement.model.User;
import org.example.bankcardmanagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;


    private final User testUser = new User("test@example.com", "testuser", "password2", Set.of(new Role("ROLE_ADMIN")));
    private final UserDtoRequest testUserDtoRequest = new UserDtoRequest("new@example.com", "newuser", false);

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void getCurrentUser_WhenUserExists_ReturnsUser() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // Act
        User result = userService.getCurrentUser();

        // Assert
        assertNotNull(result);
        assertEquals(testUser, result);
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void getCurrentUser_WhenUserNotExists_ReturnsNull() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("nonexistent@example.com");
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Act
        User result = userService.getCurrentUser();

        // Assert
        assertNull(result);
        verify(userRepository).findByEmail("nonexistent@example.com");
    }

    @Test
    void getAllUsers_ReturnsListOfUserDtoResponses() {
        // Arrange
        List<User> users = List.of(
                new User("user1@example.com", "user1", "password1", Set.of(new Role("ROLE_USER"))),
                new User("user2@example.com", "user2", "password2", Set.of(new Role("ROLE_ADMIN")))
        );

        when(userRepository.findAll()).thenReturn(users);

        // Act
        List<UserDtoResponse> result = userService.getAllUsers();

        // Assert
        assertEquals(2, result.size());
        assertEquals("user1@example.com", result.get(0).email());
        assertEquals("user2@example.com", result.get(1).email());
        verify(userRepository).findAll();
    }

    @Test
    void getUserById_WhenUserExists_ReturnsUser() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act
        User result = userService.getUserById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testUser, result);
        verify(userRepository).findById(1L);
    }

    @Test
    void getUserById_WhenUserNotExists_ThrowsException() {
        // Arrange
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.getUserById(99L));
        verify(userRepository).findById(99L);
    }

    @Test
    void updateUser_WithAllFields_UpdatesAllFields() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        userService.updateUser(1L, testUserDtoRequest);

        // Assert
        verify(userRepository).findById(1L);
        verify(userRepository).save(argThat(user ->
                user.getEmail().equals("new@example.com") &&
                        user.getUsername().equals("newuser") &&
                        !user.getEnabled()
        ));
    }

    @Test
    void deleteUser_SetsEnabledToFalse() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        userService.deleteUser(1L);

        // Assert
        verify(userRepository).findById(1L);
        verify(userRepository).save(argThat(user -> !user.getEnabled()));
    }
}