package com.bilyeocho.controller;

import com.bilyeocho.dto.request.ItemRegistRequest;
import com.bilyeocho.dto.request.ItemUpdateRequest;
import com.bilyeocho.dto.response.ItemRegistResponse;
import com.bilyeocho.dto.response.ItemSearchResponse;
import com.bilyeocho.dto.response.ItemUpdateResponse;
import com.bilyeocho.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<ItemRegistResponse> registerItem(@ModelAttribute ItemRegistRequest requestDTO) {
        ItemRegistResponse responseDTO = itemService.registerItem(requestDTO, requestDTO.getItemPhoto());
        return ResponseEntity.ok(responseDTO);
    }

    // 물품 조회 API
    @Operation(summary = "물품 조회", description = "물품아이디로 물품을 검색합니다")
    @GetMapping(value = "/item/{id}")
    public ResponseEntity<ItemSearchResponse> getItemById(@PathVariable Long id) {
        ItemSearchResponse item = itemService.getItemById(id);
        return ResponseEntity.ok(item);
    }

    // 모든 물품 조회 API
    @Operation(summary = "모든 물품 조회", description = "전체 물품을 조회합니다")
    @GetMapping(value = "/items")
    public ResponseEntity<List<ItemSearchResponse>> getAllItems() {
        List<ItemSearchResponse> item = itemService.getAllItems();
        return ResponseEntity.ok(item);
    }

    // 물품 업데이트 API
    @Operation(summary = "물품 업데이트", description = "물품의 정보를 수정합니다")
    @PreAuthorize("isAuthenticated()")
    @PutMapping(value = "/update/{id}", consumes = { "multipart/form-data" })
    public ResponseEntity<ItemUpdateResponse> updateItem(
            @PathVariable Long id,
            @ModelAttribute ItemUpdateRequest requestDTO,
            @AuthenticationPrincipal UserDetails userDetails) {

        String userId = userDetails.getUsername();
        ItemUpdateResponse updatedItem = itemService.updateItem(id, requestDTO, userId);
        return ResponseEntity.ok(updatedItem);
    }

    // 물품 삭제 API
    @Operation(summary = "물품 삭제", description = "특정 ID의 물품을 삭제합니다.")
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id, @AuthenticationPrincipal UserDetails currentUser) {
        itemService.deleteItem(id, currentUser.getUsername()); // getUsername() 메서드로 userId 추출
        return ResponseEntity.noContent().build(); // 상태 코드 204 반환
    }
}
