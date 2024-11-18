package com.bilyeocho.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewRequest {

    private double rate;
    private String reviewTitle;
    private String reviewPhoto;
    private String content;
    private Long userId;
    private Long itemId;
}
