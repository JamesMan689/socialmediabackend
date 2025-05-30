package com.james.socialbackend.controller;

import com.james.socialbackend.entity.AuthRequest;
import com.james.socialbackend.entity.UserInfo;
import com.james.socialbackend.service.JwtService;
import com.james.socialbackend.service.UserInfoService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

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

    // Search users
    @GetMapping("/search")
    public ResponseEntity<?> searchUsers(@RequestParam String query) {
        List<UserInfo> users = service.searchUsers(query);
        return ResponseEntity.ok(users.stream()
            .map(user -> new HashMap<String, String>() {{
                put("id", String.valueOf(user.getId()));
                put("name", user.getName());
                put("username", user.getUsername());
            }})
            .collect(Collectors.toList()));
    }

    // Follow user
    @PostMapping("/follow/{username}")
    public ResponseEntity<?> followUser(@PathVariable String username, HttpServletRequest request) {
        UserInfo currentUser = jwtService.getUserFromRequest(request);
        UserInfo userToFollow = service.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        
        if (currentUser.getUsername().equals(username)) {
            return ResponseEntity.badRequest().body("You cannot follow yourself");
        }
        
        if (currentUser.getFollowing().contains(userToFollow)) {
            return ResponseEntity.badRequest().body("You are already following this user");
        }
        
        // Update both sides of the relationship
        currentUser.getFollowing().add(userToFollow);
        userToFollow.getFollowers().add(currentUser);
        
        service.saveUser(currentUser);
        service.saveUser(userToFollow);
        
        return ResponseEntity.ok("Successfully followed " + username);
    }

    // Unfollow user
    @DeleteMapping("/unfollow/{username}")
    public ResponseEntity<?> unfollowUser(@PathVariable String username, HttpServletRequest request) {
        try {
            UserInfo currentUser = jwtService.getUserFromRequest(request);
            UserInfo userToUnfollow = service.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found: " + username));
            
            if (currentUser.getUsername().equals(username)) {
                return ResponseEntity.badRequest().body("You cannot unfollow yourself");
            }
            
            if (!currentUser.getFollowing().contains(userToUnfollow)) {
                return ResponseEntity.badRequest().body("You are not following this user");
            }
            
            // Update both sides of the relationship
            currentUser.getFollowing().remove(userToUnfollow);
            userToUnfollow.getFollowers().remove(currentUser);
            
            service.saveUser(currentUser);
            service.saveUser(userToUnfollow);
            
            return ResponseEntity.ok("Successfully unfollowed " + username);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Get follower count
    @GetMapping("/followers/{username}")
    public ResponseEntity<?> getFollowerCount(@PathVariable String username) {
        int count = service.getFollowerCount(username);
        return ResponseEntity.ok(count);
    }

    // Get following count
    @GetMapping("/following/{username}")
    public ResponseEntity<?> getFollowingCount(@PathVariable String username) {
        int count = service.getFollowingCount(username);
        return ResponseEntity.ok(count);
    }
}