package com.bilyeocho.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
public class UserUpdateRequest {
    private String currentPassword;
    private String newPassword;
    private String userName;
    private MultipartFile userPhoto;
    private String openKakaoLink;
}