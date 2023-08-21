package com.booklink.backend.config;

import com.booklink.backend.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class CustomJwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            String jwtToken = extractJwtFromRequest(request);
            if(jwtUtil.validateToken(jwtToken)){
                String email = jwtUtil.getEmailFromToken(jwtToken);
                request.setAttribute("email", email);
            }
        }catch (Exception e){
            System.out.println("Cannot set the Security Context");
        }
        filterChain.doFilter(request, response);
    }

    private String extractJwtFromRequest(HttpServletRequest request){
        String bearerToken = request.getHeader("Authorization");
        if(bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7);
        }
        return bearerToken;
    }
}
