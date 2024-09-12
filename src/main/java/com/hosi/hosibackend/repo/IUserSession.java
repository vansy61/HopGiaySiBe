package com.hosi.hosibackend.repo;

import com.hosi.hosibackend.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUserSession extends JpaRepository<UserSession, Long> {
}
