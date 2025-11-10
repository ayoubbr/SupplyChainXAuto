package ma.youcode.supplyChainX.service;

import jakarta.persistence.EntityNotFoundException;
import ma.youcode.supplyChainX.dto.UserRequest;
import ma.youcode.supplyChainX.dto.UserResponse;
import ma.youcode.supplyChainX.mapper.UserMapper;
import ma.youcode.supplyChainX.model.User;
import ma.youcode.supplyChainX.repository.UserRepository;
import ma.youcode.supplyChainX.shared.enums.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;


    @Test
    void shouldReturnUserWhenIdExists() {
        User mockUser = new User(1L, "ayoub@gmail.com", "password", Role.ADMIN, "ayoub", "br");

        UserResponse mockResponse = new UserResponse(1L, "ayoub@gmail.com", "ayoub", "br", Role.ADMIN);

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(userMapper.toResponse(mockUser)).thenReturn(mockResponse);

        UserResponse result = userService.getUserById(1L);

        assertEquals("ayoub@gmail.com", result.getEmail());
        assertEquals(Role.ADMIN, result.getRole());
        assertEquals("ayoub", result.getFirstName());
        assertEquals("br", result.getLastName());


        verify(userRepository).findById(1L);
        verify(userMapper).toResponse(mockUser);
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotExist() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> userService.getUserById(99L));

        assertEquals("User not found with id: 99", exception.getMessage());
    }

    @Test
    void shouldCreateUserSuccessfully() {

        UserRequest request = new UserRequest("am@gmail.com", "password", "am", "br", Role.ADMIN);

        User entity = new User(null, "am@gmail.com", "password", Role.ADMIN, "am", "br");
        User saved = new User(1L, "am@gmail.com", "password", Role.ADMIN, "am", "br");
        UserResponse response = new UserResponse(1L, "am@gmail.com", "am", "br", Role.ADMIN);

        when(userRepository.existsByEmail("am@gmail.com")).thenReturn(false);
        when(userMapper.toEntity(request)).thenReturn(entity);
        when(userRepository.save(entity)).thenReturn(saved);
        when(userMapper.toResponse(saved)).thenReturn(response);


        UserResponse result = userService.createUser(request);

        assertEquals("am@gmail.com", result.getEmail());
        assertEquals("am", result.getFirstName());
        assertEquals("br", result.getLastName());
        assertEquals(Role.ADMIN, result.getRole());

        verify(userRepository).existsByEmail("am@gmail.com");
        verify(userMapper).toEntity(request);
        verify(userRepository).save(entity);
        verify(userMapper).toResponse(saved);
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        UserRequest request = new UserRequest("am@gmail.com", "password", "am", "br", Role.ADMIN);

        when(userRepository.existsByEmail("am@gmail.com")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.createUser(request));

        assertEquals("Email already exists", exception.getMessage());
    }

    @Test
    void shouldUpdateUserRoleSuccessfully() {
        User existingUser = new User(1L, "user@gmail.com", "pass", Role.CHEF_PRODUCTION, "ayoub", "ben");
        User updatedUser = new User(1L, "user@gmail.com", "pass", Role.ADMIN, "ayoub", "ben");
        UserResponse response = new UserResponse(1L, "user@gmail.com", "ayoub", "ben", Role.ADMIN);

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(updatedUser);
        when(userMapper.toResponse(updatedUser)).thenReturn(response);

        UserResponse result = userService.updateUserRole(1L, Role.ADMIN);

        assertEquals(Role.ADMIN, result.getRole());
        verify(userRepository).findById(1L);
        verify(userRepository).save(existingUser);
    }
}

