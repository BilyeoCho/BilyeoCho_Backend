package com.bilyeocho.controller;

import com.bilyeocho.dto.request.UserUpdateRequest;
import com.bilyeocho.dto.response.UserUpdateResponse;
import com.bilyeocho.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    @Operation(summary = "사용자 정보 업데이트", description = "사용자의 정보를 수정 후 업데이트")
    @PutMapping("/update")
    public ResponseEntity<UserUpdateResponse> updateUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @ModelAttribute UserUpdateRequest requestDTO) { // 변경
        String userId = userDetails.getUsername();

        UserUpdateResponse response = userService.updateUser(userId, requestDTO);
        return ResponseEntity.ok(response);
    }

}