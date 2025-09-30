package com.lcms.system.security;

import jakarta.servlet.http.HttpServletRequest;

public class SecurityUtil {

    private static final String TOKEN_PREFIX = "Bearer ";

    private static JwtUtil jwtUtil;

    // 提供一个静态初始化（在 Spring 启动时注入）
    public SecurityUtil(JwtUtil jwtUtil) {
        SecurityUtil.jwtUtil = jwtUtil;
    }

    public static Long getCurrentLawyerId(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith(TOKEN_PREFIX)) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }
        String token = header.substring(TOKEN_PREFIX.length());
        return jwtUtil.extractLawyerId(token);
    }
}
