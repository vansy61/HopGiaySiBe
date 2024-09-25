package com.hosi.hosibackend.repo;

import com.hosi.hosibackend.entity.UserOtp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IUserOtpRepo extends JpaRepository<UserOtp, Long> {
    List<UserOtp> findUserOtpByUser_IdOrderByIdDesc(Long userId);
}
