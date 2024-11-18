package com.bilyeocho.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponse {

    private Long id;
    private String rate;
    private String reviewTitle;
    private String reviewPhoto;
    private String content;
    private String userName;
    private String itemName;
}
