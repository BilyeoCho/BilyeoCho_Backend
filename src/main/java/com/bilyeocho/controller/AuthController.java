package com.bilyeocho.controller;

import com.bilyeocho.dto.request.AuthRequest;
import com.bilyeocho.jwt.TokenInfo;
import com.bilyeocho.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "인증", description = "유저 회원가입, 로그인, 토큰 재발급")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/join")
    @Operation(summary = "회원가입", description = "유저 회원가입")
    public ResponseEntity join(@RequestBody AuthRequest joinRequest){
        authService.join(joinRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "유저 로그인")
    public ResponseEntity<TokenInfo> login(@RequestBody AuthRequest loginRequest, HttpServletResponse response) {
        TokenInfo tokenInfo = authService.login(loginRequest, response);
        return ResponseEntity.ok(tokenInfo); // 로그인 성공 시 토큰 반환
    }


    @PostMapping("/token/reissue")
    @Operation(summary = "토큰 재발급", description = "토큰 재발급")
    public ResponseEntity<TokenInfo> reissueToken(HttpServletRequest request, HttpServletResponse response) {
        TokenInfo newTokenInfo = authService.reissueToken(request, response);
        return ResponseEntity.ok(newTokenInfo); // 새로운 토큰 반환
    }
}
