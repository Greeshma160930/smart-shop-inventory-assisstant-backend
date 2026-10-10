package com.smartshop.service;

import com.smartshop.dto.AuthRequest;
import com.smartshop.dto.AuthResponse;
import com.smartshop.dto.RegisterRequest;
import com.smartshop.entity.User;
import com.smartshop.exception.ResourceNotFoundException;
import com.smartshop.repository.UserRepository;
import com.smartshop.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String cleanEmail = request.getEmail() != null ? request.getEmail().trim().toLowerCase() : "";
        if (userRepository.existsByEmailIgnoreCase(cleanEmail)) {
            throw new IllegalStateException("An account with this email address already exists. Please sign in instead.");
        }

        User user = User.builder()
                .name(request.getName().trim())
                .email(cleanEmail)
                .password(passwordEncoder.encode(request.getPassword()))
                .shopName(request.getShopName().trim())
                .role("ROLE_USER")
                .build();

        user = userRepository.save(user);

        return buildAuthResponse(user);
    }

    public AuthResponse login(AuthRequest request) {
        String email = request.getEmail() != null ? request.getEmail().trim().toLowerCase() : "";
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, request.getPassword())
            );
        } catch (BadCredentialsException | UsernameNotFoundException e) {
            throw new BadCredentialsException("Incorrect email or password");
        } catch (DisabledException e) {
            throw new BadCredentialsException("This account has been disabled");
        } catch (LockedException e) {
            throw new BadCredentialsException("This account is currently locked");
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Authentication failed: " + e.getMessage());
        }

        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        return buildAuthResponse(user);
    }

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            throw new BadCredentialsException("No authenticated user found");
        }

        String email;
        if (auth.getPrincipal() instanceof UserDetails) {
            email = ((UserDetails) auth.getPrincipal()).getUsername();
        } else {
            email = auth.getName();
        }

        return userRepository.findByEmailIgnoreCase(email.trim().toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found in database: " + email));
    }

    @Transactional
    public User updateProfile(String name, String shopName) {
        User user = getCurrentUser();
        if (name != null && !name.trim().isEmpty()) {
            user.setName(name.trim());
        }
        if (shopName != null && !shopName.trim().isEmpty()) {
            user.setShopName(shopName.trim());
        }
        return userRepository.save(user);
    }

    @Transactional
    public void updatePassword(String oldPassword, String newPassword) {
        User user = getCurrentUser();
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Current password does not match");
        }
        if (newPassword == null || newPassword.trim().length() < 6) {
            throw new IllegalArgumentException("New password must be at least 6 characters long");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    private AuthResponse buildAuthResponse(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("email", user.getEmail());
        claims.put("role", user.getRole());
        claims.put("shopName", user.getShopName());

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                java.util.Collections.emptyList()
        );

        String token = jwtUtil.generateToken(userDetails, claims);

        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .shopName(user.getShopName())
                .build();
    }
}
