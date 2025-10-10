package com.lcms.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lcms.system.dto.LoginRequest;
import com.lcms.system.dto.LoginResponse;
import com.lcms.system.dto.RegisterRequest;
import com.lcms.system.entity.Lawyers;
import com.lcms.system.security.JwtUtil;
import com.lcms.system.service.LawyersService;
import jakarta.validation.Valid;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final LawyersService lawyersService;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;

    public AuthController(LawyersService lawyersService,
                          PasswordEncoder encoder,
                          AuthenticationManager authManager,
                          JwtUtil jwtUtil) {
        this.lawyersService = lawyersService;
        this.encoder = encoder;
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public Object register(@Valid @RequestBody RegisterRequest req) {
        boolean exists = lawyersService.count(
                new LambdaQueryWrapper<Lawyers>().eq(Lawyers::getEmail, req.getEmail())) > 0;
        if (exists) return "Email already registered";

        Lawyers l = new Lawyers();
        l.setEmail(req.getEmail());
        l.setFullName(req.getFullName());
        l.setPasswordHash(encoder.encode(req.getPassword()));
        l.setStatus("ACTIVE");
        lawyersService.save(l);

        return "OK";
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest req) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));
        Lawyers user = lawyersService.findByEmail(req.getEmail());
        String token = jwtUtil.generateToken(user.getId(),req.getEmail());
        return new LoginResponse(token);
    }

    @GetMapping("/test")
    public Map<String, String> test() {
        return Map.of("status","UP");
    }
}
