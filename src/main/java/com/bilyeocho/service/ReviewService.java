package com.bilyeocho.service;

import com.bilyeocho.domain.Item;
import com.bilyeocho.domain.Review;
import com.bilyeocho.domain.User;
import com.bilyeocho.dto.request.ReviewRequest;
import com.bilyeocho.dto.response.ReviewResponse;
import com.bilyeocho.repository.ItemRepository;
import com.bilyeocho.repository.ReviewRepository;
import com.bilyeocho.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;
    private final ReviewRepository reviewRepository;

    @Transactional
    public void createReview(ReviewRequest reviewRequest, MultipartFile reviewPhoto) {
        try {
            User user = userRepository.findByUserId(String.valueOf(reviewRequest.getUserId()))
                    .orElseThrow(() -> {
                        log.error("리뷰 생성 실패: User ID {}를 찾을 수 없습니다.", reviewRequest.getUserId());
                        return new RuntimeException("User not found with ID: " + reviewRequest.getUserId());
                    });

            Item item = itemRepository.findById(reviewRequest.getItemId())
                    .orElseThrow(() -> {
                        log.error("리뷰 생성 실패: Item ID {}를 찾을 수 없습니다.", reviewRequest.getItemId());
                        return new RuntimeException("Item not found with ID: " + reviewRequest.getItemId());
                    });

            String reviewPhotoUrl = s3Service.uploadFile(reviewPhoto);

            Review review = Review.builder()
                    .rate(reviewRequest.getRate())
                    .reviewPhoto(reviewPhotoUrl)
                    .reviewcategory(reviewRequest.getReviewCategory())
                    .content(reviewRequest.getContent())
                    .user(user)
                    .item(item)
                    .build();

            reviewRepository.save(review);
            log.info("리뷰 생성 성공: Review ID {}, User ID {}, Item ID {}", review.getId(), user.getId(), item.getId());
        } catch (RuntimeException e) {
            log.error("리뷰 생성 중 예외 발생: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Transactional
    public ReviewResponse getReview(Long reviewId) {
        try {
            Review review = reviewRepository.findById(reviewId)
                    .orElseThrow(() -> {
                        log.error("리뷰 조회 실패: Review ID {}를 찾을 수 없습니다.", reviewId);
                        return new RuntimeException("Review not found with ID: " + reviewId);
                    });

            log.info("리뷰 조회 성공: Review ID {}", review.getId());
            return new ReviewResponse(
                    review.getId(),
                    review.getRate(),
                    review.getReviewcategory(),
                    review.getReviewPhoto(),
                    review.getContent(),
                    review.getUser().getUsername(),
                    review.getItem().getItemName()
            );
        } catch (RuntimeException e) {
            log.error("리뷰 조회 중 예외 발생: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Transactional
    public List<ReviewResponse> getAllReviewsByUser(Long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> {
                        log.error("리뷰 조회 실패: User ID {}를 찾을 수 없습니다.", userId);
                        return new RuntimeException("User not found with ID: " + userId);
                    });

            List<Review> reviews = reviewRepository.findByUser(user);

            log.info("사용자 리뷰 조회 성공: User ID {}, {}건 반환", userId, reviews.size());
            return reviews.stream()
                    .map(review -> new ReviewResponse(
                            review.getId(),
                            review.getRate(),
                            review.getReviewcategory(),
                            review.getReviewPhoto(),
                            review.getContent(),
                            review.getUser().getUsername(),
                            review.getItem().getItemName()
                    ))
                    .collect(Collectors.toList());
        } catch (RuntimeException e) {
            log.error("사용자 리뷰 조회 중 예외 발생: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Transactional
    public List<ReviewResponse> getReviewsByItemId(Long itemId) {
        try {
            Item item = itemRepository.findById(itemId)
                    .orElseThrow(() -> {
                        log.error("리뷰 조회 실패: Item ID {}를 찾을 수 없습니다.", itemId);
                        return new RuntimeException("Item not found with ID: " + itemId);
                    });

            List<Review> reviews = reviewRepository.findByItem(item);

            log.info("아이템 리뷰 조회 성공: Item ID {}, {}건 반환", itemId, reviews.size());
            return reviews.stream()
                    .map(review -> new ReviewResponse(
                            review.getId(),
                            review.getRate(),
                            review.getReviewcategory(),
                            review.getReviewPhoto(),
                            review.getContent(),
                            review.getUser().getUsername(),
                            review.getItem().getItemName()
                    ))
                    .collect(Collectors.toList());
        } catch (RuntimeException e) {
            log.error("아이템 리뷰 조회 중 예외 발생: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Transactional
    public void updateReview(Long reviewId, ReviewRequest reviewRequest, MultipartFile reviewPhoto) {
        try {
            Review review = reviewRepository.findById(reviewId)
                    .orElseThrow(() -> {
                        log.error("리뷰 업데이트 실패: Review ID {}를 찾을 수 없습니다.", reviewId);
                        return new RuntimeException("Review not found with ID: " + reviewId);
                    });

            review.setRate(reviewRequest.getRate());
            review.setContent(reviewRequest.getContent());

            if (reviewPhoto != null && !reviewPhoto.isEmpty()) {
                if (review.getReviewPhoto() != null) {
                    s3Service.deleteFile(review.getReviewPhoto());
                }

                String reviewPhotoUrl = s3Service.uploadFile(reviewPhoto);
                review.setReviewPhoto(reviewPhotoUrl);
            }

            reviewRepository.save(review);
            log.info("리뷰 업데이트 성공: Review ID {}", review.getId());
        } catch (RuntimeException e) {
            log.error("리뷰 업데이트 중 예외 발생: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        try {
            Review review = reviewRepository.findById(reviewId)
                    .orElseThrow(() -> {
                        log.error("리뷰 삭제 실패: Review ID {}를 찾을 수 없습니다.", reviewId);
                        return new RuntimeException("Review not found with ID: " + reviewId);
                    });

            if (review.getReviewPhoto() != null) {
                s3Service.deleteFile(review.getReviewPhoto());
            }

            reviewRepository.delete(review);
            log.info("리뷰 삭제 성공: Review ID {}", review.getId());
        } catch (RuntimeException e) {
            log.error("리뷰 삭제 중 예외 발생: {}", e.getMessage(), e);
            throw e;
        }
    }
}