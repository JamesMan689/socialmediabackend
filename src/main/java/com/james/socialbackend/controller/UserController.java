package com.james.socialbackend.controller;

import com.james.socialbackend.entity.AuthRequest;
import com.james.socialbackend.entity.UserInfo;
import com.james.socialbackend.service.JwtService;
import com.james.socialbackend.service.UserInfoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserInfoService service;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    // connection test
    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome this endpoint is not secure";
    }

    // Register
    @PostMapping("/addNewUser")
    public ResponseEntity<String> addNewUser(@Valid @RequestBody UserInfo userInfo) {
        return ResponseEntity.ok(service.addUser(userInfo));
    }
    
    // Login
    @PostMapping("/generateToken")
    public ResponseEntity<String> authenticateAndGetToken(@Valid @RequestBody AuthRequest authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsernameOrEmail(),
                            authRequest.getPassword()));
            if (authentication.isAuthenticated()) {
                return ResponseEntity.ok(jwtService.generateToken(authRequest.getUsernameOrEmail()));
            }
            return ResponseEntity.badRequest().body("Invalid credentials");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Authentication failed: " + e.getMessage());
        }
    }

    // login testing for user
    @GetMapping("/user/hello")
    public String userHello() {
        return "Hello User";
    }

    // login testing for admin
    @GetMapping("/admin/hello")
    public String adminHello() {
        return "Hello Admin";
    }


}