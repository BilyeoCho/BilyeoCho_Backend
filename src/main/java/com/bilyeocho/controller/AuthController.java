package com.bilyeocho.controller;

import com.bilyeocho.dto.request.AuthRequest;
import com.bilyeocho.jwt.TokenInfo;
import com.bilyeocho.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (예: 누락된 필드, 잘못된 데이터 형식)"),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 사용자 ID"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity join(@RequestBody AuthRequest joinRequest){
        authService.join(joinRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "유저 로그인")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (예: 비밀번호 불일치)"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자 (존재하지 않는 ID)"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<TokenInfo> login(@RequestBody AuthRequest loginRequest, HttpServletResponse response) {
        TokenInfo tokenInfo = authService.login(loginRequest, response);
        return ResponseEntity.ok(tokenInfo);
    }


    @PostMapping("/token/reissue")
    @Operation(summary = "토큰 재발급", description = "토큰 재발급")
    public ResponseEntity<TokenInfo> reissueToken(HttpServletRequest request, HttpServletResponse response) {
        TokenInfo newTokenInfo = authService.reissueToken(request, response);
        return ResponseEntity.ok(newTokenInfo);
    }
}
