package com.bilyeocho.controller;

import com.bilyeocho.dto.*;
import com.bilyeocho.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
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

    // 물품 조회 API
    @Operation(summary = "물품 조회", description = "물품아이디로 물품을 검색합니다")
    @GetMapping(value = "/item/{id}")
    public ResponseEntity<ItemSearchResponseDTO> getItemById(@PathVariable Long id) {
        ItemSearchResponseDTO item = itemService.getItemById(id);
        return ResponseEntity.ok(item);
    }

    // 모든 물품 조회 API
    @Operation(summary = "모든 물품 조회", description = "전체 물품을 조회합니다")
    @GetMapping(value = "/items")
    public ResponseEntity<List<ItemSearchResponseDTO>> getAllItems() {
        List<ItemSearchResponseDTO> item = itemService.getAllItems();
        return ResponseEntity.ok(item);
    }

    //물품 업데이트 API
    @Operation(summary = "물품 업데이트", description = "물품의 정보를 수정합니다")
    @PreAuthorize("isAuthenticated()")
    @PutMapping(value = "/update/{id}", consumes = { "multipart/form-data" })
    public ResponseEntity<ItemUpdateResponseDTO> updateItem(
            @PathVariable Long id,
            @ModelAttribute ItemUpdateRequestDTO requestDTO) {
        ItemUpdateResponseDTO updatedItem = itemService.updateItem(id, requestDTO);
        return ResponseEntity.ok(updatedItem);
    }

    // 물품 삭제 API
    @Operation(summary = "물품 삭제", description = "특정 ID의 물품을 삭제합니다.")
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
        return ResponseEntity.noContent().build(); // 상태 코드 204 반환
    }
}
