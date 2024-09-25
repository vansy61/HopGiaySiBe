package com.hosi.hosibackend.mapper;

import com.hosi.hosibackend.entity.User;
import com.hosi.hosibackend.entity.dto.user.UserResDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
    UserResDto userToUserResDto(User user);
}
