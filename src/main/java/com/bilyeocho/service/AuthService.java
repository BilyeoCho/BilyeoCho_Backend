package com.bilyeocho.service;

import com.bilyeocho.domain.User;
import com.bilyeocho.dto.request.AuthRequest;
import com.bilyeocho.error.CustomException;
import com.bilyeocho.error.ErrorCode;
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
public class AuthService  {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;
    private final AuthenticationManager authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;

    public void join(AuthRequest joinRequest){
        Optional<User> user = userRepository.findByUserId(joinRequest.getUserId());
        if(user.isPresent()){
            throw new CustomException(ErrorCode.ALREADY_EXIST_MEMBER);
        }

        // 이름, 이메일 추가 필요
        User joinUser = User.builder()
                .userId(joinRequest.getUserId())
                .userName(joinRequest.getUserName())
                .userPassword(encoder.encode(joinRequest.getUserPwd()))
                .build();

        userRepository.save(joinUser);
    }

    public TokenInfo login(AuthRequest loginRequest, HttpServletResponse response){
        User user = userRepository.findByUserId(loginRequest.getUserId()).orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));
        if(!encoder.matches(loginRequest.getUserPwd(), user.getUserPassword())){
            throw new CustomException(ErrorCode.MISMATCHED_PASSWORD);
        }
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginRequest.getUserId(), loginRequest.getUserPwd());
        Authentication authentication = authenticationManagerBuilder.authenticate(authenticationToken);
        return jwtTokenProvider.generateToken(authentication, response);
    }

    public TokenInfo reissueToken(HttpServletRequest request, HttpServletResponse response){
        Cookie[] cookies = request.getCookies();
        String refreshToken = "";

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refreshToken"))
                refreshToken = cookie.getValue();
        }
        return jwtTokenProvider.reissueToken(refreshToken, response);
    }
}
