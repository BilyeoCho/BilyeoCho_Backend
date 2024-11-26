package com.bilyeocho.controller;

import com.bilyeocho.dto.request.RentRequest;
import com.bilyeocho.dto.response.RentResponse;
import com.bilyeocho.service.RentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "대여", description = "물건 대여 및 반납")
@RequestMapping("/api/rents")
@RequiredArgsConstructor
public class RentController {

    private final RentService rentService;

    @PostMapping("/request")
    @Operation(summary = "대여 요청", description = "사용자가 물건 소유주에게 대여 요청")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "대여 요청 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (예: 물품 상태가 대여 가능하지 않음)"),
            @ApiResponse(responseCode = "404", description = "물품 또는 사용자를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<RentResponse> rentItem(@RequestBody RentRequest rentRequest) {
        RentResponse rentResponse = rentService.createRent(rentRequest);
        return ResponseEntity.ok(rentResponse);
    }

    @PutMapping("/return/{rentId}")
    @Operation(summary = "반납", description = "사용했던 물건을 반납")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "반납 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (예: 물품 반납 권한 없음)"),
            @ApiResponse(responseCode = "404", description = "대여 기록을 찾을 수 없음"),
            @ApiResponse(responseCode = "403", description = "해당 사용자는 대여하지 않은 물품을 반납하려고 함"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<RentResponse> returnItem(@PathVariable Long rentId, @RequestParam Long renterId) {
        RentResponse rentResponse = rentService.returnRent(rentId, renterId);
        return ResponseEntity.ok(rentResponse);
    }

    // 내가 빌린 물품 조회
    @GetMapping("/borrowed")
    @Operation(summary = "빌린 물품", description = "사용자가 빌린 물품 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "빌린 물품 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 요청입니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<List<RentResponse>> getBorrowedItems(@AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername(); // 현재 로그인한 사용자의 ID
        List<RentResponse> borrowedItems = rentService.getBorrowedItems(userId);
        return ResponseEntity.ok(borrowedItems);
    }

    // 내가 빌려준 물품 조회
    @GetMapping("/lent")
    @Operation(summary = "빌려준 물품", description = "사용자가 빌려준 물품 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "빌려준 물품 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 요청입니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<List<RentResponse>> getLentItems(@AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername(); // 현재 로그인한 사용자의 ID
        List<RentResponse> lentItems = rentService.getLentItems(userId);
        return ResponseEntity.ok(lentItems);
    }
}