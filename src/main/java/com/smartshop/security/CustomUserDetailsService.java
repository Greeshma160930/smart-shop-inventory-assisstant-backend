package com.smartshop.security;

import com.smartshop.entity.User;
import com.smartshop.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        if (email == null || email.trim().isEmpty()) {
            throw new UsernameNotFoundException("Email cannot be blank");
        }
        String cleanEmail = email.trim().toLowerCase();
        User user = userRepository.findByEmailIgnoreCase(cleanEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        String role = (user.getRole() != null && !user.getRole().trim().isEmpty()) ? user.getRole().trim() : "ROLE_USER";
        if (!role.startsWith("ROLE_")) {
            role = "ROLE_" + role;
        }

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(role))
        );
    }
}
