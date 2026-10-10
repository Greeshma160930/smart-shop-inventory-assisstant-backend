package com.smartshop.controller;

import com.smartshop.entity.User;
import com.smartshop.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/profile")
@Tag(name = "Profile", description = "Endpoints for managing user profile and security credentials")
public class ProfileController {

    private final AuthService authService;

    public ProfileController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping
    @Operation(summary = "Get current authenticated user profile")
    public ResponseEntity<Map<String, Object>> getProfile() {
        User user = authService.getCurrentUser();
        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "name", user.getName(),
                "email", user.getEmail(),
                "role", user.getRole(),
                "shopName", user.getShopName(),
                "createdAt", user.getCreatedAt() != null ? user.getCreatedAt().toString() : ""
        ));
    }

    @PutMapping
    @Operation(summary = "Update user profile details")
    public ResponseEntity<Map<String, Object>> updateProfile(@RequestBody ProfileUpdateRequest request) {
        User user = authService.updateProfile(request.getName(), request.getShopName());
        return ResponseEntity.ok(Map.of(
                "message", "Profile updated successfully",
                "id", user.getId(),
                "name", user.getName(),
                "email", user.getEmail(),
                "role", user.getRole(),
                "shopName", user.getShopName()
        ));
    }

    @PutMapping("/password")
    @Operation(summary = "Change password")
    public ResponseEntity<Map<String, String>> updatePassword(@RequestBody PasswordChangeRequest request) {
        authService.updatePassword(request.getOldPassword(), request.getNewPassword());
        return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
    }

    public static class ProfileUpdateRequest {
        private String name;
        private String shopName;

        public ProfileUpdateRequest() {}
        public ProfileUpdateRequest(String name, String shopName) {
            this.name = name;
            this.shopName = shopName;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getShopName() { return shopName; }
        public void setShopName(String shopName) { this.shopName = shopName; }
    }

    public static class PasswordChangeRequest {
        private String oldPassword;
        private String newPassword;

        public PasswordChangeRequest() {}
        public PasswordChangeRequest(String oldPassword, String newPassword) {
            this.oldPassword = oldPassword;
            this.newPassword = newPassword;
        }

        public String getOldPassword() { return oldPassword; }
        public void setOldPassword(String oldPassword) { this.oldPassword = oldPassword; }
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }
}
