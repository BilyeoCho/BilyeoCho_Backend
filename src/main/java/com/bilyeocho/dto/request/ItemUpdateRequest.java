package com.bilyeocho.dto.request;

import com.bilyeocho.domain.enums.ItemCategory;
import com.bilyeocho.domain.enums.ItemStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ItemUpdateRequest {
    private String itemName;
    private MultipartFile itemPhoto;
    private ItemCategory itemCategory;
    private String itemDescription;
    private Integer price;
    private ItemStatus status;

}
