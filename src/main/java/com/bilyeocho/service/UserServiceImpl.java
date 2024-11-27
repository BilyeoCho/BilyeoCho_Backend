package com.bilyeocho.service;


import com.bilyeocho.domain.User;
import com.bilyeocho.dto.request.UserUpdateRequest;
import com.bilyeocho.dto.response.UserUpdateResponse;
import com.bilyeocho.error.CustomException;
import com.bilyeocho.error.ErrorCode;
import com.bilyeocho.repository.UserRepository;
import com.bilyeocho.service.S3Service;
import com.bilyeocho.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final S3Service s3Service;

    @Transactional
    @Override
    public UserUpdateResponse updateUser(String userId, UserUpdateRequest requestDTO) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 기존 비밀번호 검증
        if (requestDTO.getCurrentPassword() != null &&
                !passwordEncoder.matches(requestDTO.getCurrentPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.MISMATCHED_PASSWORD);
        }

        // 이름 변경
        if (requestDTO.getUserName() != null) { // DTO의 userName 사용
            user.setUserName(requestDTO.getUserName());
        }

        // 비밀번호 변경
        if (requestDTO.getNewPassword() != null) {
            user.setUserPassword(passwordEncoder.encode(requestDTO.getNewPassword()));
        }

        // 프로필 사진 변경
        if (requestDTO.getUserPhoto() != null && !requestDTO.getUserPhoto().isEmpty()) {
            if (user.getUserPhoto() != null && !user.getUserPhoto().equals("none")) {
                s3Service.deleteFile(user.getUserPhoto());
            }
            String photoUrl = s3Service.uploadFile(requestDTO.getUserPhoto());
            user.setUserPhoto(photoUrl);
        }

        if (requestDTO.getOpenKakaoLink() != null) {
            user.setOpenKakaoLink(requestDTO.getOpenKakaoLink());
        }

        userRepository.save(user);

        return new UserUpdateResponse(
                user.getUserId(),
                user.getUserName(),
                user.getUserPhoto(),
                user.getOpenKakaoLink()
        );
    }
}