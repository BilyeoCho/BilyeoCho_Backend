package com.bilyeocho.controller;

import com.bilyeocho.dto.request.ItemRegistRequest;
import com.bilyeocho.dto.request.ItemUpdateRequest;
import com.bilyeocho.dto.response.ItemRegistResponse;
import com.bilyeocho.dto.response.ItemSearchResponse;
import com.bilyeocho.dto.response.ItemUpdateResponse;
import com.bilyeocho.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "물품 등록", description = "사용자가 물품을 등록합니다.", security = {@SecurityRequirement(name = "bearerAuth")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "물품 등록 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (예: 누락된 필드)"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 요청입니다. 인증이 필요합니다."),
            @ApiResponse(responseCode = "500", description = "서버 내부에서 알 수 없는 오류가 발생했습니다.")
    })
    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/regist", consumes = { "multipart/form-data" })
    public ResponseEntity<ItemRegistResponse> registerItem(
            @ModelAttribute @Parameter(description = "물품 등록 요청 데이터", required = true) ItemRegistRequest requestDTO) {
        ItemRegistResponse responseDTO = itemService.registerItem(requestDTO, requestDTO.getItemPhoto());
        return ResponseEntity.ok(responseDTO);
    }

    @Operation(summary = "물품 조회",
            description = "특정 ID를 사용해 물품 정보 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "물품 조회 성공"),
            @ApiResponse(responseCode = "404", description = "ID에 해당하는 물품을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping(value = "/item/{id}")
    public ResponseEntity<ItemSearchResponse> getItemById(
            @Parameter(description = "물품 ID", example = "1", required = true) @PathVariable Long id) {
        ItemSearchResponse item = itemService.getItemById(id);
        return ResponseEntity.ok(item);
    }

    @Operation(summary = "모든 물품 조회", description = "전체 물품을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "물품 조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping(value = "/items")
    public ResponseEntity<List<ItemSearchResponse>> getAllItems() {
        List<ItemSearchResponse> item = itemService.getAllItems();
        return ResponseEntity.ok(item);
    }

    @Operation(summary = "물품 업데이트", description = "특정 ID의 물품 정보를 수정합니다.", security = {@SecurityRequirement(name = "bearerAuth")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "물품 업데이트 성공"),
            @ApiResponse(responseCode = "400", description = "데이터가 유효하지 않음."),
            @ApiResponse(responseCode = "403", description = "권한이 없음."),
            @ApiResponse(responseCode = "404", description = "물품을 찾을 수 없음."),
            @ApiResponse(responseCode = "500", description = "서버 오류.")
    })
    @PreAuthorize("isAuthenticated()")
    @PutMapping(value = "/update/{id}", consumes = { "multipart/form-data" })
    public ResponseEntity<ItemUpdateResponse> updateItem(
            @Parameter(description = "업데이트할 물품 ID", example = "1", required = true) @PathVariable Long id,
            @ModelAttribute @Parameter(description = "물품 업데이트 요청 데이터", required = true) ItemUpdateRequest requestDTO,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {

        String userId = userDetails.getUsername();
        ItemUpdateResponse updatedItem = itemService.updateItem(id, requestDTO, userId);
        return ResponseEntity.ok(updatedItem);
    }

    @Operation(summary = "물품 삭제",
            description = "특정 ID의 물품을 삭제합니다. 관리자는 모든 물품을 삭제할 수 있으며, 일반 사용자는 자신의 물품만 삭제할 수 있.",
            security = {@SecurityRequirement(name = "bearerAuth")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "물품 삭제 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "물품을 찾을 수 없음."),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteItem(
            @Parameter(description = "삭제할 물품 ID", example = "1", required = true) @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails currentUser) {
        itemService.deleteItem(id, currentUser.getUsername());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "최신 물품 조회",
            description = "최근 등록된 물품 4개를 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "최신 물품 조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/latest")
    public ResponseEntity<List<ItemSearchResponse>> getLatestItems() {
        List<ItemSearchResponse> latestItems = itemService.getLatestItems();
        return ResponseEntity.ok(latestItems);
    }

    @Operation(summary = "등록한 물품 조회",
            description = "현재 사용자 계정으로 등록된 물품 목록을 조회합니다.",
            security = {@SecurityRequirement(name = "bearerAuth")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "등록한 물품 조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/myitems")
    public ResponseEntity<List<ItemSearchResponse>> getMyItems(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername();
        List<ItemSearchResponse> items = itemService.getItemsByUserId(userId);
        return ResponseEntity.ok(items);
    }
}