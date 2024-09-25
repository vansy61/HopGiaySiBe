package com.hosi.hosibackend.entity.dto.user;

import lombok.Data;

@Data
public class UserResDto {
    private Long id;
    private String email;
    private boolean isActive;
    private boolean enableTwoFactor;
}
