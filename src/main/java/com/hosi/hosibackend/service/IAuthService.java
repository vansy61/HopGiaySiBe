package com.hosi.hosibackend.service;

import com.hosi.hosibackend.entity.dto.FormLogin;
import com.hosi.hosibackend.entity.dto.FormSignup;
import com.hosi.hosibackend.entity.dto.ResponseUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

public interface IAuthService {
    ResponseUser login(@Valid FormLogin formLogin, HttpServletRequest request);

    void signup(@Valid FormSignup formSignup);

    void verifyEmail(String token);
}
