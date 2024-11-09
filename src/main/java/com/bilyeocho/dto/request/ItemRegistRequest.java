package com.bilyeocho.dto.request;

import com.bilyeocho.domain.Category;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ItemRegistRequest {
    private MultipartFile itemPhoto;
    private Category category;
    private String itemName;
    private String itemDescription;
    private String userId;
    private Integer price;
}
