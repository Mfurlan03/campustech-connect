package com.example.campustech.utils;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    public JwtAuthFilter(JwtUtils jwtUtils, UserDetailsService userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI();
        System.out.println("🔍 Incoming Request: " + path);

        if (path.startsWith("/auth/")) {
            System.out.println("🔄 Skipping JWT Authentication for: " + path);
            filterChain.doFilter(request, response);
            return;
        }

        String token = jwtUtils.extractToken(request);
        if (token == null) {
            System.out.println("⚠️ No token found. Proceeding without authentication.");
            filterChain.doFilter(request, response);
            return;
        }

        System.out.println("✅ Token found: " + token);

        try {
            String username = jwtUtils.extractUsername(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtUtils.validateToken(token, userDetails)) {
                System.out.println("🔓 Token valid. User authenticated: " + username);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                System.out.println("❌ Invalid token for user: " + username);
            }
        } catch (Exception e) {
            System.out.println("🚨 JWT Error: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
