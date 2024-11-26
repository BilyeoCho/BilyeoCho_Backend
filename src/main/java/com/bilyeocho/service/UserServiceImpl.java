package com.bilyeocho.service;

import com.bilyeocho.domain.User;
import com.bilyeocho.dto.request.UserUpdateRequest;
import com.bilyeocho.dto.response.UserUpdateResponse;
import com.bilyeocho.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final S3Service s3Service;

    @Transactional
    @Override
    public UserUpdateResponse updateUser(String userId, UserUpdateRequest requestDTO) {
        try {
            User user = userRepository.findByUserId(userId)
                    .orElseThrow(() -> {
                        log.error("사용자 업데이트 실패: User ID {}를 찾을 수 없습니다.", userId);
                        return new RuntimeException("User not found with ID: " + userId);
                    });

            // 기존 비밀번호 검증
            if (requestDTO.getCurrentPassword() != null &&
                    !passwordEncoder.matches(requestDTO.getCurrentPassword(), user.getPassword())) {
                log.error("비밀번호 검증 실패: User ID {}", userId);
                throw new RuntimeException("Current password does not match for User ID: " + userId);
            }

            // 이름 변경
            if (requestDTO.getUserName() != null) {
                user.setUserName(requestDTO.getUserName());
                log.info("사용자 이름 업데이트: User ID {}, New Name {}", userId, requestDTO.getUserName());
            }

            // 비밀번호 변경
            if (requestDTO.getNewPassword() != null) {
                user.setUserPassword(passwordEncoder.encode(requestDTO.getNewPassword()));
                log.info("사용자 비밀번호 업데이트: User ID {}", userId);
            }

            // 프로필 사진 변경
            if (requestDTO.getUserPhoto() != null && !requestDTO.getUserPhoto().isEmpty()) {
                if (user.getUserPhoto() != null && !user.getUserPhoto().equals("none")) {
                    s3Service.deleteFile(user.getUserPhoto());
                    log.info("기존 프로필 사진 삭제: User ID {}, Photo URL {}", userId, user.getUserPhoto());
                }
                String photoUrl = s3Service.uploadFile(requestDTO.getUserPhoto());
                user.setUserPhoto(photoUrl);
                log.info("새 프로필 사진 업로드: User ID {}, Photo URL {}", userId, photoUrl);
            }

            userRepository.save(user);
            log.info("사용자 업데이트 성공: User ID {}", userId);

            return new UserUpdateResponse(
                    user.getUserId(),
                    user.getUserName(),
                    user.getUserPhoto()
            );
        } catch (RuntimeException e) {
            log.error("사용자 업데이트 중 예외 발생: {}", e.getMessage(), e);
            throw e;
        }
    }
}