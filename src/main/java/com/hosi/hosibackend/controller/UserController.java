package com.hosi.hosibackend.controller;

import com.hosi.hosibackend.entity.User;
import com.hosi.hosibackend.entity.dto.user.UserResDto;
import com.hosi.hosibackend.entity.dto.user.UserUpdateDto;
import com.hosi.hosibackend.entity.response.UserSessionResDto;
import com.hosi.hosibackend.security.CustomUserDetails;
import com.hosi.hosibackend.service.IUserService;
import com.hosi.hosibackend.service.IUserSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@CrossOrigin("*")
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final IUserSessionService userSessionService;
    private final IUserService userService;

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(
            Authentication authentication
    ) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UserResDto user = userService.getCurrentUser(userDetails.getId());
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("#id == authentication.principal.id")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @RequestBody UserUpdateDto updateDto
    ) {
        userService.updateUser(id, updateDto);
        return ResponseEntity.ok("{}");
    }

    @GetMapping("/login-history")
    public ResponseEntity<?> getUserLoginHistory(
            Authentication authentication
    ) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        List<UserSessionResDto> userSessionResDto = userSessionService.getAllUserSessions(userDetails.getId());
        return ResponseEntity.ok(userSessionResDto);
    }

    @GetMapping("/session/{sessionId}/logout")
    public ResponseEntity<?> logout(
            Authentication authentication,
            @PathVariable Long sessionId
    ) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        userSessionService.logoutSession(userDetails.getId(), sessionId);
        return ResponseEntity.ok().build();
    }
}
