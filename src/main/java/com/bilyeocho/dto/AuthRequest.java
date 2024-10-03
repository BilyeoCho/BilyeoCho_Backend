package com.bilyeocho.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequest {

    // 이름, 이메일 추가 필요
    private String userId;
    private String userPwd;
}
