package com.bilyeocho.service;

import com.bilyeocho.domain.User;
import com.bilyeocho.dto.request.AuthRequest;
import com.bilyeocho.error.CustomException;
import com.bilyeocho.error.ErrorCode;
import com.bilyeocho.jwt.JwtTokenProvider;
import com.bilyeocho.jwt.TokenInfo;
import com.bilyeocho.repository.ItemRepository;
import com.bilyeocho.repository.RentRepository;
import com.bilyeocho.repository.ReviewRepository;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService  {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;
    private final AuthenticationManager authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RentRepository rentRepository;
    private final ItemRepository itemRepository;
    private final ReviewRepository reviewRepository;

    @Transactional
    public void join(AuthRequest joinRequest){
        Optional<User> user = userRepository.findByUserId(joinRequest.getUserId());
        if(user.isPresent()){
            throw new CustomException(ErrorCode.ALREADY_EXIST_MEMBER);
        }

        if (joinRequest.getUserName() == null || joinRequest.getUserName().isEmpty()) {
            throw new CustomException(ErrorCode.MISSING_USER_NAME);
        }
        if (joinRequest.getUserId() == null || joinRequest.getUserId().isEmpty()) {
            throw new CustomException(ErrorCode.MISSING_USER_ID);
        }


        User joinUser = User.builder()
                .userId(joinRequest.getUserId())
                .userName(joinRequest.getUserName())
                .userPassword(encoder.encode(joinRequest.getUserPwd()))
                .openKakaoLink("추가 바람")
                .build();

        userRepository.save(joinUser);
    }

    @Transactional
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

    @Transactional
    public void deleteUser(String userId) {

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        rentRepository.deleteByItemUser(user);
        reviewRepository.deleteByItemUser(user);
        itemRepository.deleteByUser(user);
        userRepository.delete(user);
        jwtTokenProvider.invalidateToken(userId);
    }

}
