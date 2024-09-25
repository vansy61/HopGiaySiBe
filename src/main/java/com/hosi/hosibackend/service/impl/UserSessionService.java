package com.hosi.hosibackend.service.impl;

import com.hosi.hosibackend.entity.UserSession;
import com.hosi.hosibackend.entity.response.UserSessionResDto;
import com.hosi.hosibackend.mapper.UserSessionMapper;
import com.hosi.hosibackend.repo.IUserSessionRepo;
import com.hosi.hosibackend.service.IUserSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserSessionService implements IUserSessionService {
    private final IUserSessionRepo userSessionRepo;

    @Override
    public List<UserSessionResDto> getAllUserSessions(Long userId) {
        List<UserSession> userSessions = userSessionRepo.getUserSessionsByUser_IdOrderByIdDesc(userId);
        return UserSessionMapper.INSTANCE.userSessionsToUserSessionResDto(userSessions);
    }

    @Override
    public void logoutSession(Long userId, Long sessionId) {
        UserSession userSession = userSessionRepo.findUserSessionByIdAndUserId(sessionId, userId);

        if (userSession == null) {
            throw new RuntimeException("session_not_found");
        }

        userSession.setIsRevoked(true);
        userSessionRepo.save(userSession);
    }
}
