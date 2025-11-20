package com.shopstream.order_service.controller;


import com.shopstream.order_service.dto.AuthRequest;
import com.shopstream.order_service.dto.AuthResponse;
import com.shopstream.order_service.dto.RegisterRequest;
import com.shopstream.order_service.entity.Role;
import com.shopstream.order_service.entity.User;
import com.shopstream.order_service.repository.RoleRepository;
import com.shopstream.order_service.repository.UserRepository;
import com.shopstream.order_service.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final com.shopstream.order_service.repository.UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(UserRepository userRepo, RoleRepository roleRepo, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        if (userRepo.existsByUsername(req.getUsername())) {
            return ResponseEntity.badRequest().body("Username already exists");
        }
        if (userRepo.existsByEmail(req.getEmail())) {
            return ResponseEntity.badRequest().body("Email already exists");
        }
        User u = new User();
        u.setUsername(req.getUsername());
        u.setEmail(req.getEmail());
        u.setPassword(passwordEncoder.encode(req.getPassword()));
        Role userRole = roleRepo.findByName("ROLE_USER").orElseGet(() -> {
            Role r = new Role();
            r.setName("ROLE_USER");
            return roleRepo.save(r);
        });
        u.getRoles().add(userRole);
        User saved = userRepo.save(u);
        String token = jwtUtil.generateToken(saved.getUsername(), List.of(userRole.getName()));
        return ResponseEntity.ok(new AuthResponse(token, saved.getUsername()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest req) {
        var possible = userRepo.findByUsername(req.getUsername());
        if (possible.isEmpty()) return ResponseEntity.status(401).body("Invalid credentials");
        User u = possible.get();
        if (!passwordEncoder.matches(req.getPassword(), u.getPassword())) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
        List<String> roles = u.getRoles().stream().map(Role::getName).toList();
        String token = jwtUtil.generateToken(u.getUsername(), roles);
        return ResponseEntity.ok(new AuthResponse(token, u.getUsername()));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(org.springframework.security.core.Authentication auth) {
        if (auth == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(auth.getName());
    }
}
