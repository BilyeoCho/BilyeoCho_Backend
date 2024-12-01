package com.bilyeocho.controller;

import com.bilyeocho.dto.request.RentRequest;
import com.bilyeocho.dto.response.RentResponse;
import com.bilyeocho.service.RentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    @PostMapping("/rentstatus")
    @Operation(summary = "물품 상태 변경", description = "렌트 요청을 받은 물건 소유주가 물품 상태 변경")
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

    @Operation(summary = "렌트 요청", description = "대여를 원하는 사용자가 소유주에게 렌트 요청.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "렌트 요청 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (예: 필드 누락, 잘못된 데이터 형식)"),
            @ApiResponse(responseCode = "404", description = "아이템 또는 사용자 정보가 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/request")
    public ResponseEntity<RentResponse> makeRentRequest(@RequestBody RentRequest rentRequest) {

        RentResponse rentResponse = rentService.makeRentRequest(rentRequest);
        return new ResponseEntity<>(rentResponse, HttpStatus.OK);
    }
}