package ma.youcode.supplyChainX.controller;

import lombok.RequiredArgsConstructor;
import ma.youcode.supplyChainX.dto.UserRequest;
import ma.youcode.supplyChainX.dto.UserResponse;
import ma.youcode.supplyChainX.security.SecuredAction;
import ma.youcode.supplyChainX.service.UserService;
import ma.youcode.supplyChainX.shared.enums.Role;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @SecuredAction(roles = {Role.ADMIN})
    public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest request) {
        return ResponseEntity.ok(userService.createUser(request));
    }

    @PutMapping("/{id}/role")
    @SecuredAction(roles = {Role.ADMIN})
    public ResponseEntity<UserResponse> updateUserRole(
            @PathVariable Long id,
            @RequestParam Role newRole
    ) {
        return ResponseEntity.ok(userService.updateUserRole(id, newRole));
    }
}
