package com.bilyeocho.dto.request;

import com.bilyeocho.domain.enums.ItemCategory;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRegistRequest {
    private MultipartFile itemPhoto;
    private ItemCategory itemCategory;
    private String itemName;
    private String itemDescription;
    //private String userId;
    private Integer price;
}
