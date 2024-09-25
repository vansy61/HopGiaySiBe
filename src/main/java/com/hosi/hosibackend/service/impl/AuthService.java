package com.hosi.hosibackend.service.impl;

import com.hosi.hosibackend.entity.*;
import com.hosi.hosibackend.entity.dto.FormLogin;
import com.hosi.hosibackend.entity.dto.FormSignup;
import com.hosi.hosibackend.entity.response.ResponseUser;
import com.hosi.hosibackend.exception.NotFoundException;
import com.hosi.hosibackend.exception.UnauthorizedException;
import com.hosi.hosibackend.helper.RequestHelper;
import com.hosi.hosibackend.mailer.AuthMailer;
import com.hosi.hosibackend.repo.IRoleRepo;
import com.hosi.hosibackend.repo.IUserOtpRepo;
import com.hosi.hosibackend.repo.IUserRepo;
import com.hosi.hosibackend.repo.IUserSessionRepo;
import com.hosi.hosibackend.security.CustomUserDetails;
import com.hosi.hosibackend.security.JwtProvider;
import com.hosi.hosibackend.service.IAuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {
    private final PasswordEncoder passwordEncoder;
    private final IUserRepo userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final IUserSessionRepo userSession;
    private final IRoleRepo roleRepository;
    private final AuthMailer authMailer;
    private final IUserSessionRepo userSessionRepo;
    private final UserDetailsService userDetailsService;
    private final IUserOtpRepo userOtpRepo;
    private static final int MAX_SESSIONS = 5;


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

        if(user.isEnableTwoFactor()) {
            generateAndSendTwoFactorOtp(user);
            return ResponseUser.builder()
                    .email(user.getEmail())
                    .needOtp(true)
                    .build();
        }

        return handleLoginSuccess(request, authentication, user);
    }

    private ResponseUser handleLoginSuccess(HttpServletRequest request, Authentication authentication, User user) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String accessToken = jwtProvider.generateAccessToken(userDetails);

        // check max session
        handleMaxSession(user);

        new UserSession();
        UserSession session = UserSession
                .builder()
                .token(accessToken)
                .ipAddress(RequestHelper.getIpAddress(request))
                .userAgent(request.getHeader("User-Agent"))
                .isRevoked(false)
                .user(user)
                .isMobile(RequestHelper.isMobile(request)).build();
        userSession.save(session);

        return ResponseUser.builder()
                .email(user.getEmail())
                .authorities(userDetails.getAuthorities())
                .accessToken(accessToken)
                .build();
    }

    private void handleMaxSession(User user) {
        List<UserSession> activeSessions = userSessionRepo.getUserSessionsByUser_IdAndIsRevokedOrderByIdAsc(user.getId(), false);
        if (activeSessions.size() >= MAX_SESSIONS) {
            UserSession oldestSession = activeSessions.get(0);
            oldestSession.setIsRevoked(true);
            userSessionRepo.save(oldestSession);
        }
    }

    private void generateAndSendTwoFactorOtp(User user) {
        Random random = new Random();
        int randomOtp = 1000 + random.nextInt(9000);
        UserOtp userOtp = UserOtp.builder()
                .user(user)
                .otp(String.valueOf(randomOtp))
                .expiredAt(LocalDateTime.now().plusMinutes(5))
                .isUsed(false)
                .build();
        userOtpRepo.save(userOtp);

        try {
            authMailer.sendOtpTwoFactor("vansy61@gmail.com", String.valueOf(randomOtp));
        } catch (Exception e) {
            throw new RuntimeException("cannot_send_otp_mail");
        }

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

    @Override
    public ResponseUser verifyTwoFactor(String email, String otp, HttpServletRequest request) {
        User user = findByEmail(email);
        if(user == null) {
            throw new NotFoundException("can_not_find_user");
        }

        UserOtp userOtp = userOtpRepo.findUserOtpByUser_IdOrderByIdDesc(user.getId()).get(0);

        if(userOtp == null || !userOtp.getOtp().equals(otp)) {
            throw new RuntimeException("otp_not_valid");
        }
        if(userOtp.getIsUsed()) {
            throw new RuntimeException("otp_used");
        }
        if(userOtp.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("otp_expired");
        }

        userOtp.setIsUsed(true);
        userOtpRepo.save(userOtp);

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
        return handleLoginSuccess(request, auth, user);
    }


    public User findByEmail(String email) {
        return userRepository.findUserByEmail(email).orElse(null);
    }

}
