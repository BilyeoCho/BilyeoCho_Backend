package com.bilyeocho.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor // 모든 필드를 포함하는 생성자 자동 생성
public class UserUpdateResponse {
    private String userId;
    private String userName;
    private String userPhoto;
    private String openKakaoLink; // 카카오톡 링크 추가
}
