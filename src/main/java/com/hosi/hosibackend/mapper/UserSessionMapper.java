package com.hosi.hosibackend.mapper;

import com.hosi.hosibackend.entity.UserSession;
import com.hosi.hosibackend.entity.response.UserSessionResDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface UserSessionMapper {
    UserSessionMapper INSTANCE = Mappers.getMapper(UserSessionMapper.class);
    UserSessionResDto userSessionToUserSessionResDto(UserSession userSession);
    List<UserSessionResDto> userSessionsToUserSessionResDto(List<UserSession> userSessions);
}
