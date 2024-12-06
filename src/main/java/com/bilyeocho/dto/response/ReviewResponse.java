package com.bilyeocho.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponse {
    private Long reviewId;
    private String rate;
    private String reviewPhoto;
    private String content;
    private String userId;
    private String itemName;
}

