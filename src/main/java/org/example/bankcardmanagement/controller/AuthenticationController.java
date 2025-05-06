package org.example.bankcardmanagement.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.example.bankcardmanagement.dto.*;
import org.example.bankcardmanagement.security.jwt.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
    private final AuthenticationService authService;

    public AuthenticationController(AuthenticationService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    @Operation(summary = "Registration in the system")
    public ResponseEntity<?> signUp(@Valid @RequestBody RegistrationUserDto request) {
        authService.signUp(request);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/verify")
    @Operation(summary = "Confirmation verification code")
    public ResponseEntity<?> verifyUser(@Valid @RequestBody VerifyUserDto verifyUserDto) {
        try {
            authService.verifyUser(verifyUserDto);
            return ResponseEntity.ok("Account verified successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/resend")
    @Operation(summary = "Resend verification code")
    public ResponseEntity<?> resendVerificationCode(@RequestParam String email) {
        try {
            authService.resendVerificationCode(email);
            return ResponseEntity.ok("Verification code sent");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Login in the system")
    public ResponseEntity<?> login(@Valid @RequestBody AuthenticationUserDtoRequest request) {
        TokenPair tokenPair = authService.login(request);
        return ResponseEntity.ok(tokenPair);
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Refresh token in the system")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        TokenPair tokenPair = authService.refreshToken(request);
        return ResponseEntity.ok(tokenPair);
    }
}
