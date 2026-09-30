package com.scanaura.common.security;

import com.scanaura.auth.entity.User;
import com.scanaura.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String identifier)
            throws UsernameNotFoundException {

        User user;

        if (identifier != null &&
                identifier.contains("@")) {

            user = userRepository.findByEmail(
                    identifier.trim().toLowerCase()
            ).orElseThrow(() ->
                    new UsernameNotFoundException(
                            "User not found"
                    )
            );

        } else {

            user = userRepository.findByMobile(
                    identifier.trim()
            ).orElseThrow(() ->
                    new UsernameNotFoundException(
                            "User not found"
                    )
            );
        }

        return new CustomUserDetails(user);
    }
}