package com.bilyeocho.dto.request;

import com.bilyeocho.domain.Category;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ItemUpdateRequest {
    private String itemName;
    private MultipartFile itemPhoto;
    private Category category;
    private Integer rentalDuration;
    private String itemDescription;

}
