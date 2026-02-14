package com.example.demo.service;

import com.example.demo.common.exception.DataAlreadyExistsException;
import com.example.demo.common.exception.ResourceNotFoundException;
import com.example.demo.config.JwtService;
import com.example.demo.entity.auth.LoginRequest;
import com.example.demo.entity.auth.LoginResponse;
import com.example.demo.entity.auth.User;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public User register(User request) {
        // 1. التأكد من عدم تكرار البريد الإلكتروني
        if (repository.existsByEmail(request.getEmail())) {
            throw new DataAlreadyExistsException("هذا البريد الإلكتروني مستخدم بالفعل!");
        }

        var user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        return repository.save(user);
    }

    public LoginResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (AuthenticationException e) {
            // تخصيص رسالة الخطأ عند فشل تسجيل الدخول
            throw new ResourceNotFoundException("البريد الإلكتروني أو كلمة المرور غير صحيحة");
        }

        var user = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("المستخدم غير موجود"));

        var token = jwtService.generateToken(user);

        return new LoginResponse(user,token);
    }
}