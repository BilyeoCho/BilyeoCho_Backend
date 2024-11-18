package com.bilyeocho.controller;


import com.bilyeocho.dto.request.ReviewRequest;
import com.bilyeocho.dto.response.ReviewResponse;
import com.bilyeocho.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@Tag(name = "리뷰", description = "리뷰 작성, 불러오기, 수정, 삭제")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/write")
    @Operation(summary = "리뷰 작성", description = "사용자가 물품에 대해 리뷰를 작성")
    public ResponseEntity<String> writeReview(@ModelAttribute ReviewRequest reviewRequest, @RequestParam(value = "reviewPhoto", required = false) MultipartFile reviewPhoto) {
        reviewService.createReview(reviewRequest, reviewPhoto);
        return new ResponseEntity<>("리뷰 작성 성공", HttpStatus.OK);
    }

    @GetMapping("/{reviewId}")
    @Operation(summary = "리뷰 조회", description = "리뷰 ID로 리뷰를 조회")
    public ResponseEntity<ReviewResponse> getReview(@PathVariable Long reviewId) {
        ReviewResponse reviewResponse = reviewService.getReview(reviewId);
        return new ResponseEntity<>(reviewResponse, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "유저 리뷰 조회", description = "특정 사용자가 작성한 모든 리뷰 조회")
    public ResponseEntity<List<ReviewResponse>> getAllReviewsByUser(@PathVariable Long userId) {
        List<ReviewResponse> reviews = reviewService.getAllReviewsByUser(userId);
        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }

    @GetMapping("/item/{itemId}")
    @Operation(summary = "물품 리뷰 조회", description = "특정 물품에 대한 모든 리뷰 조회")
    public ResponseEntity<List<ReviewResponse>> getReviewsByItem(@PathVariable Long itemId) {
        List<ReviewResponse> reviews = reviewService.getReviewsByItemId(itemId);
        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }

    @PutMapping("/{reviewId}")
    @Operation(summary = "리뷰 수정", description = "리뷰를 수정")
    public ResponseEntity<String> updateReview(@PathVariable Long reviewId, @RequestBody ReviewRequest reviewRequest, @RequestParam(value = "reviewPhoto", required = false) MultipartFile reviewPhoto) {
        reviewService.updateReview(reviewId, reviewRequest, reviewPhoto);
        return new ResponseEntity<>("리뷰 수정 성공", HttpStatus.OK);
    }

    @DeleteMapping("/{reviewId}")
    @Operation(summary = "리뷰 삭제", description = "리뷰를 삭제")
    public ResponseEntity<String> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return new ResponseEntity<>("리뷰 삭제 성공", HttpStatus.OK);
    }
}
