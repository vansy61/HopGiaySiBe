package com.hosi.hosibackend.repo;

import com.hosi.hosibackend.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IUserSessionRepo extends JpaRepository<UserSession, Long> {
    List<UserSession> getUserSessionsByUser_IdOrderByIdDesc(Long userId);

    List<UserSession> getUserSessionsByUser_IdAndIsRevokedOrderByIdAsc(Long userId, Boolean isRevoked);

    @Query("SELECT us FROM UserSession us WHERE us.token = :token AND us.isRevoked = false")
    UserSession findActiveSessionByToken(@Param("token") String token);

    UserSession findUserSessionByIdAndUserId(Long id, Long userId);
}
