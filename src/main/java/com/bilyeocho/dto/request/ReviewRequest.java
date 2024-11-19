package com.bilyeocho.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewRequest {

    private String rate;
    private String reviewTitle;
    private MultipartFile reviewPhoto;
    private String content;
    private Long userId;
    private Long itemId;
}
