package com.hosi.hosibackend.repo;

import com.hosi.hosibackend.entity.User;
import org.apache.poi.sl.draw.geom.GuideIf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;

@Repository
public interface IUserRepo extends JpaRepository<User, Long> {
    Optional<User> findUserByEmail(String email);
    Optional<User> findUserBySessionsToken(String token);
    Optional<User> findUserByVerifyToken(String verifyToken);
}
