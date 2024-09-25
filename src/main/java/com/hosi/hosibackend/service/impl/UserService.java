package com.hosi.hosibackend.service.impl;

import com.hosi.hosibackend.entity.User;
import com.hosi.hosibackend.entity.dto.user.UserResDto;
import com.hosi.hosibackend.entity.dto.user.UserUpdateDto;
import com.hosi.hosibackend.exception.NotFoundException;
import com.hosi.hosibackend.mapper.UserMapper;
import com.hosi.hosibackend.repo.IUserRepo;
import com.hosi.hosibackend.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService implements IUserService {
    @Autowired
    private IUserRepo userRepo;
    @Override
    public void updateUser(Long id, UserUpdateDto updateDto) {
        User user = userRepo.findById(id).orElseThrow(() -> new NotFoundException("user_not_found"));

        if(updateDto.getEnableTwoFactor() != null) {
            user.setEnableTwoFactor(updateDto.getEnableTwoFactor());
        }
        userRepo.save(user);
    }

    @Override
    public UserResDto getCurrentUser(Long id) {
        User user = userRepo.findById(id).orElseThrow(() -> new NotFoundException("user_not_found"));
        return UserMapper.INSTANCE.userToUserResDto(user);
    }
}
