package com.hosi.hosibackend.entity.response;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ResponseUser {
    private String email;
    private Collection<? extends GrantedAuthority> authorities;
    private String accessToken;
    private boolean needOtp = false;
}
