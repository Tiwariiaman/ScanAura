package com.scanaura.common.util;

import com.scanaura.auth.entity.User;
import com.scanaura.common.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    private SecurityUtil() {
    }

    public static User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        return userDetails.getUser();
    }
}
