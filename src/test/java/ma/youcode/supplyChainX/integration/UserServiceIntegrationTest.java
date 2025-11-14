package ma.youcode.supplyChainX.integration;

import ma.youcode.supplyChainX.dto.UserRequest;
import ma.youcode.supplyChainX.dto.UserResponse;
import ma.youcode.supplyChainX.model.User;
import ma.youcode.supplyChainX.repository.UserRepository;
import ma.youcode.supplyChainX.service.UserService;
import ma.youcode.supplyChainX.shared.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
    }

    @Test
    void shouldCreateAndRetrieveUserSuccessfully() {
        UserRequest request = new UserRequest(
                "ayoub@gmail.com", "password123", "Ayoub", "Benmansour", Role.ADMIN
        );

        UserResponse created = userService.createUser(request);
        assertNotNull(created.getId());

        UserResponse retrieved = userService.getUserById(created.getId());

        assertEquals(created.getEmail(), retrieved.getEmail());
        assertEquals(Role.ADMIN, retrieved.getRole());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        assertThrows(EntityNotFoundException.class,
                () -> userService.getUserById(999L));
    }

    @Test
    void shouldUpdateUserRoleSuccessfully() {
        UserRequest request = new UserRequest(
                "user@test.com", "1234", "Ali", "Baba", Role.CHEF_PRODUCTION
        );
        UserResponse created = userService.createUser(request);

        UserResponse updated = userService.updateUserRole(created.getId(), Role.ADMIN);

        assertEquals(Role.ADMIN, updated.getRole());
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        UserRequest request = new UserRequest("dup@gmail.com", "pass", "A", "B",
                Role.CHEF_PRODUCTION);

        userService.createUser(request);

        assertThrows(IllegalArgumentException.class,
                () -> userService.createUser(request));
    }
}
