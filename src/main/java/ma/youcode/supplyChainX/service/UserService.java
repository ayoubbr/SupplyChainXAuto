package ma.youcode.supplyChainX.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import ma.youcode.supplyChainX.dto.UserRequest;
import ma.youcode.supplyChainX.dto.UserResponse;
import ma.youcode.supplyChainX.mapper.UserMapper;
import ma.youcode.supplyChainX.model.User;
import ma.youcode.supplyChainX.repository.UserRepository;
import ma.youcode.supplyChainX.shared.enums.Role;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserResponse createUser(UserRequest request) {
        if (userRepository.existsByEmail(request.getEmail().toLowerCase().trim())) {
            throw new IllegalArgumentException("Email already exists");
        }

        if (request.getRole() == null) {
            throw new IllegalArgumentException("Role is required");
        }

        User user = userMapper.toEntity(request);
        User saved = userRepository.save(user);

        return userMapper.toResponse(saved);
    }


    public UserResponse updateUserRole(Long id, Role newRole) {
        if (newRole == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        user.setRole(newRole);
        User updated = userRepository.save(user);

        return userMapper.toResponse(updated);
    }
}

