package com.bilyeocho.controller;

import com.bilyeocho.dto.request.UserUpdateRequest;
import com.bilyeocho.dto.response.UserUpdateResponse;
import com.bilyeocho.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PutMapping("/update")
    public ResponseEntity<UserUpdateResponse> updateUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UserUpdateRequest requestDTO) {
        String userId = userDetails.getUsername();

        UserUpdateResponse response = userService.updateUser(userId, requestDTO);

        return ResponseEntity.ok(response);
    }
}