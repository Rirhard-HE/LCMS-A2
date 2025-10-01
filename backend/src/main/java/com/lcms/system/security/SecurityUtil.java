package com.lcms.system.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {

    @Autowired
    private JwtUtil jwtUtil;
    private static final String TOKEN_PREFIX = "Bearer ";


    public Long getCurrentLawyerId(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith(TOKEN_PREFIX)) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }
        String token = header.substring(TOKEN_PREFIX.length());
        return jwtUtil.extractLawyerId(token);
    }
}
