package com.bilyeocho.controller;

import com.bilyeocho.dto.ItemRegistRequestDTO;
import com.bilyeocho.dto.ItemRegistResponseDTO;
import com.bilyeocho.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    // 물품 등록 API
    @Operation(summary = "물품 등록", description = "사용자가 물품을 등록합니다.", security = {@SecurityRequirement(name = "bearerAuth")})
    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/regist", consumes = { "multipart/form-data" })
    public ResponseEntity<ItemRegistResponseDTO> registerItem(@ModelAttribute ItemRegistRequestDTO requestDTO) {
        ItemRegistResponseDTO responseDTO = itemService.registerItem(requestDTO, requestDTO.getItemPhoto());
        return ResponseEntity.ok(responseDTO);
    }
}
