package com.hosi.hosibackend.service;

import com.hosi.hosibackend.entity.response.UserSessionResDto;

import java.util.List;

public interface IUserSessionService {
    List<UserSessionResDto> getAllUserSessions(Long userId);

    void logoutSession(Long id, Long sessionId);
}
