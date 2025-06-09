package com.jotavera.demo.auth.controller;

import com.jotavera.demo.auth.dto.request.SignUpRequest;
import com.jotavera.demo.auth.dto.response.SignUpResponse;
import com.jotavera.demo.auth.dto.response.UserResponse;
import com.jotavera.demo.auth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class AuthenticationController {

    private final UserService userService;

    @Operation(summary = "Register a new user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User successfully registered"),
            @ApiResponse(responseCode = "400", description = "Validation error, bad request", content = @Content),
            @ApiResponse(responseCode = "409", description = "The user already exists", content = @Content)
    })
    @PostMapping("/sign-up")
    public ResponseEntity<SignUpResponse> signUp(@Valid @RequestBody SignUpRequest signUpRequest){
        SignUpResponse userResponse = userService.signUp(signUpRequest);
        return ResponseEntity.ok(userResponse);
    }

    @Operation(summary = "Login with JWT token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User authenticated"),
            @ApiResponse(responseCode = "400", description = "Invalid or missing token", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/login")
    public ResponseEntity<UserResponse> login(@RequestHeader("Authorization") String authHeader) {
        UserResponse userResponse = userService.login(authHeader);
        return ResponseEntity.ok(userResponse);
    }

}
