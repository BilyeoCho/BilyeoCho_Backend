package com.bilyeocho.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateResponse {
    private String userId;
    private String userName;
    private String userPhoto;
}