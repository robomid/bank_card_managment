package org.example.bankcardmanagement.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.example.bankcardmanagement.dto.UserDtoRequest;
import org.example.bankcardmanagement.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "Get all users")
    public ResponseEntity<?> getUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<?> getUserById(@PathVariable long userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @PutMapping("/{userId}")
    @Operation(summary = "Update user by ID")
    public ResponseEntity<?> updateUser(@PathVariable long userId, @RequestBody UserDtoRequest userDtoRequest) {
        userService.updateUser(userId, userDtoRequest);
        return ResponseEntity.ok("User data has been successfully updated.");
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Delete user by ID")
    public ResponseEntity<?> deleteUser(@PathVariable long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok("User has been successfully deleted.");
    }
}
