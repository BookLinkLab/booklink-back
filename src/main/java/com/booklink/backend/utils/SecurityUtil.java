package com.booklink.backend.utils;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class SecurityUtil {
    public UserDetails getLoggedUser() {
        Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return (UserDetails) user;
    }
}
