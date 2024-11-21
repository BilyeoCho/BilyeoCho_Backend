package com.bilyeocho.service;

import com.bilyeocho.dto.request.UserUpdateRequest;
import com.bilyeocho.dto.response.UserUpdateResponse;

public interface UserService {
    UserUpdateResponse updateUser(String userId, UserUpdateRequest requestDTO);
}
