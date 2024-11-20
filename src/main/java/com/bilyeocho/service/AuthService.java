package com.bilyeocho.service;

import com.bilyeocho.domain.User;
import com.bilyeocho.dto.request.AuthRequest;
import com.bilyeocho.jwt.JwtTokenProvider;
import com.bilyeocho.jwt.TokenInfo;
import com.bilyeocho.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;
    private final AuthenticationManager authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;

    public void join(AuthRequest joinRequest) {
        Optional<User> user = userRepository.findByUserId(joinRequest.getUserId());
        if (user.isPresent()) {
            throw new RuntimeException("User already exists with the given ID.");
        }

        if (joinRequest.getUserName() == null || joinRequest.getUserName().isEmpty()) {
            throw new RuntimeException("User name is missing or empty.");
        }
        if (joinRequest.getUserId() == null || joinRequest.getUserId().isEmpty()) {
            throw new RuntimeException("User ID is missing or empty.");
        }

        // 이름, 이메일 추가 필요
        User joinUser = User.builder()
                .userId(joinRequest.getUserId())
                .userName(joinRequest.getUserName())
                .userPassword(encoder.encode(joinRequest.getUserPwd()))
                .build();

        userRepository.save(joinUser);
    }

    public TokenInfo login(AuthRequest loginRequest, HttpServletResponse response) {
        User user = userRepository.findByUserId(loginRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with the given ID."));

        if (!encoder.matches(loginRequest.getUserPwd(), user.getUserPassword())) {
            throw new RuntimeException("Password mismatch.");
        }

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequest.getUserId(), loginRequest.getUserPwd());
        Authentication authentication = authenticationManagerBuilder.authenticate(authenticationToken);
        return jwtTokenProvider.generateToken(authentication, response);
    }

    public TokenInfo reissueToken(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        String refreshToken = "";

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refreshToken"))
                refreshToken = cookie.getValue();
        }
        if (refreshToken.isEmpty()) {
            throw new RuntimeException("Refresh token is missing or invalid.");
        }
        return jwtTokenProvider.reissueToken(refreshToken, response);
    }
}