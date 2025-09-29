package org.atics.bot450.service;

import lombok.RequiredArgsConstructor;
import org.atics.bot450.model.UserAccount;
import org.atics.bot450.repository.UserAccountRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserAccountRepository users;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserAccount register(String username, String rawPassword) {
        if (users.existsByUsername(username)) {
            throw new IllegalArgumentException("User already exists");
        }
        UserAccount user = UserAccount.builder()
                .username(username)
                .passwordHash(passwordEncoder.encode(rawPassword))
                .enabled(true)
                .role("USER")
                .build();
        return users.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccount u = users.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return org.springframework.security.core.userdetails.User
                .withUsername(u.getUsername())
                .password(u.getPasswordHash())
                .disabled(!u.isEnabled())
                .authorities(
                        Arrays.stream(u.getRole().split(","))
                                .map(String::trim)
                                .filter(s -> !s.isEmpty())
                                .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                                .collect(Collectors.toSet())
                )
                .build();
    }
}
