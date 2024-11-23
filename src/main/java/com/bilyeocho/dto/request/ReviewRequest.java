package com.bilyeocho.dto.request;

import com.bilyeocho.domain.enums.ReviewCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewRequest {

    private String rate;
    private ReviewCategory reviewCategory;
    private MultipartFile reviewPhoto;
    private String content;
    private Long userId;
    private Long itemId;
}
