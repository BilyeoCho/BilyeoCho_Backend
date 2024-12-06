package com.bilyeocho.service;

import com.bilyeocho.domain.Item;
import com.bilyeocho.domain.Review;
import com.bilyeocho.domain.User;
import com.bilyeocho.dto.request.ReviewRequest;
import com.bilyeocho.dto.response.ReviewResponse;
import com.bilyeocho.error.CustomException;
import com.bilyeocho.error.ErrorCode;
import com.bilyeocho.repository.ItemRepository;
import com.bilyeocho.repository.ReviewRepository;
import com.bilyeocho.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;
    private final ReviewRepository reviewRepository;
    private final UserAuthenticationService userAuthenticationService;

    @Transactional
    public void createReview(ReviewRequest reviewRequest, MultipartFile reviewPhoto) {
        String userId = userAuthenticationService.getAuthenticatedUserId();

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Item item = itemRepository.findById(reviewRequest.getItemId())
                .orElseThrow(() -> new CustomException(ErrorCode.ITEM_NOT_FOUND));

        String reviewPhotoUrl =  s3Service.uploadFile(reviewPhoto);

        Review review = Review.builder()
                .rate(reviewRequest.getRate())
                .reviewPhoto(reviewPhotoUrl)
                .content(reviewRequest.getContent())
                .user(user)
                .item(item)
                .build();

        reviewRepository.save(review);
    }
    @Transactional
    public ReviewResponse getReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

        return new ReviewResponse(
                review.getId(),
                review.getRate(),
                review.getReviewPhoto(),
                review.getContent(),
                review.getUser().getUsername(),
                review.getItem().getItemName()
        );
    }

    @Transactional
    public List<ReviewResponse> getAllReviewsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        List<Review> reviews = reviewRepository.findByUser(user);

        return reviews.stream()
                .map(review -> new ReviewResponse(
                        review.getId(),
                        review.getRate(),
                        review.getReviewPhoto(),
                        review.getContent(),
                        review.getUser().getUsername(),
                        review.getItem().getItemName()
                ))
                .collect(Collectors.toList());
    }


    @Transactional
    public List<ReviewResponse> getReviewsByItemId(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new CustomException(ErrorCode.ITEM_NOT_FOUND));

        List<Review> reviews = reviewRepository.findByItem(item);

        return reviews.stream()
                .map(review -> new ReviewResponse(
                        review.getId(),
                        review.getRate(),
                        review.getReviewPhoto(),
                        review.getContent(),
                        review.getUser().getUsername(),
                        review.getItem().getItemName()
                ))
                .collect(Collectors.toList());
    }


    @Transactional
    public void updateReview(Long reviewId, ReviewRequest reviewRequest, MultipartFile reviewPhoto) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

        String userId = userAuthenticationService.getAuthenticatedUserId();


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

    }

    @Transactional
    public void deleteReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

        if (review.getReviewPhoto() != null) {
            s3Service.deleteFile(review.getReviewPhoto());
        }

        reviewRepository.delete(review);
    }

    public List<ReviewResponse> getAllReviews(){
        List<Review> reviews = reviewRepository.findAll();

        return reviews.stream()
                .map(review -> new ReviewResponse(
                        review.getId(),
                        review.getRate(),
                        review.getReviewPhoto(),
                        review.getContent(),
                        review.getUser().getUsername(),
                        review.getItem().getItemName()
                ))
                .collect(Collectors.toList());
    }
}
