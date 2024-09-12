package com.hosi.hosibackend.controller;

import com.hosi.hosibackend.entity.dto.FormLogin;
import com.hosi.hosibackend.entity.dto.FormSignup;
import com.hosi.hosibackend.entity.dto.ResponseUser;
import com.hosi.hosibackend.service.IAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final IAuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody FormLogin formLogin,
                                   HttpServletRequest request) {
        ResponseUser responseUser =  authService.login(formLogin, request);
        return new ResponseEntity<>(responseUser, HttpStatus.OK);
    }
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody FormSignup formSignup) {
        authService.signup(formSignup);
        return new ResponseEntity<>("{}", HttpStatus.CREATED);
    }

    @PostMapping("/verify-email")
    public ResponseEntity<?> verify(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        authService.verifyEmail(token);
        return new ResponseEntity<>("{}", HttpStatus.OK);
    }
}
