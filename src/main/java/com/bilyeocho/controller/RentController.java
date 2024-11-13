package com.bilyeocho.controller;

import com.bilyeocho.dto.request.RentRequest;
import com.bilyeocho.dto.response.RentResponse;
import com.bilyeocho.service.RentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
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
    public ResponseEntity<RentResponse> returnItem(@PathVariable Long rentId) {
        RentResponse rentResponse = rentService.returnRent(rentId);
        return ResponseEntity.ok(rentResponse);
    }
}
