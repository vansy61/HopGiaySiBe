package com.hosi.hosibackend.service.impl;

import com.hosi.hosibackend.entity.Role;
import com.hosi.hosibackend.entity.RoleName;
import com.hosi.hosibackend.entity.User;
import com.hosi.hosibackend.entity.UserSession;
import com.hosi.hosibackend.entity.dto.FormLogin;
import com.hosi.hosibackend.entity.dto.FormSignup;
import com.hosi.hosibackend.entity.dto.ResponseUser;
import com.hosi.hosibackend.exception.NotFoundException;
import com.hosi.hosibackend.exception.UnauthorizedException;
import com.hosi.hosibackend.helper.RequestHelper;
import com.hosi.hosibackend.mailer.AuthMailer;
import com.hosi.hosibackend.repo.IRoleRepo;
import com.hosi.hosibackend.repo.IUserRepo;
import com.hosi.hosibackend.repo.IUserSession;
import com.hosi.hosibackend.security.CustomUserDetails;
import com.hosi.hosibackend.security.JwtProvider;
import com.hosi.hosibackend.service.IAuthService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {
    private final PasswordEncoder passwordEncoder;
    private final IUserRepo userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final IUserSession userSession;
    private final IRoleRepo roleRepository;
    private final AuthMailer authMailer;

    @Override
    public ResponseUser login(FormLogin formLogin, HttpServletRequest request) {
        User user = findByEmail(formLogin.getEmail());
        if (user == null) {
            throw new NotFoundException("can_not_find_user");
        }

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(formLogin.getEmail(), formLogin.getPassword()));
        } catch (AuthenticationException e) {
            throw new UnauthorizedException("email_or_password_not_valid");
        }


        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String accessToken = jwtProvider.generateAccessToken(userDetails);

        new UserSession();
        UserSession session = UserSession
        .builder()
        .token(accessToken)
        .ipAddress(RequestHelper.getIpAddress(request))
        .userAgent(request.getHeader("User-Agent"))
        .user(user)
        .isMobile(RequestHelper.isMobile(request)).build();
        userSession.save(session);

        return ResponseUser.builder()
                .email(user.getEmail())
                .authorities(userDetails.getAuthorities())
                .accessToken(accessToken)
                .build();
    }

    @Override
    public void signup(FormSignup formSignup) {
        if(!formSignup.getPassword().equals(formSignup.getConfirmPassword())) {
            throw new RuntimeException("invalid_confirm_password");
        }

        User user = findByEmail(formSignup.getEmail());
        if (user != null) {
            throw new RuntimeException("email_already");
        }
        Role role = roleRepository.findRoleByRole(RoleName.ROLE_USER);
        if(role == null) {
            throw new RuntimeException("role_user_not_found");
        }

        String token = UUID.randomUUID().toString();
        user = User.builder()
                .email(formSignup.getEmail())
                .password(passwordEncoder.encode(formSignup.getPassword()))
                .verifyToken(token)
                .build();

        user.setRoles(Set.of(role));
        userRepository.save(user);
        try {
            authMailer.sendActiveMail("vansy61@gmail.com", token);
        } catch (Exception e) {
            throw new RuntimeException("cannot_send_active_mail");
        }
    }

    @Override
    public void verifyEmail(String token) {
        User user = userRepository.findUserByVerifyToken(token).orElse(null);
        if(user == null) {
            throw new NotFoundException("token_not_found");
        }
        user.setActive(true);
        userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository.findUserByEmail(email).orElse(null);
    }

}
