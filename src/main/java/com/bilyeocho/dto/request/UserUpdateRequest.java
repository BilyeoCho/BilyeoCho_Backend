package com.bilyeocho.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UserUpdateRequest {
    private String currentPassword;
    private String newPassword;
    private String userName;
    private MultipartFile userPhoto;
}