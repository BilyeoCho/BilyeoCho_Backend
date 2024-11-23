package com.bilyeocho.dto.response;

import com.bilyeocho.domain.enums.ReviewCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponse {

    private Long id;
    private String rate;
    private ReviewCategory reviewCategory;
    private String reviewPhoto;
    private String content;
    private String userName;
    private String itemName;
}
