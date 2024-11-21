package com.bilyeocho.controller;

import com.bilyeocho.dto.request.RentRequest;
import com.bilyeocho.dto.response.RentResponse;
import com.bilyeocho.service.RentService;
import io.swagger.v3.oas.annotations.Operation;
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
    public ResponseEntity<RentResponse> rentItem(@RequestBody RentRequest rentRequest) {
        RentResponse rentResponse = rentService.createRent(rentRequest);
        return ResponseEntity.ok(rentResponse);
    }

    @PutMapping("/return/{rentId}")
    @Operation(summary = "반납", description = "사용했던 물건을 반납")
    public ResponseEntity<RentResponse> returnItem(@PathVariable Long rentId, @RequestParam Long renterId) {
        RentResponse rentResponse = rentService.returnRent(rentId, renterId);
        return ResponseEntity.ok(rentResponse);
    }

    // 내가 빌린 물품 조회
    @GetMapping("/borrowed")
    @Operation(summary = "빌린 물품", description = "사용자가 빌린 물품들")
    public ResponseEntity<List<RentResponse>> getBorrowedItems(@AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername(); // 현재 로그인한 사용자의 ID
        List<RentResponse> borrowedItems = rentService.getBorrowedItems(userId);
        return ResponseEntity.ok(borrowedItems);
    }

    // 내가 빌려준 물품 조회
    @GetMapping("/lent")
    @Operation(summary = "빌려준 물품", description = "사용자가 빌려준 물품")
    public ResponseEntity<List<RentResponse>> getLentItems(@AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername(); // 현재 로그인한 사용자의 ID
        List<RentResponse> lentItems = rentService.getLentItems(userId);
        return ResponseEntity.ok(lentItems);
    }
}
