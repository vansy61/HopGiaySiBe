package com.hosi.hosibackend.entity.response;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserSessionResDto {
    private Long id;
    private String userAgent;
    private String ipAddress;
    private Boolean isMobile;
    private Boolean isRevoked;
}
