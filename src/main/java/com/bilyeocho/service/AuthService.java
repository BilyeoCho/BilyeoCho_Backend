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
            log.error("회원가입 실패: 이미 존재하는 사용자 ID입니다 - {}", joinRequest.getUserId());
            throw new RuntimeException("User already exists with this ID: " + joinRequest.getUserId());
        }

        if (joinRequest.getUserName() == null || joinRequest.getUserName().isEmpty()) {
            log.error("회원가입 실패: 사용자 이름이 누락되었습니다.");
            throw new RuntimeException("User name is missing.");
        }
        if (joinRequest.getUserId() == null || joinRequest.getUserId().isEmpty()) {
            log.error("회원가입 실패: 사용자 ID가 누락되었습니다.");
            throw new RuntimeException("User ID is missing.");
        }

        // 사용자 생성
        User joinUser = User.builder()
                .userId(joinRequest.getUserId())
                .userName(joinRequest.getUserName())
                .userPassword(encoder.encode(joinRequest.getUserPwd()))
                .build();

        userRepository.save(joinUser);
        log.info("회원가입 성공: 사용자 ID - {}", joinRequest.getUserId());
    }

    public TokenInfo login(AuthRequest loginRequest, HttpServletResponse response) {
        User user = userRepository.findByUserId(loginRequest.getUserId())
                .orElseThrow(() -> {
                    log.error("로그인 실패: 사용자 ID를 찾을 수 없습니다 - {}", loginRequest.getUserId());
                    return new RuntimeException("User not found with this ID: " + loginRequest.getUserId());
                });

        if (!encoder.matches(loginRequest.getUserPwd(), user.getUserPassword())) {
            log.error("로그인 실패: 비밀번호가 일치하지 않습니다 - 사용자 ID: {}", loginRequest.getUserId());
            throw new RuntimeException("Incorrect password for user ID: " + loginRequest.getUserId());
        }

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequest.getUserId(), loginRequest.getUserPwd());
        Authentication authentication = authenticationManagerBuilder.authenticate(authenticationToken);

        log.info("로그인 성공: 사용자 ID - {}", loginRequest.getUserId());
        return jwtTokenProvider.generateToken(authentication, response);
    }

    public TokenInfo reissueToken(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        String refreshToken = "";

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refreshToken")) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if (refreshToken.isEmpty()) {
            log.error("토큰 갱신 실패: RefreshToken이 쿠키에 존재하지 않습니다.");
            throw new RuntimeException("Refresh token is missing from cookies.");
        }

        log.info("토큰 갱신 요청: RefreshToken - {}", refreshToken);
        return jwtTokenProvider.reissueToken(refreshToken, response);
    }
}