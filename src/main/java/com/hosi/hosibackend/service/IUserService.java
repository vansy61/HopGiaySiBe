package com.hosi.hosibackend.service;

import com.hosi.hosibackend.entity.User;
import com.hosi.hosibackend.entity.dto.user.UserResDto;
import com.hosi.hosibackend.entity.dto.user.UserUpdateDto;

public interface IUserService {
    void updateUser(Long id, UserUpdateDto updateDto);

    UserResDto getCurrentUser(Long id);
}
