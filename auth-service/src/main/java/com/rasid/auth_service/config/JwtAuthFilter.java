package com.rasid.auth_service.config;

import com.rasid.auth_service.service.UserDetailsService;
import com.rasid.auth_service.util.JWTUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JWTUtils jwtUtils;
    private final UserDetailsService service;

    public JwtAuthFilter(JWTUtils jwtUtils, UserDetailsService service) {
        this.jwtUtils = jwtUtils;
        this.service = service;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String bearerToken = request.getHeader("Authorization");
        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        String jwt = bearerToken.substring(7);
        if (jwt == null) {
            filterChain.doFilter(request, response);
            return;
        }
        UserDetails userDetails = service.loadUserById(UUID.fromString(jwtUtils.extractUserId(jwt)));
        if (jwtUtils.isTokenValid(jwt) && !jwtUtils.isTokenExpired(jwt)){
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
                    );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }
}
